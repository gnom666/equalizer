package equalizer.controler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import equalizer.EqualizerConfiguration;
import equalizer.controlermodel.Error;
import equalizer.controlermodel.Node;
import equalizer.controlermodel.Row;
import equalizer.controlermodel.Constants.ErrorCode;
import equalizer.controlermodel.Constants.ErrorType;
import equalizer.model.Activity;
import equalizer.model.Person;
import equalizer.repository.ActivityRepository;
import equalizer.repository.PaymentsRepository;
import equalizer.repository.PersonRepository;
import equalizer.viewmodel.ActivityOut;
import equalizer.viewmodel.PaymentOut;


@RestController
public class ActivitiesServices {
	
	@Autowired
	private ActivityRepository activityRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private PaymentsRepository paymentsRepo;
	
	@Autowired
	private EqualizerConfiguration eConf;
	
	@RequestMapping(value="/activitiesbyowner", method=RequestMethod.GET)
    public List<ActivityOut> getActivitiesByOwner(@RequestParam(value="oId", defaultValue="") String ownerId) {
    	
		Person owner = personRepo.findById(Long.decode(ownerId));
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
    public List<ActivityOut> getActivitiesByParticipant(@RequestParam(value="pId", defaultValue="") String personId) {
    	
		Person person = personRepo.findById(Long.decode(personId));
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
