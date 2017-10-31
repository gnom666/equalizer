package equalizer.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import equalizer.model.Person;

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
	public String user;
	public String password;
	public int numpers;
	
	public PersonOut(long id, List<Long> owns, List<Long> activities, List<Long> tasks, List<Long> paid,
			List<Long> received, String firstName, String lastName, String modified, String email, String user,
			String password, int numpers) {
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
		this.user = user;
		this.password = password;
		this.numpers = numpers;
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
			this.user = person.getUser();
			this.password = person.getPassword();
			this.numpers = person.getNumpers();
		}
	}

	@Override
	public String toString() {
		return "[id=" + id + ", owns=" + owns + ", activities=" + activities + ", tasks=" + tasks + ", paid="
				+ paid + ", received=" + received + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", modified=" + modified + ", email=" + email + ", user=" + user + ", password=" + password
				+ ", numpers=" + numpers + "]";
	}
}
