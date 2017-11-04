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
import equalizer.viewmodel.TaskOut;

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
	
	@RequestMapping(value="/tasksbyact", method=RequestMethod.GET)
    public List<TaskOut> tasksByActivity(@RequestParam(value="aId", defaultValue="0") long activityId) {
    	
		Activity activity = activityRepo.findById(activityId);
		if (activity != null) {
			List<Task> tasksList =  taskRepo.findByActivity(activity);
			ArrayList<TaskOut> tasksOutList = new ArrayList<>();
			tasksList.forEach(t->tasksOutList.add(new TaskOut(t)));		
			
			return tasksOutList;
		}
		eConf.lastError().updateError(ErrorCode.TASKS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found");
		return null;
    }
	
	@RequestMapping(value="/tasksbyowner", method=RequestMethod.GET)
    public List<TaskOut> tasksByOwner(@RequestParam(value="oId", defaultValue="0") long ownerId) {
    	
		Person owner = personRepo.findById(ownerId);
		if (owner != null) {
			List<Task> tasksList =  taskRepo.findByOwner(owner);
			ArrayList<TaskOut> tasksOutList = new ArrayList<>();
			tasksList.forEach(t->tasksOutList.add(new TaskOut(t)));		
			
			return tasksOutList;
		}
		eConf.lastError().updateError(ErrorCode.TASKS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found");
		return null;
    }
	
	@RequestMapping(value="/tasksbyactandowner", method=RequestMethod.GET)
    public List<TaskOut> tasksByActivityAndOwner(@RequestParam(value="aId", defaultValue="0") long activityId, 
    											 @RequestParam(value="oId", defaultValue="0") long ownerId) {
    	
		Activity activity = activityRepo.findById(activityId);
		Person owner = personRepo.findById(ownerId);
		if (activity != null && owner != null) {
			List<Task> tasksList =  taskRepo.findByActivityAndOwner(activity, owner);
			ArrayList<TaskOut> tasksOutList = new ArrayList<>();
			tasksList.forEach(t->tasksOutList.add(new TaskOut(t)));		
			
			return tasksOutList;
		}
		if (activity == null) {
			eConf.lastError().updateError(ErrorCode.TASKS_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found");
		}
		if (owner == null) {
			eConf.lastError().updateError(ErrorCode.TASKS_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found");
		}
		
		return null;
    }
}
