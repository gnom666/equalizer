package equalizer.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


@Entity
public class Person { 
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private long id;

	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Activity.class)
	@OneToMany(mappedBy = "owner")
	private Set<Activity> owns;

	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Activity.class)
	@ManyToMany(mappedBy = "participants")
	private Set<Activity> activities;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Task.class)
	@OneToMany(mappedBy = "owner")
	private Set<Task> tasks;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Payment.class)
	@OneToMany(mappedBy = "from")
	private Set<Payment> paid;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Payment.class)
	@OneToMany(mappedBy = "to")
	private Set<Payment> received;
	
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
	
	public Set<Activity> getActivities() {
		return activities;
	}

	public String getEmail() {
		return email;
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

	public Set<Activity> getOwns() {
		return owns;
	}

	public Set<Payment> getPaid() {
		return paid;
	}

	public String getPassword() {
		return password;
	}

	public Set<Payment> getReceived() {
		return received;
	}

	public Role getRole() {
		return role;
	}

	public Set<Task> getTasks() {
		return tasks;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setActivities(Set<Activity> activities) {
		this.activities = activities;
		updateModified();
	}

	public void setEmail(String email) {
		this.email = email;
		updateModified();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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

	public void setOwns(Set<Activity> owns) {
		this.owns = owns;
		updateModified();
	}

	public void setPaid(Set<Payment> paid) {
		this.paid = paid;
		updateModified();
	}

	public void setPassword(String password) {
		this.password = password;
		updateModified();
	}

	public void setReceived(Set<Payment> received) {
		this.received = received;
		updateModified();
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
		updateModified();
	}

	private void updateModified() {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        modified = sdf.format(cal.getTime());
	}
}
