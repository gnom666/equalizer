package equalizer.viewmodel;

import equalizer.model.Task;

public class TaskOut {

	public long id;
	public long owner;
	public long activity;
	public String name;
	public String modified;
	public boolean calculated;
	public double ammount;
	
	public TaskOut(long id, long owner, long activity, String name, String modified, boolean calculated,
			double ammount) {
		this.id = id;
		this.owner = owner;
		this.activity = activity;
		this.name = name;
		this.modified = modified;
		this.calculated = calculated;
		this.ammount = ammount;
	}
	
	public TaskOut(Task task) {
		if (task != null) {
			this.id = task.getId();
			this.owner = task.getOwner().getId();
			this.activity = task.getActivity().getId();
			this.name = task.getName();
			this.modified = task.getModified();
			this.calculated = task.isCalculated();
			this.ammount = task.getAmmount();
		}
	}

}
