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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.beans.factory.annotation.Autowired;

@Entity
public class Activity { 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	private Person owner;
	
	
	@ManyToMany(cascade = CascadeType.ALL)
	private Set<Person> persons;
	
	@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
	private Set<Task> tasks;
	
	private String name;
	private String modified;
	private Date date;
	private boolean calculated = false;
	private double total = 0.0;
		
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

	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
		updateModified();
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
		updateModified();
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
	
	public Set<Person> getPersons() {
		return persons;
	}

	public void setPersons(Set<Person> persons) {
		this.persons = persons;
		updateModified();
	}

	private void updateModified() {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        modified = sdf.format(cal.getTime());
	}
}
