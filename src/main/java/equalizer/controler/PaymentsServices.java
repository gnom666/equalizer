package equalizer.controler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import equalizer.config.EqualizerConfiguration;
import equalizer.controlermodel.Constants.ErrorCode;
import equalizer.controlermodel.Constants.ErrorType;
import equalizer.controlermodel.Constants.PaymentStatus;
import equalizer.controlermodel.Error;
import equalizer.controlermodel.Node;
import equalizer.controlermodel.Row;
import equalizer.controlermodel.Table;
import equalizer.model.Activity;
import equalizer.model.Payment;
import equalizer.model.Person;
import equalizer.model.Task;
import equalizer.repository.ActivityRepository;
import equalizer.repository.PaymentsRepository;
import equalizer.repository.PersonRepository;
import equalizer.repository.TaskRepository;
import equalizer.viewmodel.PaymentOut;

/**
 * Payment Services
 * @author jorgerios
 *
 */
@RestController
@RequestMapping("/payments")
public class PaymentsServices {
	
	@Autowired
	private ActivityRepository activityRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private PaymentsRepository paymentsRepo;
	
	@Autowired
	private TaskRepository taskRepo;
	
	@Autowired
	private EqualizerConfiguration eConf;
	
	/**
	 * Truncates a value to two decimals
	 * @param val Value to truncate
	 * @return double
	 */
	public double trunc2decimals (double val) {
		double result = val*100;
		result = (double)((int) result);
		result = result /100;
		return result;
	}
	
	/**
	 * Rounds a value to two decimals
	 * @param val Value to round
	 * @return double
	 */
	public double round2decimals (double val) {
		double result = val*100;
		result = Math.round(result);
		result = result /100;
		return result;
	}
		
