package equalizer.controler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import equalizer.config.EqualizerConfiguration;
import equalizer.controlermodel.Constants.ErrorCode;
import equalizer.controlermodel.Constants.ErrorType;
import equalizer.controlermodel.Error;
import equalizer.model.Activity;
import equalizer.model.Person;
import equalizer.model.Task;
import equalizer.repository.ActivityRepository;
import equalizer.repository.PaymentsRepository;
import equalizer.repository.PersonRepository;
import equalizer.repository.TaskRepository;
import equalizer.viewmodel.TaskOut;

/**
 * Tasks Services
 * @author jorgerios
 *
 */
@RestController
@RequestMapping("/tasks")
public class TasksServices {
	
	@Autowired
	private ActivityRepository activityRepo;
	
	@Autowired
	private TaskRepository taskRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private PaymentsRepository paymentsRepo;

	@Autowired
	private EqualizerConfiguration eConf;
	
	/**
	 * Lists the tasks for a specific activity
	 * @param activityId The Id of the activity
	 * @return List of TaskOut
	 */
	@RequestMapping(value="/tasksbyact", method=RequestMethod.GET)
    public List<TaskOut> tasksByActivity(@RequestParam(value="aId", defaultValue="0") long activityId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		List<TaskOut> result = new ArrayList<>();
		Activity activity = activityRepo.findById(activityId);
		if (activity != null) {
			List<Task> tasksList =  taskRepo.findByActivity(activity);
			tasksList.forEach(t->result.add(new TaskOut(t)));		
		}	else {
			result.add(new TaskOut(
					new Task()
					.setError(eConf.lastError().updateError(ErrorCode.TASKS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found"))));
		}
		return result;
    }
	
	/**
	 * Lists the tasks owned by a specific person
	 * @param ownerId The Id of the person
	 * @return List of TaskOut
	 */
	@RequestMapping(value="/tasksbyowner", method=RequestMethod.GET)
    public List<TaskOut> tasksByOwner(@RequestParam(value="oId", defaultValue="0") long ownerId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		List<TaskOut> result = new ArrayList<>();
		Person owner = personRepo.findById(ownerId);
		if (owner != null) {
			List<Task> tasksList =  taskRepo.findByOwner(owner);
			tasksList.forEach(t->result.add(new TaskOut(t)));
		}	else {
			result.add(new TaskOut(
					new Task()
					.setError(eConf.lastError().updateError(ErrorCode.TASKS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found"))));
		}
		return result;
    }
	
	/**
	 * Lists the tasks owned by a person for a specific activity
	 * @param activityId The Id of the activity
	 * @param ownerId The Id of the person
	 * @return List TaskOut
	 */
	@RequestMapping(value="/tasksbyactandowner", method=RequestMethod.GET)
    public List<TaskOut> tasksByActivityAndOwner(@RequestParam(value="aId", defaultValue="0") long activityId, 
    											 @RequestParam(value="oId", defaultValue="0") long ownerId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		List<TaskOut> result = new ArrayList<>();
		Activity activity = activityRepo.findById(activityId);
		Person owner = personRepo.findById(ownerId);
		if (activity != null && owner != null) {
			List<Task> tasksList =  taskRepo.findByActivityAndOwner(activity, owner);
			tasksList.forEach(t->result.add(new TaskOut(t)));		
		}
		if (activity == null) {
			result.add(new TaskOut(
					new Task()
					.setError(eConf.lastError().updateError(ErrorCode.TASKS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found"))));
		}
		if (owner == null) {
			result.add(new TaskOut(
					new Task()
					.setError(eConf.lastError().updateError(ErrorCode.TASKS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found"))));
		}
		
		return result;
    }
	
	/**
	 * Add a new Task
	 * @param tsk The Task
	 * @return TaskOut
	 */
	@RequestMapping(value="/addtask", method=RequestMethod.POST)
    public TaskOut addTask(@RequestBody TaskOut tsk) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Task task = new Task();
    	
    	Person owner = personRepo.findById(tsk.owner);
    	if (owner == null) {
    		return new TaskOut(
    				new Task()
    				.setError(new Error(ErrorCode.TASKS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Unknown owner " + tsk.owner)));
    	}
    	
    	Activity activity = activityRepo.findById(tsk.activity);
    	if (activity == null) {
    		return new TaskOut(
    				new Task()
    				.setError(new Error(ErrorCode.TASKS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Unknown activity " + tsk.activity)));
    	}
    	
    	task.setActivity(activity);
    	task.setOwner(owner);
    	task.setName(tsk.name);
    	task.setDescription(tsk.description);
    	task.setAmount(tsk.amount);
    	task.setCalculated(false);
    	
    	taskRepo.save(task);
		
		return new TaskOut(task);
    }
	
	/**
	 * Modify Task
	 * @param tsk The Task
	 * @return TaskOut
	 */
	@RequestMapping(value="/modifytask", method=RequestMethod.POST)
    public TaskOut modifyTask(@RequestBody TaskOut tsk) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Task task = taskRepo.findById(tsk.id);
		if (task == null) {
			return new TaskOut(
    				new Task()
    				.setError(new Error(ErrorCode.TASKS_SERVICES, ErrorType.TASK_NOT_FOUND, "Unknown task " + tsk.id)));
		}		
    	
    	task.setName(tsk.name);
    	task.setDescription(tsk.description);
    	if (tsk.amount != task.getAmount()) {
	    	task.setAmount(tsk.amount);
	    	task.setCalculated(false);
	    	paymentsRepo.removeByActivity(task.getActivity());
	    	task.getActivity().setCalculated(false);
	    	
	    	activityRepo.save(task.getActivity());
	    	
    	}	else {
    		task.setCalculated(tsk.calculated);
    	}
    	
    	taskRepo.save(task);
		
		return new TaskOut(task);
    }
	
	/**
	 * Delete a Task
	 * @param taskId The Id of the task
	 * @return TaskOut
	 */
	@RequestMapping(value="/deletetask", method=RequestMethod.GET)
    public TaskOut deleteTask (@RequestParam(value="tId", defaultValue="0") long taskId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Task task = taskRepo.findById(taskId);
		if (task == null) {
			return new TaskOut(
					new Task()
					.setError(eConf.lastError().updateError(ErrorCode.TASKS_SERVICES, ErrorType.TASK_NOT_FOUND, "Task not found")));
		}
		
		taskRepo.delete(task);
		
		return new TaskOut(task);
    }
	
	
}
