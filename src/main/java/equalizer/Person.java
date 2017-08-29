package equalizer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;


@Entity
public class Person { 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@OneToMany(mappedBy = "owner")
	private Set<Activity> owns;
	
	@ManyToMany(mappedBy = "participants")
	private Set<Activity> activities;
	
	@OneToMany(mappedBy = "owner")
	private Set<Task> tasks;
	
	@OneToMany(mappedBy = "from")
	private Set<Payments> paid;
	
	@OneToMany(mappedBy = "to")
	private Set<Payments> received;

	private String firstName;
	private String lastName;
	private String modified;
	private String email;
	private String user;
	private String password;
	private int numpers;
	
	public long getId() {
		return id;
	}
	
	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
		updateModified();
	}

	public Set<Payments> getPaid() {
		return paid;
	}

	public void setPaid(Set<Payments> paid) {
		this.paid = paid;
		updateModified();
	}

	public Set<Payments> getReceived() {
		return received;
	}

	public void setReceived(Set<Payments> received) {
		this.received = received;
		updateModified();
	}

	public int getNumpers() {
		return numpers;
	}

	public void setNumpers(int numpers) {
		this.numpers = numpers;
		updateModified();
	}
	
	public Set<Activity> getOwns() {
		return owns;
	}

	public void setOwns(Set<Activity> owns) {
		this.owns = owns;
		updateModified();
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
		updateModified();
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
		updateModified();
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
		updateModified();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
		updateModified();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		updateModified();
	}

	public Set<Activity> getActivities() {
		return activities;
	}

	public void setActivities(Set<Activity> activities) {
		this.activities = activities;
		updateModified();
	}

	private void updateModified() {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        modified = sdf.format(cal.getTime());
	}
}
