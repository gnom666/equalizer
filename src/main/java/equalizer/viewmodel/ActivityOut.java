package equalizer.viewmodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import equalizer.model.Activity;

public class ActivityOut {
	public long id;
	public String owner;
	public List<Long> participants;
	public List<Long> tasks;
	public List<Long> payments;
	public String name;
	public String modified;
	public String date;
	public boolean calculated;
	public double total = 0.0;
	
	public ActivityOut(long id, String owner, List<Long> participants, List<Long> tasks, List<Long> payments, String name,
			String modified, String date, boolean calculated, double total) {
		this.id = id;
		this.owner = owner;
		this.participants = participants;
		this.tasks = tasks;
		this.payments = payments;
		this.name = name;
		this.modified = modified;
		this.date = date;
		this.calculated = calculated;
		this.total = total;
	}
	
	public ActivityOut(Activity activity) {
		
		this.participants = new ArrayList<>();
		this.tasks = new ArrayList<>();
		this.payments = new ArrayList<>();
		
		if (activity != null) {
			activity.getParticipants().forEach(p->this.participants.add(p.getId()));
			activity.getTasks().forEach(t->this.tasks.add(t.getId()));
			activity.getPayments().forEach(p->this.payments.add(p.getId()));
			
			this.id = activity.getId();
			this.owner = activity.getOwner().getEmail();
			this.name = activity.getName();
			this.modified = activity.getModified();
			this.date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).format(activity.getDate());
			this.calculated = activity.isCalculated();
			this.total = activity.getTotal();
		}
	}
		
}
