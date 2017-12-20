package equalizer.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import equalizer.controlermodel.Error;

/**
 * Class that represents the Person or a group of Persons
 * @author jorgerios
 *
 */
@Entity
public class Person { 
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private long id;

	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Activity.class)
	@OneToMany(mappedBy = "owner")
	private List<Activity> owns;

	/*@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Activity.class)
	@ManyToMany(mappedBy = "participants")*/
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Activity.class)
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(	name = "person_activity", 
				joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"), 
				inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"))
	private List<Activity> activities;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Task.class)
	@OneToMany(mappedBy = "owner")
	private List<Task> tasks;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Payment.class)
	@OneToMany(mappedBy = "from")
	private List<Payment> paid;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Payment.class)
	@OneToMany(mappedBy = "to")
	private List<Payment> received;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Person.class)
	@ManyToMany(cascade = CascadeType.ALL,
				targetEntity = Person.class,
				fetch = FetchType.LAZY)
	@JoinTable(	name = "person_person",
				joinColumns = @JoinColumn(name = "contactOf", referencedColumnName = "id"), 
				inverseJoinColumns = @JoinColumn(name = "contact", referencedColumnName = "id"))
	private List<Person> contacts;
	
	//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Person.class)
	//@ManyToMany(mappedBy = "contacts")
	@ManyToMany(cascade = CascadeType.ALL,
				targetEntity = Person.class,
				fetch = FetchType.LAZY)
	@JoinTable(	name = "person_person",
				joinColumns = @JoinColumn(name = "contact", referencedColumnName = "id"), 
				inverseJoinColumns = @JoinColumn(name = "contactOf", referencedColumnName = "id"))
	private List<Person> contactOf;
	
	private String firstName;

	private String lastName;

	private String modified;

	@NotNull
    @Min(1)
	private int numpers;

	@Column(unique = true, nullable = false, length = 45)
	private String email;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private boolean enabled;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Role.class)
	@ManyToOne
    @JoinColumn(name="role")
    private Role role;
	
	@Transient
	private Error error = null;
	
	public Person() {
		this.owns = new ArrayList<>();
		this.activities = new ArrayList<>();
		this.tasks = new ArrayList<>();
		this.paid = new ArrayList<>();
		this.received = new ArrayList<>();
		this.contacts = new ArrayList<>();
		this.contactOf = new ArrayList<>();
	}
	
	public List<Activity> getActivities() {
		return activities;
	}
	
	public List<Person> getContactOf() {
		return contactOf;
	}
	
	public List<Person> getContacts() {
		return contacts;
	}
	
	public String getEmail() {
		return email;
	}

	public Error getError() {
		return error;
	}

	public String getFirstName() {
		return firstName;
	}

	public long getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public String getModified() {
		return modified;
	}

	public int getNumpers() {
		return numpers;
	}
	public List<Activity> getOwns() {
		return owns;
	}
	
	public List<Payment> getPaid() {
		return paid;
	}

	public String getPassword() {
		return password;
	}

	public List<Payment> getReceived() {
		return received;
	}

	public Role getRole() {
		return role;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
		updateModified();
	}

	public void setContactOf(List<Person> contactOf) {
		this.contactOf = contactOf;
	}
	
	public void setContacts(List<Person> contacts) {
		this.contacts = contacts;
	}
	
	public void setEmail(String email) {
		this.email = email;
		updateModified();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Person setError(Error error) {
		this.error = error;
		return this;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
		updateModified();
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
		updateModified();
	}

	public void setModified(String modified) {
		this.modified = modified;
	}
	
	public void setNumpers(int numpers) {
		this.numpers = numpers;
		updateModified();
	}

	public void setOwns(List<Activity> owns) {
		this.owns = owns;
		updateModified();
	}

	public void setPaid(List<Payment> paid) {
		this.paid = paid;
		updateModified();
	}

	public void setPassword(String password) {
		this.password = password;
		updateModified();
	}

	public void setReceived(List<Payment> received) {
		this.received = received;
		updateModified();
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
		updateModified();
	}
	
	public List<Person> addToContacts (Person person) {
		for (Person p : contacts) {
			if (p.id == person.id) return contacts;
		}
		contacts.add(person);
		return contacts;
	}
	
	public List<Person> addToContactOf (Person person) {
		for (Person p : contactOf) {
			if (p.id == person.id) return contactOf;
		}
		contactOf.add(person);
		return contactOf;
	}
	
	public List<Person> removeFromContacts (Person person) {
		for (Person p : contacts) {
			if (p.id == person.id) {
				contacts.remove(p);
				return contacts;
			}
		}
		return contacts;
	}
	
	public List<Person> removeFromContactOf (Person person) {
		for (Person p : contactOf) {
			if (p.id == person.id) {
				contactOf.remove(p);
				return contactOf;
			}
		}
		return contactOf;
	}

	private void updateModified() {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        modified = sdf.format(cal.getTime());
	}
}
