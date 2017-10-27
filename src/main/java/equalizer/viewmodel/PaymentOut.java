package equalizer.viewmodel;

import equalizer.model.Payments;

public class PaymentOut {
	
	public long id;
	public long from;
	public long to;
	public long activity;
	public String modified;
	public double ammount;
		
	public PaymentOut (long id, long from, long to, long activity, String modified, double ammount) {
		this.id = id;
		this.from = from;
		this.to = to;
		this.activity = activity;
		this.modified = modified;
		this.ammount = ammount;
	}
	
	public PaymentOut (Payments payments) {
		this.id = payments.getId();
		this.from = payments.getFrom().getId();
		this.to = payments.getTo().getId();
		this.activity = payments.getActivity().getId();
		this.modified = payments.getModified();
		this.ammount = payments.getAmmount();
	}

	@Override
	public String toString() {
		return "[id=" + id + ", from=" + from + ", to=" + to + ", activity=" + activity + ", modified="
				+ modified + ", ammount=" + ammount + "]";
	}
}
