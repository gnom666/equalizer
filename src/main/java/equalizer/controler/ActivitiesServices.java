package equalizer.controler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import equalizer.repository.ActivityRepository;
import equalizer.repository.PersonRepository;
import equalizer.viewmodel.ActivityOut;

/**
 * Activity Services
 * @author jorgerios
 *
 */
@RestController
@RequestMapping("/activities")
public class ActivitiesServices {
	
	@Autowired
	private ActivityRepository activityRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private EqualizerConfiguration eConf;
	
	/**
	 * List all activities owned by a person
	 * @param ownerId The Id of the person
	 * @return List of ActivityOut
	 */
	@RequestMapping(value="/activitiesbyowner", method=RequestMethod.GET)
    public List<ActivityOut> getActivitiesByOwner(@RequestParam(value="oId", defaultValue="0") long ownerId) {
    	
		List<ActivityOut> result = new ArrayList<>();
		Person owner = personRepo.findById(ownerId);
		if (owner != null) {
			List<Activity> activitiesList = activityRepo.findByOwner(owner);
			if (activitiesList != null) {
				activitiesList.forEach(a->result.add(new ActivityOut(a)));
			}	else {
				result.add(new ActivityOut(
						new Activity()
						.setError(eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found"))));
			}
		} 	else {
			result.add(new ActivityOut(
					new Activity()
					.setError(eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.PERSON_NOT_FOUND, "Owner not found"))));
		}		
		
		return result;
    }
	
	/**
	 * Add a new Activity
	 * @param act The Activity
	 * @return String
	 */
	@RequestMapping(value="/addactivity", method=RequestMethod.POST)
    public String addActivity(@RequestBody ActivityOut act) {
    	Activity activity = new Activity();
    	String result = "ok";
    	Error error = null;
    	
    	System.out.println(act.toString());
		
    	Person owner = personRepo.findById(act.owner);
    	if (owner == null) {
    		return new Error(ErrorCode.ACTIVITY_SERVICES, ErrorType.PERSON_NOT_FOUND, "Unknown owner " + act.owner).toString();
    	}
    	
    	activity.setOwner(owner);
		activity.setName(act.name);
		activity.setDescription(act.description);
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		try {
			activity.setDate(format.parse(act.date));
		} 	catch (ParseException e) {
			e.printStackTrace();
			return new Error(ErrorCode.ACTIVITY_SERVICES, ErrorType.BAD_DATE_FORMAT, e.getMessage()).toString();
		}
		
		activityRepo.save(activity);
		
		act.participants.forEach(pId -> {
			Person participant = personRepo.findById(pId);
			if (participant != null) {
				activity.addParticipant(participant);
			}
		});
		
		activityRepo.save(activity);
		
		return "OK";
    }
	
	/**
	 * Modify Activity
	 * @param act The Activity
	 * @return String
	 */
	@RequestMapping(value="/modifyactivity", method=RequestMethod.POST)
    public String modifyActivity(@RequestBody ActivityOut act) {
    	String result = "ok";
    	Error error = null;
    	
    	Activity activity = activityRepo.findById(act.id);
    	if (activity == null) {
    		return new Error(ErrorCode.ACTIVITY_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Unknown activity " + act.id).toString();
    	}
    	
    	Person owner = personRepo.findById(act.owner);
    	if (owner == null) {
    		return new Error(ErrorCode.ACTIVITY_SERVICES, ErrorType.PERSON_NOT_FOUND, "Unknown owner " + act.owner).toString();
    	}
    	
    	activity.setOwner(owner);
		activity.setName(act.name);
		activity.setDescription(act.description);
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
		try {
			activity.setDate(format.parse(act.date));
		} 	catch (ParseException e) {
			e.printStackTrace();
			return new Error(ErrorCode.ACTIVITY_SERVICES, ErrorType.BAD_DATE_FORMAT, e.getMessage()).toString();
		}
		
		activity.getParticipants().clear();		
		act.participants.forEach(pId -> {
			Person participant = personRepo.findById(pId);
			if (participant != null) {
				activity.addParticipant(participant);
			}
		});
		
		activityRepo.save(activity);
		
		return "OK";
    }
	
	/**
	 * Lists all activities which a person has participated in
	 * @param personId The Id of the person
	 * @return List of ActivityOut
	 */
	@RequestMapping(value="/activitiesbyparticipant", method=RequestMethod.GET)
    public List<ActivityOut> getActivitiesByParticipant(@RequestParam(value="pId", defaultValue="0") long personId) {
    	
		List<ActivityOut> result = new ArrayList<>();
		Person person = personRepo.findById(personId);
		if (person != null) {
			List<Activity> activitiesList = activityRepo.findByParticipantsIn(person);
			if (activitiesList != null) {
				activitiesList.forEach(a->result.add(new ActivityOut(a)));
			}	else {
				result.add(new ActivityOut(
						new Activity()
						.setError(eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found"))));
			}
		} 	else {
			result.add(new ActivityOut(
					new Activity()
					.setError(eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.PERSON_NOT_FOUND, "Owner not found"))));
		}		

		return result;
    }
	
	/**
	 * Adds a new paticipant to an activity
	 * @param activityId The Id of the activity
	 * @param personId The Id of the person
	 * @return ActivityOut
	 */
	@RequestMapping(value="/addparticipant", method=RequestMethod.GET)
    public ActivityOut addParticipant(@RequestParam(value="aId", defaultValue="0") long activityId,
    								  @RequestParam(value="pId", defaultValue="0") long personId) {
    	
		Activity activity = activityRepo.findById(activityId);
		if (activity != null) {
			Person person = personRepo.findById(personId);
			if (person != null) {
				activity.addParticipant(person);
				activityRepo.save(activity);
				return new ActivityOut(activity);
			}	else {
				new ActivityOut(
						activity.setError(eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")));
			}
		}	else {
			return new ActivityOut(
					new Activity()
					.setError(eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found")));
		}	

		return new ActivityOut(activity);
    }
	
	/**
	 * Removes a paticipant from an activity
	 * @param activityId The Id of the activity
	 * @param personId The Id of the person
	 * @return ActivityOut
	 */
	@RequestMapping(value="/removeparticipant", method=RequestMethod.GET)
    public ActivityOut removeParticipant(@RequestParam(value="aId", defaultValue="0") long activityId,
    									 @RequestParam(value="pId", defaultValue="0") long personId) {
    	
		Activity activity = activityRepo.findById(activityId);
		if (activity != null) {
			Person person = personRepo.findById(personId);
			if (person != null) {
				activity.removeParticipant(person);
				activityRepo.save(activity);
				return new ActivityOut(activity);
			}	else {
				new ActivityOut(
						activity.setError(eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")));
			}
		}	else {
			return new ActivityOut(
					new Activity()
					.setError(eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found")));
		}	

		return new ActivityOut(activity);
    }
	
	/**
	 * Removes all paticipants from an activity
	 * @param activityId The Id of the activity
	 * @return ActivityOut
	 */
	@RequestMapping(value="/removeallparticipants", method=RequestMethod.GET)
    public ActivityOut removeAllParticipants(@RequestParam(value="aId", defaultValue="0") long activityId) {
    	
		Activity activity = activityRepo.findById(activityId);
		
		if (activity != null) {
			for (Person person : activity.getParticipants()) {
				if (person != null) {
					activity.removeParticipant(person);
					activityRepo.save(activity);
					return new ActivityOut(activity);
				}	else {
					new ActivityOut(
							activity.setError(eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")));
				}
			}
		}	else {
			return new ActivityOut(
					new Activity()
					.setError(eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found")));
		}	

		return new ActivityOut(activity);
    }
	
	/**
	 * Lists all activities containing a given name
	 * @param name The name or part of it
	 * @return List of ActivityOut
	 */
	@RequestMapping(value="/activitiesbyname", method=RequestMethod.GET)
    public List<ActivityOut> getActivitiesByName(@RequestParam(value="name", defaultValue="") String name) {
    	
		List<ActivityOut> result = new ArrayList<>();
		activityRepo.findByNameContaining(name).forEach(a -> result.add(new ActivityOut(a)));
		return result;
    }
}
