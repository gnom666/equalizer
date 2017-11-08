package equalizer.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import equalizer.controlermodel.Error;
import equalizer.controlermodel.Constants.PaymentStatus;

@Entity
public class Payment { 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Person.class)
	@ManyToOne
	@JoinColumn(name="from_id")
	private Person from;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Person.class)
	@ManyToOne
	@JoinColumn(name="to_id")
	private Person to;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Activity.class)
	@ManyToOne
	@JoinColumn(name="activity_id")
	private Activity activity;
	
	private String modified;
	private double ammount;
	private PaymentStatus status;
	
	@Transient
	private Error error = null;
	
	public Payment() {
		this.from = new Person();
		this.to = new Person();
		this.activity = new Activity();
	}

	public Activity getActivity() {
		return activity;
	}
	
	public double getAmmount() {
		return ammount;
	}

	public Error getError() {
		return error;
	}

	public Person getFrom() {
		return from;
	}

	public long getId() {
		return id;
	}
	
	public String getModified() {
		return modified;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public Person getTo() {
		return to;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
		updateModified();
	}
	
	public void setAmmount(double ammount) {
		this.ammount = ammount;
		updateModified();
	}

	public Payment setError(Error error) {
		this.error = error;
		return this;
	}

	public void setFrom(Person from) {
		this.from = from;
		updateModified();
	}

	public void setModified(String modified) {
		this.modified = modified;
		updateModified();
	}

	public void setStatus(PaymentStatus pending) {
		this.status = pending;
	}

	public void setTo(Person to) {
		this.to = to;
		updateModified();
	}

	private void updateModified() {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        modified = sdf.format(cal.getTime());
	}
}