	/**
	 * Calculates all payments for an activity
	 * @param act The activity
	 * @param per The person that owns the activity
	 * @param redo Whether or not re-do calculations
	 * @return List of Payment
	 */
	private List<Payment> calculatePayments (Activity act, Person per, boolean redo) {
		
		List<Payment> generatedPayments = new ArrayList<>();
		
		//* eliminate previous payments if redo
		//List<Payment> deletedPayments = null;
		// a new payment not calculated is a reason for redo
		for (Task t : act.getTasks()) {
			if (!t.isCalculated())
				redo = true;
		}
		if (redo) {
			/*deletedPayments = */paymentsRepo.removeByActivity(act);
		}
		
		//*/
		//* build differences table
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
		//* calculate differences and totals
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
			if (r.numPersons == 0) {
				eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ZERO_PERSONS, "Zero persons represented by: " + r.personId);
				return generatedPayments;
			}
		}
		if (totalPers == 0) {
			eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ZERO_PERSONS, "Zero persons found for activity: " + act.getId());
			return generatedPayments;
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
		System.out.println((new Table (table)).formatedString());
		//*/
		//* determine payments
		Collections.sort(positives);
		Collections.sort(negatives);
		while (!positives.isEmpty() && !negatives.isEmpty()) {
			Node p = positives.peek();
			Node n = negatives.peek();
			Payment pay = new Payment();
			pay.setStatus(PaymentStatus.PENDING);
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
			// persist
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
	
	/**
	 * Generates an activity's payments
	 * @param activityId The Id of the activity
	 * @param personId The Id of the activity's owner
	 * @param reDo Re-do or not the calculations
	 * @return List of PaymentOut
	 */
	@RequestMapping(value="/generatepayments", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PaymentOut> generatePayments(@RequestParam(value="aId", defaultValue="0") long activityId, 
								    		 @RequestParam(value="pId", defaultValue="") long personId, 
								    		 @RequestParam(value="redo", defaultValue="false") boolean reDo) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		List<PaymentOut> result = new ArrayList<>();
		Activity act = activityRepo.findById(activityId);
		Person per = personRepo.findById(personId);
		if (act != null && per!= null && per.getEmail() == act.getOwner().getEmail()) {
			
			calculatePayments(act, per, reDo).forEach(p->result.add(new PaymentOut(p)));
						
			act.setCalculated(true);
			activityRepo.save(act);
			
			act.getTasks().forEach(t->{
				t.setCalculated(true);
				taskRepo.save(t);
			});
		}
		
		if (act == null) {
			result.add(	new PaymentOut(
					new Payment()
					.setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity Id not found"))));
		}	else if (per == null) {
			result.add(	new PaymentOut(
					new Payment()
					.setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person Id not found"))));
		}	else if (per.getEmail() != act.getOwner().getEmail()) {
			result.add(	new PaymentOut(
					new Payment()
					.setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ACTIVITYOWNER_MISSMATCH, "Person Id is different than actuvity owner Id"))));
		}
		
		return result;
    }
	
	/**
	 * Tests the calculations of an activity's payments without persisting them
	 * @param activityId The Id of the activity
	 * @return List of PaymentOut
	 */
	@RequestMapping(value="/testpayments", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PaymentOut> testPayments(@RequestParam(value="aId", defaultValue="0") long activityId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		List<PaymentOut> result = new ArrayList<>();
		Activity act = activityRepo.findById(activityId);
		if (act != null) {
			calculatePayments(act, null, false).forEach(p->result.add(new PaymentOut(p)));
		}	else {
			result.add(	new PaymentOut(
					new Payment()
					.setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found"))));
		}
		
		return result;
    }
	
	/**
	 * Lists all the payments of an activity
	 * @param activityId The Id of the activity
	 * @return List of PaymentOut
	 */
	@RequestMapping(value="/paymentsbyact", method=RequestMethod.GET)
    public List<PaymentOut> testPaymentsByActivity(@RequestParam(value="aId", defaultValue="0") String activityId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		List<Payment> paymentsList = paymentsRepo.findByActivityId(Long.decode(activityId));
		ArrayList<PaymentOut> paymentsOutList = new ArrayList<>();
		
		paymentsList.forEach(p->paymentsOutList.add(new PaymentOut(p)));		
		
		return paymentsOutList;
    }
	
	/**
	 * Deletes all the payments of an activity
	 * @param activityId The Id of the activity
	 * @return List of PaymentOut
	 */
	@RequestMapping(value="/paymentsbyact", method=RequestMethod.DELETE)
    public List<PaymentOut> deletePaymentsByActivity(@RequestParam(value="aId", defaultValue="0") long activityId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		List<PaymentOut> result = new ArrayList<>();
		Activity activity = activityRepo.findById(activityId);
		if (activity != null) {
			List<Payment> paymentsList =  paymentsRepo.removeByActivity(activity);
			activity.setCalculated(false);
			activityRepo.save(activity);
			activity.getTasks().forEach(t->{
				t.setCalculated(false);
				taskRepo.save(t);
			});
			paymentsList.forEach(p->result.add(new PaymentOut(p)));		
		}	else {
			result.add(	new PaymentOut(
					new Payment()
					.setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found"))));
		}
		
		return result;
    }
	
	/**
	 * Lists all the payments from a person
	 * @param fromId The Id of the person
	 * @return List of PaymentOut
	 */
	@RequestMapping(value="/paymentsbyfrom", method=RequestMethod.GET)
    public List<PaymentOut> paymentsByFrom(@RequestParam(value="fId", defaultValue="0") long fromId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		List<PaymentOut> result = new ArrayList<>();
		Person person = personRepo.findById(fromId);
		if (person != null) {
			List<Payment> paymentsList = paymentsRepo.findByFrom(person);
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
				
		return result;
    }
	
	/**
	 * Lists all the payments to a person
	 * @param toId The Id of the person
	 * @return List of PaymentOut
	 */
	@RequestMapping(value="/paymentsbyto", method=RequestMethod.GET)
    public List<PaymentOut> paymentsByTo(@RequestParam(value="tId", defaultValue="0") long toId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		List<PaymentOut> result = new ArrayList<>();
		Person person = personRepo.findById(toId);
		if (person != null) {
			List<Payment> paymentsList = paymentsRepo.findByTo(person);
			if (paymentsList != null) {
				paymentsList.forEach(p->result.add(new PaymentOut(p)));
			}	else {
				result.add(	new PaymentOut(
								new Payment()
								.setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found"))));
			}
		} 	else {
			result.add(	new PaymentOut(
					new Payment()
					.setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Owner not found"))));
		}
				
		return result;
    }
	
	/**
	 * Sets the payment status to Requested. The 'from' person supposedly made the payment
	 * @param fromId The Id of the person that should made the payment
	 * @param paymentId The Id of the payment
	 * @return PaymentOut
	 */
	@RequestMapping(value="/makepay", method=RequestMethod.GET)
    public PaymentOut makePayment(@RequestParam(value="fId", defaultValue="0") long fromId, 
    							  @RequestParam(value="pId", defaultValue="") long paymentId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Person person = personRepo.findById(fromId);
		Payment payment = paymentsRepo.findById(paymentId);
		if (person != null && payment != null) {
			if (payment.getFrom().getEmail() == person.getEmail()) {
				payment.setStatus(PaymentStatus.REQUESTED);
				paymentsRepo.save(payment);
			}	else {
				payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PAYMENT_USERMISSMATCH, "Payment from is different to person Id"));
			}
		}	else {
			if (payment == null) {
				payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PAYMENT_NOT_FOUND, "Payment not found"));
			}
			if (person == null) {
				payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found"));
			}
		}
		
		return new PaymentOut(payment);
    }
	
	/**
	 * Set the payment status to Paid. The 'to' person accepts the Requested payment status
	 * @param toId The Id of the person that should receive the payment
	 * @param paymentId The Id of the payment
	 * @return PaymentOut
	 */
	@RequestMapping(value="/acceptpay", method=RequestMethod.GET)
    public PaymentOut acceptPayment(@RequestParam(value="tId", defaultValue="0") long toId, 
    								@RequestParam(value="pId", defaultValue="") long paymentId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Person person = personRepo.findById(toId);
		Payment payment = paymentsRepo.findById(paymentId);
		if (person != null && payment != null) {
			if (payment.getTo().getEmail() == person.getEmail()) {
				payment.setStatus(PaymentStatus.PAID);
				paymentsRepo.save(payment);
			}	else {
				payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PAYMENT_USERMISSMATCH, "Payment to is different to person Id"));
			}
		}	else {
			if (payment == null) {
				payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PAYMENT_NOT_FOUND, "Payment not found"));
			}
			if (person == null) {
				payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found"));
			}
		}
		
		return new PaymentOut(payment);
    }
	
	/**
	 * Sets the payment status to Conflict. The 'to' person rejects the Requested payment status
	 * @param toId The Id of the person that should receive the payment
	 * @param paymentId The Id of the payment
	 * @return PaymentOut
	 */
	@RequestMapping(value="/suepay", method=RequestMethod.GET)
    public PaymentOut suePayment(@RequestParam(value="tId", defaultValue="0") long toId, 
    							 @RequestParam(value="pId", defaultValue="") long paymentId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Person person = personRepo.findById(toId);
		Payment payment = paymentsRepo.findById(paymentId);
		if (person != null && payment != null) {
			if (payment.getTo().getEmail() == person.getEmail()) {
				payment.setStatus(PaymentStatus.CONFLICT);
				paymentsRepo.save(payment);
			}	else {
				payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PAYMENT_USERMISSMATCH, "Payment from is different to person Id"));
			}
		}	else {
			if (payment == null) {
				payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PAYMENT_NOT_FOUND, "Payment not found"));
			}
			if (person == null) {
				payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found"));
			}
		}
		
		return new PaymentOut(payment);
    }
	
	/**
	 * Sets the payment status to Pending, but only if the current status isn't Paid
	 * @param paymentId The Id of the payment
	 * @return PaymentOut
	 */
	@RequestMapping(value="/resetpay", method=RequestMethod.GET)
    public PaymentOut resetPayment(@RequestParam(value="pId", defaultValue="0") long paymentId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Payment payment = paymentsRepo.findById(paymentId);
		if (payment != null) {
			if (payment.getStatus() != PaymentStatus.PAID) {
				payment.setStatus(PaymentStatus.PENDING);
				paymentsRepo.save(payment);
			}	else {
				payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PAYMENT_CLOSED, "Payment is already closed"));
			}
		}	else {
			payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PAYMENT_NOT_FOUND, "Payment not found"));
		}
		
		return new PaymentOut(payment);
    }
	
	/**
	 * Sets the payment status to Pending
	 * @param paymentId The Id of the payment
	 * @return PaymentOut
	 */
	@RequestMapping(value="/forceresetpay", method=RequestMethod.GET)
    public PaymentOut forceResetPayment(@RequestParam(value="pId", defaultValue="0") long paymentId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Payment payment = paymentsRepo.findById(paymentId);
		if (payment != null) {
			payment.setStatus(PaymentStatus.PENDING);
			paymentsRepo.save(payment);
		}	else {
			payment = new Payment().setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PAYMENT_NOT_FOUND, "Payment not found"));
		}
		
		return new PaymentOut(payment);
    }
	
	/**
	 * Returns the last error occurred 
	 * @return Error
	 */
	@RequestMapping(value="/lasterror", method=RequestMethod.GET)
    public Error getLastError() {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		return eConf.lastError();
    }
	
	/**
	 * Delete a Payment
	 * @param paymentId The Id of the payment
	 * @return PaymentOut
	 */
	@RequestMapping(value="/deletepayment", method=RequestMethod.GET)
    public PaymentOut deletePayment(@RequestParam(value="tId", defaultValue="0") long paymentId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Payment payment = paymentsRepo.findById(paymentId);
		if (payment == null) {
			return new PaymentOut(
					new Payment()
					.setError(eConf.lastError().updateError(ErrorCode.PAYMENTS_SERVICES, ErrorType.PAYMENT_NOT_FOUND, "Payment not found")));
		}
		
		paymentsRepo.delete(payment);
		
		return new PaymentOut(payment);
    }
}
