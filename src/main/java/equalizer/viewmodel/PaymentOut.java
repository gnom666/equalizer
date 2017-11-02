package equalizer.viewmodel;

import equalizer.controlermodel.Constants.PaymentStatus;
import equalizer.model.Payment;

public class PaymentOut {
	
	public long id;
	public long from;
	public long to;
	public long activity;
	public String modified;
	public double ammount;
	public PaymentStatus status;
		
	public PaymentOut (long id, long from, long to, long activity, String modified, double ammount, PaymentStatus status) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.activity = activity;
		this.modified = modified;
		this.ammount = ammount;
		this.status = status;
	}
	
	public PaymentOut (Payment payments) {
		if (payments != null) {
			this.id = payments.getId();
			this.from = payments.getFrom().getId();
			this.to = payments.getTo().getId();
			this.activity = payments.getActivity().getId();
			this.modified = payments.getModified();
			this.ammount = payments.getAmmount();
			this.status = payments.getStatus();
		}
	}
	
}
