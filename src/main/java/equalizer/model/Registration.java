package equalizer.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import equalizer.controlermodel.Constants;
import equalizer.controlermodel.Constants.RegistrationStatus;
import equalizer.controlermodel.Error;

@Entity
public class Registration {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Person.class)
	@OneToOne(mappedBy="registration")
    private Person person;
	
	private String token;
	
	private Date date;
	
	private RegistrationStatus status;
	
	@Transient
	private Error error = null;
	
	public Registration() {
		this.token = Constants.unique();
		this.date = new Date();
		this.status = RegistrationStatus.PENDING;
	}
	
	public void regenerateToken () {
		this.token = Constants.unique();
		this.date = new Date();
	}

	public Date getDate() {
		return date;
	}

	public Error getError() {
		return error;
	}

	public long getId() {
		return id;
	}

	public Person getPerson() {
		return person;
	}

	public RegistrationStatus getStatus() {
		return status;
	}

	public String getToken() {
		return token;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Registration setError(Error error) {
		this.error = error;
		return this;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
