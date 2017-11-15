package equalizer.viewmodel;

import java.util.ArrayList;
import java.util.List;

import equalizer.model.Person;
import equalizer.controlermodel.Error;

/**
 * Class to be used as interface of the Person class by the personServices methods
 * @author jorgerios
 *
 */
public class PersonOut {
	
	public long id;
	public List<Long> owns;
	public List<Long> activities;
	public List<Long> tasks;
	public List<Long> paid;
	public List<Long> received;
	public String firstName;
	public String lastName;
	public String modified;
	public String email;
	public String password;
	public int numpers;
	public boolean enabled;
	public Error error;
	
	public PersonOut(long id, List<Long> owns, List<Long> activities, List<Long> tasks, List<Long> paid,
			List<Long> received, String firstName, String lastName, String modified, String email,
			String password, int numpers, boolean enabled, Error error) {
		this.id = id;
		this.owns = owns;
		this.activities = activities;
		this.tasks = tasks;
		this.paid = paid;
		this.received = received;
		this.firstName = firstName;
		this.lastName = lastName;
		this.modified = modified;
		this.email = email;
		this.password = password;
		this.numpers = numpers;
		this.enabled = enabled;
		this.error = error;
	}
	
	public PersonOut(Person person) {
		this.owns = new ArrayList<>();
		this.activities = new ArrayList<>();
		this.tasks = new ArrayList<>();
		this.paid = new ArrayList<>();
		this.received = new ArrayList<>();
		
		if (person != null) {
			person.getOwns().forEach(o->this.owns.add(o.getId()));
			person.getActivities().forEach(a->this.activities.add(a.getId()));
			person.getTasks().forEach(t->this.tasks.add(t.getId()));
			person.getPaid().forEach(p->this.paid.add(p.getId()));
			person.getReceived().forEach(r->this.received.add(r.getId()));
			
			this.id = person.getId();
			this.firstName = person.getFirstName();
			this.lastName = person.getLastName();
			this.modified = person.getModified();
			this.email = person.getEmail();
			this.password = person.getPassword();
			this.numpers = person.getNumpers();
			this.enabled = person.isEnabled();
			this.error = person.getError();
		}
	}
	
	public PersonOut toPublic() {
		
		this.password = "";
		this.modified = "";
		
		return this;
	}
	
}
