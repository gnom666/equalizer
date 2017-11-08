package equalizer.viewmodel;

import equalizer.model.Task;
import equalizer.controlermodel.Error;

public class TaskOut {

	public long id;
	public long owner;
	public long activity;
	public String name;
	public String modified;
	public String description;
	public boolean calculated;
	public double ammount;
	public Error error;
	
	public TaskOut(long id, long owner, long activity, String name, String modified, String description, 
			boolean calculated,	double ammount, Error error) {
		this.id = id;
		this.owner = owner;
		this.activity = activity;
		this.name = name;
		this.modified = modified;
		this.description = description;
		this.calculated = calculated;
		this.ammount = ammount;
		this.error = error;
	}
	
	public TaskOut(Task task) {
		if (task != null) {
			this.id = task.getId();
			this.owner = task.getOwner().getId();
			this.activity = task.getActivity().getId();
			this.name = task.getName();
			this.modified = task.getModified();
			this.description = task.getDescription();
			this.calculated = task.isCalculated();
			this.ammount = task.getAmmount();
			this.error = task.getError();
		}
	}

}
