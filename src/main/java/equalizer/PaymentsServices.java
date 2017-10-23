package equalizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tmp_model.Node;
import tmp_model.Row;


@RestController
public class PaymentsServices {
	
	@Autowired
	private ActivityRepository activityRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private PaymentsRepository paymentsRepo;
	
	//private EntityManager em;
	
	public double trunc2decimals (double val) {
		double result = val*100;
		result = (double)((int) result);
		result = result /100;
		return result;
	}
	
	public double round2decimals (double val) {
		double result = val*100;
		result = Math.round(result);
		result = result /100;
		return result;
	}
	
	private StringBuffer calculatePayments (Activity act, Person per, boolean redo) {
		StringBuffer sb = new StringBuffer();
		
		// eliminar pagos previos en caso de recalculo
		/*if (redo) {
			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory( "org.hibernate.tutorial.jpa" );
			em = entityManagerFactory.createEntityManager();
			Query query = em.createQuery("DELETE FROM Payments p WHERE p.activity = ?1");
			int deletedCount = query.setParameter(1, act.getId()).executeUpdate();
		} // */
		
		// construir tabla para determinar diferencias
		ArrayList<Row> table = new ArrayList<>();
		act.getParticipants().forEach(p->{
			Row row = new Row();
			row.idPerson = p.getId();
			row.numPersons = p.getNumpers();
			row.totalPaid = 0;
			act.getTasks().forEach(t->{
				if (row.idPerson == t.getOwner().getId()) {
					row.totalPaid += t.getAmmount();
				}
			});
			table.add(row);
		});
		
		// calcular diferencias y totales
		if (table.isEmpty()) return sb.append("No tasks\n");
		int totalPers = 0;
		double totalPaid = 0;
		long minId = table.get(0).idPerson;
		double minPaid = table.get(0).totalPaid / table.get(0).numPersons;
		for (Row r : table) {
			totalPers += r.numPersons;
			totalPaid += r.totalPaid;
		}
		act.setTotal(totalPaid);
		double paidPerPerson = trunc2decimals(totalPaid / totalPers);
		double penalty = round2decimals(totalPaid - paidPerPerson * totalPers);
		Stack<Node> positives = new Stack<>();
		Stack<Node> negatives = new Stack<>();
		for (Row r : table) {
			r.paidPerPerson = r.numPersons * paidPerPerson;
			r.difference = round2decimals(r.totalPaid - r.paidPerPerson);
			if (r.totalPaid / r.numPersons < minPaid) {
				minPaid = r.totalPaid / r.numPersons;
				minId = r.idPerson;
			}
		}
		for (Row r : table) {
			if (r.idPerson == minId) r.difference = round2decimals(r.difference - penalty);
			if (r.difference < 0) negatives.add(new Node(r.idPerson, -r.difference));
			if (r.difference > 0) positives.add(new Node(r.idPerson, r.difference));
		}

		// determinar pagos
		Collections.sort(positives);
		Collections.sort(negatives);
		while (!positives.isEmpty() && !negatives.isEmpty()) {
			Node p = positives.peek();
			Node n = negatives.peek();
			Payments pay = new Payments();
			pay.setActivity(act);
			pay.setFrom(personRepo.findById(n.idPerson));
			pay.setTo(personRepo.findById(p.idPerson));
			if (n.ammount < p.ammount) {
				pay.setAmmount(n.ammount);
				positives.peek().ammount = round2decimals(positives.peek().ammount - n.ammount);
				negatives.pop();
			}	else if (n.ammount > p.ammount) {
				pay.setAmmount(p.ammount);
				negatives.peek().ammount = round2decimals(negatives.peek().ammount - p.ammount);
				positives.pop();
			}	else { // son iguales
				pay.setAmmount(n.ammount);
				positives.pop();
				negatives.pop();
			}
			// persistir
			if (!act.isCalculated()) {
				paymentsRepo.save(pay);
			}	/*else if (redo) {
				paymentsRepo.
			}*/
			sb.append(pay.toStr() + "\n");
		}
		if (!positives.isEmpty() || !negatives.isEmpty()) sb.append("Error. One list isn't empty\n");
		
		return sb;
	}
	
	@RequestMapping(value="/generatepayments", method=RequestMethod.GET, produces="application/text;charset=UTF-8")
    public String generatePayments(@RequestParam(value="ida", defaultValue="") String idActivity, @RequestParam(value="idp", defaultValue="") String idPerson, @RequestParam(value="redo", defaultValue="false") boolean reDo) {
    	
		Activity act = activityRepo.findById(Long.decode(idActivity));
		Person per = personRepo.findById(Long.decode(idPerson));
		if (act != null && per!= null && per.getId() == act.getOwner().getId()) {
			
			StringBuffer sb = calculatePayments(act, per, reDo);
						
			act.setCalculated(true);
			activityRepo.save(act);
			return sb.toString();
		}
		
		return "Unknown error\n";
    }
	
	@RequestMapping(value="/testpayments", method=RequestMethod.GET, produces="application/text;charset=UTF-8")
    public String testPayments(@RequestParam(value="ida", defaultValue="") String idActivity) {
    	
		Activity act = activityRepo.findById(Long.decode(idActivity));
		if (act != null) {
			
			StringBuffer sb = calculatePayments(act, null, false);
			
			return sb.toString();
		}
		
		return "Unknown error\n";
    }
	
	@RequestMapping(value="/testpaymentsbyact", method=RequestMethod.GET)
    public Payments testPaymentsByActivity(@RequestParam(value="ida", defaultValue="") String idActivity) {
    	
		List<Payments> lp1 = paymentsRepo.findByActivityId(Long.decode(idActivity));
		
		Activity act = activityRepo.findById(Long.decode(idActivity));
		if (act != null) {
			return paymentsRepo.findByActivity(act).get(0);
		}
			
		
		return null;
    }
	
}
