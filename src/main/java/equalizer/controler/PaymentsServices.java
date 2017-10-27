package equalizer.controler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import equalizer.EqualizerConfiguration;
import equalizer.controlermodel.Error;
import equalizer.controlermodel.Node;
import equalizer.controlermodel.Row;
import equalizer.controlermodel.Constants.ErrorCode;
import equalizer.controlermodel.Constants.ErrorType;
import equalizer.model.Activity;
import equalizer.model.Payments;
import equalizer.model.Person;
import equalizer.repository.ActivityRepository;
import equalizer.repository.PaymentsRepository;
import equalizer.repository.PersonRepository;
import equalizer.viewmodel.PaymentOut;


@RestController
public class PaymentsServices {
	
	@Autowired
	private ActivityRepository activityRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private PaymentsRepository paymentsRepo;
	
	@Autowired
	private EqualizerConfiguration eConf;
	
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
	
	private List<Payments> calculatePayments (Activity act, Person per, boolean redo) {
		List<Payments> generatedPayments = new ArrayList<>();
		//* eliminar pagos previos en caso de recalculo
		List<Payments> deletedPayments = null;
		if (redo) {
			deletedPayments = paymentsRepo.removeByActivity(act);
		}
		
		//*/
		//* construir tabla para determinar diferencias
		ArrayList<Row> table = new ArrayList<>();
		act.getParticipants().forEach(p->{
			Row row = new Row();
			row.personId = p.getId();
			row.numPersons = p.getNumpers();
			row.totalPaid = 0;
			act.getTasks().forEach(t->{
				if (row.personId == t.getOwner().getId()) {
					row.totalPaid += t.getAmmount();
				}
			});
			table.add(row);
		});
		//*/
		//* calcular diferencias y totales
		if (table.isEmpty()) {
			eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.TASK_NOT_FOUND, "Zero tasks found for this activity");
			return generatedPayments;
		}
		int totalPers = 0;
		double totalPaid = 0;
		long minId = table.get(0).personId;
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
				minId = r.personId;
			}
		}
		for (Row r : table) {
			if (r.personId == minId) r.difference = round2decimals(r.difference - penalty);
			if (r.difference < 0) negatives.add(new Node(r.personId, -r.difference));
			if (r.difference > 0) positives.add(new Node(r.personId, r.difference));
		}
		//*/
		//* determinar pagos
		Collections.sort(positives);
		Collections.sort(negatives);
		while (!positives.isEmpty() && !negatives.isEmpty()) {
			Node p = positives.peek();
			Node n = negatives.peek();
			Payments pay = new Payments();
			pay.setActivity(act);
			pay.setFrom(personRepo.findById(n.personId));
			pay.setTo(personRepo.findById(p.personId));
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
			generatedPayments.add(pay);
			if (!act.isCalculated() || redo) {
				paymentsRepo.save(pay);
			}
			//paymentsOut.add(new PaymentsOut(pay));
			//sb.append((new PaymentsOut(pay)) + "\n");
		}
		if (!positives.isEmpty() || !negatives.isEmpty()) {
			eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PAYMENTSLISTS_MISSMATCH, "Positives and Negatives lists do not match");
			return generatedPayments;
		}
		//*/
		return generatedPayments;
	}
	
	@RequestMapping(value="/generatepayments", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PaymentOut> generatePayments(@RequestParam(value="aId", defaultValue="") String activityId, @RequestParam(value="idp", defaultValue="") String personId, @RequestParam(value="redo", defaultValue="false") boolean reDo) {
    	
		Activity act = activityRepo.findById(Long.decode(activityId));
		Person per = personRepo.findById(Long.decode(personId));
		if (act != null && per!= null && per.getId() == act.getOwner().getId()) {
			
			ArrayList<PaymentOut> paymentsOutList = new ArrayList<>();
			calculatePayments(act, per, reDo).forEach(p->paymentsOutList.add(new PaymentOut(p)));
						
			act.setCalculated(true);
			activityRepo.save(act);
			
			return paymentsOutList;
		}
		
		eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.UNKNOWN, "Unknown");
		if (act == null) {
			eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity Id not found");
		}
		if (per == null) {
			eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person Id not found");
		}
		if (per.getId() == act.getOwner().getId()) {
			eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ACTIVITYOWNER_MISSMATCH, "Person Id is different than actuvity owner Id");
		}
		
		return null;
    }
	
	@RequestMapping(value="/testpayments", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PaymentOut> testPayments(@RequestParam(value="aId", defaultValue="") String activityId) {
    	
		Activity act = activityRepo.findById(Long.decode(activityId));
		if (act != null) {
			ArrayList<PaymentOut> paymentsOutList = new ArrayList<>();
			calculatePayments(act, null, false).forEach(p->paymentsOutList.add(new PaymentOut(p)));
			return paymentsOutList;
		}
		
		eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found");
		return null;
    }
	
	@RequestMapping(value="/paymentsbyact", method=RequestMethod.GET)
    public List<PaymentOut> testPaymentsByActivity(@RequestParam(value="aId", defaultValue="") String activityId) {
    	
		List<Payments> paymentsList = paymentsRepo.findByActivityId(Long.decode(activityId));
		ArrayList<PaymentOut> paymentsOutList = new ArrayList<>();
		
		paymentsList.forEach(p->paymentsOutList.add(new PaymentOut(p)));		
		
		return paymentsOutList;
    }
	
	@RequestMapping(value="/deletepaymentsbyactid", method=RequestMethod.GET)
    public String deletePaymentsByActivityId(@RequestParam(value="aId", defaultValue="") String activityId) {
    	
		try {
			paymentsRepo.removeByActivityId(Long.decode(activityId));
		}	catch (Exception e) {
			return e.getMessage();
		}	
		
		return "OK";
    }
	
	@RequestMapping(value="/paymentsbyact", method=RequestMethod.DELETE)
    public List<PaymentOut> deletePaymentsByActivity(@RequestParam(value="aId", defaultValue="") String activityId) {
    	
		Activity activity = activityRepo.findById(Long.decode(activityId));
		if (activity != null) {
			List<Payments> paymentsList =  paymentsRepo.removeByActivity(activity);
			activity.setCalculated(true);
			activityRepo.save(activity);
			ArrayList<PaymentOut> paymentsOutList = new ArrayList<>();
			paymentsList.forEach(p->paymentsOutList.add(new PaymentOut(p)));		
			
			return paymentsOutList;
		}
		eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found");
		return null;
    }
	
	@RequestMapping(value="/paymentsbyfrom", method=RequestMethod.GET)
    public List<PaymentOut> paymentsByFrom(@RequestParam(value="fId", defaultValue="") String fromId) {
    	
		Person person = personRepo.findById(Long.decode(fromId));
		if (person != null) {
			List<Payments> paymentsList = paymentsRepo.findByFrom(person);
			if (paymentsList != null) {
				List<PaymentOut> paymentsOutList = new ArrayList<>();
				paymentsList.forEach(p->paymentsOutList.add(new PaymentOut(p)));
				return paymentsOutList;
			}	else {
				eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found");
			}
		} 	else {
			eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Owner not found");
		}
				
		return null;
    }
	
	@RequestMapping(value="/paymentsbyto", method=RequestMethod.GET)
    public List<PaymentOut> paymentsByTo(@RequestParam(value="tId", defaultValue="") String toId) {
    	
		Person person = personRepo.findById(Long.decode(toId));
		if (person != null) {
			List<Payments> paymentsList = paymentsRepo.findByTo(person);
			if (paymentsList != null) {
				List<PaymentOut> paymentsOutList = new ArrayList<>();
				paymentsList.forEach(p->paymentsOutList.add(new PaymentOut(p)));
				return paymentsOutList;
			}	else {
				eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found");
			}
		} 	else {
			eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Owner not found");
		}
				
		return null;
    }
	
	@RequestMapping(value="/lasterror", method=RequestMethod.GET)
    public Error getLastError() {
		return eConf.lastError();
    }
}
