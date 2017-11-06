package equalizer.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
public class Task { 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Person.class)
	@ManyToOne
	@JoinColumn(name="owner_id")
	private Person owner;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Activity.class)
	@ManyToOne
    @JoinColumn(name="activity_id")
    private Activity activity;
	
	private String name;
	private String description;
	private String modified;

	private boolean calculated;

	private double ammount;
	
	public Activity getActivity() {
		return activity;
	}
	public double getAmmount() {
		return ammount;
	}
		
	public String getDescription() {
		return description;
	}
	
	public long getId() {
		return id;
	}

	public String getModified() {
		return modified;
	}

	public String getName() {
		return name;
	}

	public Person getOwner() {
		return owner;
	}
	
	public boolean isCalculated() {
		return calculated;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
		updateModified();
	}

	public void setAmmount(double ammount) {
		this.ammount = ammount;
		updateModified();
	}

	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
		updateModified();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setModified(String modified) {
		this.modified = modified;
		updateModified();
	}

	public void setName(String name) {
		this.name = name;
		updateModified();
	}

	public void setOwner(Person owner) {
		this.owner = owner;
		updateModified();
	}

	private void updateModified() {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        modified = sdf.format(cal.getTime());
	}
}
