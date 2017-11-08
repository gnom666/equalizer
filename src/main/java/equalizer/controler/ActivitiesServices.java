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
import equalizer.repository.ActivityRepository;
import equalizer.repository.PersonRepository;
import equalizer.viewmodel.ActivityOut;


@RestController
@RequestMapping("/activities")
public class ActivitiesServices {
	
	@Autowired
	private ActivityRepository activityRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private EqualizerConfiguration eConf;
	
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
}
