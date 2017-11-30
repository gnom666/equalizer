package equalizer.controler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import equalizer.viewmodel.PersonOut;

/**
 * Person Services
 * @author jorgerios
 *
 */
@RestController
@RequestMapping("/people")
public class PersonServices {

	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private ActivityRepository activityRepo;

	@Autowired
	private EqualizerConfiguration eConf;
	
	/**
	 * Check if a person is included in a list
	 * @param collection The list of persons
	 * @param person The Person
	 * @return true if any PersonOut.id in the collection match the Person.id, false otherwise  
	 */
	public boolean containsPerson (Collection<PersonOut> collection, Person person) {
		for (PersonOut p : collection ) {
			if (p.id == person.getId()) return true;
		};
		return false;
	}
	
	/**
	 * Lists all the persons
	 * @return List of PersonOut
	 */
	@RequestMapping(value="/userslist", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PersonOut> usersList() {
		List<PersonOut> all = new ArrayList<>();
		personRepo.findAll().forEach(p -> all.add(new PersonOut(p).toPublic()));
		return all;
    }
	
	/**
	 * Lists the persons with first name or last name containing a specific name
	 * @param name The name to search for
	 * @return Set of unrepeated PersonOut
	 */
	@RequestMapping(value="/usersbyname", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public Set<PersonOut> usersList(@RequestParam(value="name", defaultValue="0") String name) {
		Set<PersonOut> all = new LinkedHashSet<>();
		personRepo.findByFirstNameContaining(name).forEach(p -> all.add(new PersonOut(p).toPublic()));
		personRepo.findByLastNameContaining(name).forEach(p -> {
			if (!containsPerson(all, p)) all.add(new PersonOut(p).toPublic());
		});
		return all;
    }
	
	/**
	 * Gets a person by its id
	 * @param id The Id of a person
	 * @return PersonOut
	 */
	@RequestMapping(value="/userbyid", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public PersonOut userById(@RequestParam(value="pId", defaultValue="0") long id) {
    	Person person = personRepo.findById(id);
		if (person != null) {
			return new PersonOut(person).toPublic();
		}
		return new PersonOut(
				new Person()
				.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")))
				.toPublic();
    }
	
	@RequestMapping(value="/participantsbyact", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PersonOut> participantsByActivity(@RequestParam(value="aId", defaultValue="0") long id) {
    	ArrayList<PersonOut> participants = new ArrayList<>();
		Activity activity = activityRepo.findById(id);
    	if (activity != null) {
    		activity.getParticipants().forEach(p -> {
    			participants.add(new PersonOut(p));
    		});
    	}
    	return participants;
    }
	
	/**
	 * Gets the list of contacts of a person by its id
	 * @param id The Id of a person
	 * @return List<Long>
	 */
	@RequestMapping(value="/contactsidsbyid", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<Long> contactsIdsById(@RequestParam(value="pId", defaultValue="0") long id) {
    	Person person = personRepo.findById(id);
		if (person != null) {
			return new PersonOut(person).contacts;
		}
		return new ArrayList<>();
    }
	
	/**
	 * Gets the list of persons with this person as contact
	 * @param id The Id of a person
	 * @return List<Long>
	 */
	@RequestMapping(value="/contactofidsbyid", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<Long> contactOfIdsById(@RequestParam(value="pId", defaultValue="0") long id) {
    	Person person = personRepo.findById(id);
		if (person != null) {
			return new PersonOut(person).contactOf;
		}
		return new ArrayList<>();
    }
	
	/**
	 * Gets the list of contacts of a person by its id
	 * @param id The Id of a person
	 * @return List<PersonOut>
	 */
	@RequestMapping(value="/contactsbyid", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PersonOut> contactsById(@RequestParam(value="pId", defaultValue="0") long id) {
    	List<PersonOut> contacts = new ArrayList<>();		
		Person person = personRepo.findById(id);
		if (person != null) {
			person.getContacts().forEach(p -> contacts.add(new PersonOut(p)));
		}
		return contacts;
    }
	
	/**
	 * Gets the list of persons with this person as contact
	 * @param id The Id of a person
	 * @return List<PersonOut>
	 */
	@RequestMapping(value="/contactofbyid", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PersonOut> contactOfById(@RequestParam(value="pId", defaultValue="0") long id) {
		List<PersonOut> contactOf = new ArrayList<>();		
		Person person = personRepo.findById(id);
		if (person != null) {
			person.getContactOf().forEach(p -> contactOf.add(new PersonOut(p)));
		}
		return contactOf;
    }
	
	/**
	 * Sets friends A-->B and B-->A
	 * @param id1 Person A id
	 * @param id2 Person B id
	 * @return List<PersonOut>
	 */
	@RequestMapping(value="/setfriends", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PersonOut> setFrinds(@RequestParam(value="person1Id", defaultValue="0") long id1, 
    								 @RequestParam(value="person2Id", defaultValue="0") long id2) {
		List<PersonOut> friends = new ArrayList<>();		
		Person person1 = personRepo.findById(id1);
		Person person2 = personRepo.findById(id2);
		Error error = new Error(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person with id not found");
		
		if (person1 != null) {
			friends.add(new PersonOut(person1));
		}	else {
			friends.add(new PersonOut(new Person().setError(error)));
		}
		if (person2 != null) {
			friends.add(new PersonOut(person2));
		}	else {
			friends.add(new PersonOut(new Person().setError(error)));
		}
		if (person1 != null && person2 != null) {
			person1.addToContacts(person2);
			person2.addToContacts(person1);
			personRepo.save(person1);
			personRepo.save(person2);
		}
		return friends;
    }
	
	/**
	 * Unsets friends A-x>B and B-x>A
	 * @param id1 Person A id
	 * @param id2 Person B id
	 * @return List<PersonOut>
	 */
	@RequestMapping(value="/unsetfriends", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PersonOut> unsetFriends(@RequestParam(value="person1Id", defaultValue="0") long id1, 
    									@RequestParam(value="person2Id", defaultValue="0") long id2) {
		List<PersonOut> friends = new ArrayList<>();		
		Person person1 = personRepo.findById(id1);
		Person person2 = personRepo.findById(id2);
		Error error = new Error(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person with id not found");
		
		if (person1 != null) {
			friends.add(new PersonOut(person1));
		}	else {
			friends.add(new PersonOut(new Person().setError(error)));
		}
		if (person2 != null) {
			friends.add(new PersonOut(person2));
		}	else {
			friends.add(new PersonOut(new Person().setError(error)));
		}
		if (person1 != null && person2 != null) {
			person1.removeFromContacts(person2);
			//person1.removeFromContactOf(person2);
			person2.removeFromContacts(person1);
			//person2.removeFromContactOf(person1);
			personRepo.save(person1);
			personRepo.save(person2);
		}
		return friends;
    }
	
	/**
	 * Checks a person's username (ie email)
	 * @param user The username of the Person
	 * @return PersonOut
	 */
	@RequestMapping(value="/checkuser", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public PersonOut checkUser(@RequestParam(value="u", defaultValue="") String user) {
    	Person person = personRepo.findByEmail(user);
		if (person != null) {
			return new PersonOut(person).toPublic();
		}
		return new PersonOut(
				new Person()
				.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")))
				.toPublic();
    }
	
	/**
	 * Checks a person's password given it's id
	 * @param userId The Id of the person
	 * @param password The corresponding password
	 * @return PersonOut 
	 */
	@RequestMapping(value="/checkpassword", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public PersonOut checkPassword(@RequestParam(value="uId", defaultValue="0") long userId, 
    									 @RequestParam(value="uPass", defaultValue="0") String password) {
    	Person person = personRepo.findById(userId);
		if (person != null) {
			if (person.getPassword().equals(password)) {
				return new PersonOut(person).toPublic();
			}	else {
				return new PersonOut(
						new Person()
						.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.INCORRECT_PASSWORD, "Incorrect password")))
						.toPublic();
			}
		}
		return new PersonOut(
				new Person()
				.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")))
				.toPublic();
    }
	
	/**
	 * Checks a person's password given it's username (ie email)
	 * @param userId The username of the person
	 * @param password The corresponding password
	 * @return PersonOut 
	 */
	@RequestMapping(value="/checkuserandpass", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public PersonOut checkPassword(@RequestParam(value="uName", defaultValue="0") String username, 
    									 @RequestParam(value="uPass", defaultValue="0") String password) {
    	Person person = personRepo.findByEmail(username);
		if (person != null) {
			if (person.getPassword().equals(password)) {
				return new PersonOut(person).toPublic();
			}	else {
				return new PersonOut(
						new Person()
						.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.INCORRECT_PASSWORD, "Incorrect password")))
						.toPublic();
			}
		}
		return new PersonOut(
				new Person()
				.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")))
				.toPublic();
    }
}
