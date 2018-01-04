package equalizer.viewmodel;

import equalizer.model.Attachment;
import equalizer.model.Task;
import equalizer.controlermodel.Error;

/**
 * Class to be used as interface of the Attachment class by the attachmentServices methods
 * @author jorgerios
 *
 */
public class AttachmentOut {

	public long id;
	public String name;
	public String modified;
	public String contentB64;
	public long task;
	public Error error;
	
	public AttachmentOut () {
		error = null;
	}

	public AttachmentOut(long id, String name, String modified, String contentB64, long task, Error error) {
		this.id = id;
		this.name = name;
		this.modified = modified;
		this.contentB64 = contentB64;
		this.task = task;
		this.error = error;
	}
	
	public AttachmentOut(Attachment attachment) {
		this.id = attachment.getId();
		this.name = attachment.getName();
		this.modified = attachment.getModified();
		this.contentB64 = attachment.getContentB64();
		this.task = attachment.getTask().getId();
		this.error = attachment.getError();
	}

}
