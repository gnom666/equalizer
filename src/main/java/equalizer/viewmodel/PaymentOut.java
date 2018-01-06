package equalizer.viewmodel;

import equalizer.controlermodel.Constants.PaymentStatus;
import equalizer.model.Payment;
import equalizer.controlermodel.Error;

/**
 * Class to be used as interface of the Payment class by the paymentsServices methods
 * @author jorgerios
 *
 */
public class PaymentOut {
	
	public long id;
	public long from;
	public long to;
	public long activity;
	public String modified;
	public double amount;
	public PaymentStatus status;
	public Error error;
		
	public PaymentOut (long id, long from, long to, long activity, String modified, double amount, PaymentStatus status
			, Error error) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.activity = activity;
		this.modified = modified;
		this.amount = amount;
		this.status = status;
		this.error = error;
	}
	
	public PaymentOut (Payment payments) {
		if (payments != null) {
			this.id = payments.getId();
			this.from = payments.getFrom().getId();
			this.to = payments.getTo().getId();
			this.activity = payments.getActivity().getId();
			this.modified = payments.getModified();
			this.amount = payments.getAmount();
			this.status = payments.getStatus();
			this.error = payments.getError();
		}
	}
	
}
