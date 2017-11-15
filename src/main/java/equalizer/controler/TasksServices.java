package equalizer.controler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import equalizer.config.EqualizerConfiguration;
import equalizer.controlermodel.Constants.ErrorCode;
import equalizer.controlermodel.Constants.ErrorType;
import equalizer.model.Activity;
import equalizer.model.Person;
import equalizer.model.Task;
import equalizer.repository.ActivityRepository;
import equalizer.repository.PersonRepository;
import equalizer.repository.TaskRepository;
import equalizer.viewmodel.ActivityOut;
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
	private EqualizerConfiguration eConf;
	
	/**
	 * Lists the tasks for a specific activity
	 * @param activityId The Id of the activity
	 * @return List of TaskOut
	 */
	@RequestMapping(value="/tasksbyact", method=RequestMethod.GET)
    public List<TaskOut> tasksByActivity(@RequestParam(value="aId", defaultValue="0") long activityId) {
    	
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
}
