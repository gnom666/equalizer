package equalizer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Activity { 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
    @JoinColumn(name="owner_id")
    private Person owner;
		
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(	name = "person_activity", 
				joinColumns = @JoinColumn(name = "person_id", referencedColumnName = "id"), 
				inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"))
	private Set<Person> participants;
	
	@OneToMany(mappedBy = "activity")
	private Set<Task> tasks;
	
	private String name;
	private String modified;
	private Date date;
	
	private boolean calculated = false;
	private double total = 0.0;
		
	public long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
		updateModified();
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}
	
	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
		updateModified();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		updateModified();
	}

	public boolean isCalculated() {
		return calculated;
	}

	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
		updateModified();
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
		updateModified();
	}
	
	public Set<Person> getParticipants() {
		return participants;
	}

	public void setParticipants(Set<Person> participants) {
		this.participants = participants;
		updateModified();
	}

	private void updateModified() {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        modified = sdf.format(cal.getTime());
	}
}
