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
    	
		Person owner = personRepo.findById(ownerId);
		if (owner != null) {
			List<Activity> activitiesList = activityRepo.findByOwner(owner);
			if (activitiesList != null) {
				List<ActivityOut> activitiesOutList = new ArrayList<>();
				activitiesList.forEach(a->activitiesOutList.add(new ActivityOut(a)));
				return activitiesOutList;
			}	else {
				eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found");
			}
		} 	else {
			eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.PERSON_NOT_FOUND, "Owner not found");
		}		
		
		return null;
    }
	
	@RequestMapping(value="/activitiesbyparticipant", method=RequestMethod.GET)
    public List<ActivityOut> getActivitiesByParticipant(@RequestParam(value="pId", defaultValue="0") long personId) {
    	
		Person person = personRepo.findById(personId);
		if (person != null) {
			List<Activity> activitiesList = activityRepo.findByParticipantsIn(person);
			if (activitiesList != null) {
				List<ActivityOut> activitiesOutList = new ArrayList<>();
				activitiesList.forEach(a->activitiesOutList.add(new ActivityOut(a)));
				return activitiesOutList;
			}	else {
				eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.ACTIVITY_NOT_FOUND, "Activity not found");
			}
		} 	else {
			eConf.lastError().updateError(ErrorCode.ACTIVITY_SERVICES, ErrorType.PERSON_NOT_FOUND, "Owner not found");
		}		

		return null;
    }
}
