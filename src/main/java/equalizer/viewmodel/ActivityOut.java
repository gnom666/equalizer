package equalizer.viewmodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import equalizer.model.Activity;
import equalizer.controlermodel.Error;

/**
 * Class to be used as interface of the Activity class by the activitiesServices methods
 * @author jorgerios
 * 
 */
public class ActivityOut {
	
	public long id;
	public long owner;
	public List<Long> participants;
	public List<Long> tasks;
	public List<Long> payments;
	public String name;
	public String modified;
	public String date;
	public String description;
	public boolean calculated = false;
	public double total = 0.0;
	public Error error;
	
	public ActivityOut() {
		participants = new ArrayList<>();
		tasks = new ArrayList<>();
		payments = new ArrayList<>();
	}
	
	public ActivityOut(long id, long owner, List<Long> participants, List<Long> tasks, List<Long> payments, String name,
			String modified, String date, String description, boolean calculated, double total, Error error) {
		this.id = id;
		this.owner = owner;
		this.participants = participants;
		this.tasks = tasks;
		this.payments = payments;
		this.name = name;
		this.modified = modified;
		this.date = date;
		this.description = description;
		this.calculated = calculated;
		this.total = total;
		this.error = error;
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
			this.owner = activity.getOwner().getId();
			this.name = activity.getName();
			this.modified = activity.getModified();
			this.date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).format(activity.getDate());
			this.description = activity.getDescription();
			this.calculated = activity.isCalculated();
			this.total = activity.getTotal();
			this.error = activity.getError();
		}
	}

	@Override
	public String toString() {
		return "ActivityOut [id=" + id + ", owner=" + owner + ", participants=" + participants + ", tasks=" + tasks
				+ ", payments=" + payments + ", name=" + name + ", modified=" + modified + ", date=" + date
				+ ", description=" + description + ", calculated=" + calculated + ", total=" + total + ", error="
				+ error + "]";
	}		
}
