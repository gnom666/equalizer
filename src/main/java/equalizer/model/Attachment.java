package equalizer.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import equalizer.controlermodel.Error;

@Entity
public class Attachment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Task.class)
	@OneToOne(mappedBy="attachment")
    private Task task;
	
	private String name;
	private String contentB64;
	private String modified;
	
	@Transient
	private Error error = null;

	public Attachment() {}
	
	public Attachment(Task task, String name, String contentB64) {
		this.task = task;
		this.name = name;
		this.contentB64 = contentB64;
		updateModified();
	}
	
	public Attachment(String name, String contentB64) {
		this.name = name;
		this.contentB64 = contentB64;
		updateModified();
	}

	public String getContentB64() {
		return contentB64;
	}

	public Error getError() {
		return error;
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

	public Task getTask() {
		return task;
	}

	public void setContentB64(String contentB64) {
		this.contentB64 = contentB64;
		updateModified();
	}

	public Attachment setError(Error error) {
		this.error = error;
		return this;
	}

	public void setModified(String modified) {
		this.modified = modified;
		updateModified();
	}
	
	public void setName(String name) {
		this.name = name;
		updateModified();
	}

	public void setTask(Task task) {
		this.task = task;
		updateModified();
	}
	
	private void updateModified() {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        modified = sdf.format(cal.getTime());
	}
	
}
