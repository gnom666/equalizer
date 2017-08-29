package equalizer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Payments { 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name="from_id")
	private Person from;
	
	@ManyToOne
	@JoinColumn(name="to_id")
	private Person to;
	
	@ManyToOne
	@JoinColumn(name="activity_id")
	private Activity activity;
	
	private String modified;
	private double ammount;
		
	public long getId() {
		return id;
	}
	
	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
		updateModified();
	}

	public double getAmmount() {
		return ammount;
	}

	public void setAmmount(double ammount) {
		this.ammount = ammount;
		updateModified();
	}
	
	public Person getFrom() {
		return from;
	}

	public void setFrom(Person from) {
		this.from = from;
		updateModified();
	}

	public Person getTo() {
		return to;
	}

	public void setTo(Person to) {
		this.to = to;
		updateModified();
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
		updateModified();
	}

	private void updateModified() {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        modified = sdf.format(cal.getTime());
	}
}
