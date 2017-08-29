package equalizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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
	
	public double round2decimals (double val) {
		double result = val*100;
		result = (double)((int) result);
		result = result /100;
		return result;
	}
	
	@RequestMapping(value="/generatepayments", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public String generatePayments(@RequestParam(value="ida", defaultValue="") String idActivity, @RequestParam(value="idp", defaultValue="") String idPerson) {
    	
		Activity act = activityRepo.findById(Long.decode(idActivity));
		Person per = personRepo.findById(Long.decode(idPerson));
		if (act != null && per!= null && per.getId() == act.getOwner().getId()) {
			
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
			if (table.isEmpty()) return "No tasks";
			int totalPers = 0;
			double totalPaid = 0;
			long maxId = table.get(0).idPerson;
			long minId = table.get(0).idPerson;
			double maxPaid = table.get(0).totalPaid;
			double minPaid = table.get(0).totalPaid;
			for (Row r : table) {
				totalPers += r.numPersons;
				totalPaid += r.totalPaid;
				if (r.totalPaid > maxPaid) {
					maxPaid = r.totalPaid;
					maxId = r.idPerson;
				}
				if (r.totalPaid < minPaid) {
					minPaid = r.totalPaid;
					minId = r.idPerson;
				}
			}
			act.setTotal(totalPaid);
			double paidPerPerson = round2decimals(totalPaid / totalPers);
			double penalty = totalPaid - paidPerPerson * totalPers;
			Stack<Node> positives = new Stack<>();
			Stack<Node> negatives = new Stack<>();
			for (Row r : table) {
				r.paidPerPerson = r.numPersons * paidPerPerson;
				r.difference = r.totalPaid - r.paidPerPerson;
				if (r.idPerson == maxId) r.difference += penalty;
				if (r.idPerson == minId) r.difference -= penalty; 
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
				if (n.ammount <= p.ammount) {
					pay.setAmmount(n.ammount);
					positives.peek().ammount -= n.ammount;
					negatives.pop();
					if (n.ammount == p.ammount) {
						positives.pop();
					}
				}	else {
					pay.setAmmount(p.ammount);
					negatives.peek().ammount -= p.ammount;
					positives.pop();
				}
				paymentsRepo.save(pay);
			}
			
		}
		
		//p.setEmail("loquesea@algo.com");
		//person.save(p);
		
		return "error";
    }
	
}
