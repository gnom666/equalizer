package equalizer.controler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import equalizer.config.EqualizerConfiguration;
import equalizer.controlermodel.Constants;
import equalizer.controlermodel.Constants.ErrorCode;
import equalizer.controlermodel.Constants.ErrorType;
import equalizer.controlermodel.Constants.RegistrationStatus;
import equalizer.controlermodel.Constants.RoleType;
import equalizer.controlermodel.Error;
import equalizer.model.Activity;
import equalizer.model.Person;
import equalizer.model.Registration;
import equalizer.model.Role;
import equalizer.repository.ActivityRepository;
import equalizer.repository.PaymentsRepository;
import equalizer.repository.PersonRepository;
import equalizer.repository.RegistrationRepository;
import equalizer.repository.RoleRepository;
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
	private RoleRepository roleRepo;
	
	@Autowired
	private PaymentsRepository paymentsRepo;
	
	@Autowired
	private RegistrationRepository regRepo;

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
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
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
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
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
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
    	Person person = personRepo.findById(id);
		if (person != null) {
			return new PersonOut(person).toPublic();
		}
		return new PersonOut(
				new Person()
				.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")))
				.toPublic();
    }
	
	/**
	 * Gets the partcipants of one Activity
	 * @param id The Id of the Activity
	 * @return List<PersonOut>
	 */
	@RequestMapping(value="/participantsbyact", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PersonOut> participantsByActivity(@RequestParam(value="aId", defaultValue="0") long id) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
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
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
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
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
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
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
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
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
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
    public List<PersonOut> setFriends(@RequestParam(value="person1Id", defaultValue="0") long id1, 
    								 @RequestParam(value="person2Id", defaultValue="0") long id2) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
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
	 * Sets friends A-->B and B-->A
	 * @param id1 Person A id
	 * @param id2 Person B id
	 * @return List<PersonOut>
	 */
	@RequestMapping(value="/setfriendbyemail", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public PersonOut setFriendByEmail(@RequestParam(value="pId", defaultValue="0") long id, 
    								 @RequestParam(value="email", defaultValue="") String email) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Person person1 = personRepo.findById(id);
		Person person2 = personRepo.findByEmail(email);
		
		if (person1 == null) {
			return new PersonOut(
					new Person()
					.setError(
							new Error(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Main person not found")));
		}
		if (person2 == null) {
			return new PersonOut(
					new Person()
					.setError(
							new Error(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Friend not found by email")));
		}
		if (person1.getId() == person2.getId()) {
			return new PersonOut(
					new Person()
					.setError(
							new Error(ErrorCode.PERSON_SERVICES, ErrorType.UNKNOWN, "Friend is the same person")));
		}
		
		person1.addToContacts(person2);
		person2.addToContacts(person1);
		personRepo.save(person1);
		personRepo.save(person2);
		
		return new PersonOut(person2).toPublic();
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
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
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
	 * Unsets friends A-x>B and B-x>A
	 * @param id1 Person A id
	 * @param id2 Person B id
	 * @return List<PersonOut>
	 */
	@RequestMapping(value="/unsetfriendsbyemail", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PersonOut> unsetFriendsByEmail(@RequestParam(value="person1Id", defaultValue="0") long id, 
    									@RequestParam(value="person2email", defaultValue="") String email) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		List<PersonOut> friends = new ArrayList<>();		
		Person person1 = personRepo.findById(id);
		Person person2 = personRepo.findByEmail(email);
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
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
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
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
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
	 * @param username The username of the person
	 * @param password The corresponding password
	 * @return PersonOut 
	 */
	@RequestMapping(value="/checkuserandpass", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public PersonOut checkPassword(@RequestParam(value="uName", defaultValue="0") String username, 
    									 @RequestParam(value="uPass", defaultValue="0") String password) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Person person = personRepo.findByEmail(username);
		if (person != null) {
			if (person.getPassword().equals(password)) {
				if (person.isEnabled()) {
					return new PersonOut(person).toPublic();
				}	else {
					return new PersonOut(
							new Person()
							.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_DISABLED, "Person not enabled")))
							.toPublic();
				}
			}	else {
				if (person.getRegistration() != null) {
					person.getRegistration().regenerateToken();
					person.getRegistration().setStatus(RegistrationStatus.FORGOTTEN);
					regRepo.save(person.getRegistration());
				}	else {
					Registration registration = new Registration();
					registration.setStatus(RegistrationStatus.FORGOTTEN);
					regRepo.save(registration);
					person.setRegistration(registration);
				}
				personRepo.save(person);
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
	 * Add a new Person
	 * @param p The Person
	 * @return PersonOut
	 */
	@RequestMapping(value="/addperson", method=RequestMethod.POST)
    public PersonOut addPerson(@RequestBody PersonOut p) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Person person = personRepo.findByEmail(p.email);
		
		if (person != null) {
			return new PersonOut(
					new Person()
					.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.EXISTENT_DATA, "Existent email " + p.email)))
					.toPublic();
		}
		
		person = new Person();
		person.setEmail(p.email);
		person.setFirstName(p.firstName);
		person.setLastName(p.lastName);
		person.setNumpers(p.numpers);
		person.setPassword(p.password);
		person.setEnabled(false);
		
		Registration registration = new Registration();
		regRepo.save(registration);
		person.setRegistration(registration);
		
		List<Role> roles = roleRepo.findByRoleType(RoleType.COMMON_USER);
		if(roles == null || roles.size() < 1) {
			Role role = new Role();
			role.setRoleType(RoleType.COMMON_USER);
			roleRepo.save(role);
			person.setRole(role);
		}	else {
			person.setRole(roles.get(0));
		}
		
		personRepo.save(person);
		
		return new PersonOut(person).toPublic();
    }
	
	/**
	 * Add a new Person
	 * @param p The Person
	 * @return {@link PersonOut}
	 */
	@RequestMapping(value="/addgoogleuser", method=RequestMethod.POST)
    public PersonOut addGoogleUser(@RequestBody PersonOut p) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Person person = personRepo.findByEmail(p.email);
		
		if (person != null) {
			return new PersonOut(person).toPublic();
		}
		
		person = new Person();
		person.setEmail(p.email);
		person.setFirstName(p.firstName);
		person.setLastName(p.lastName);
		person.setNumpers(p.numpers);
		person.setPassword(p.password);
		person.setEnabled(true);
		
		Registration registration = new Registration();
		registration.setStatus(RegistrationStatus.OK);
		regRepo.save(registration);
		person.setRegistration(registration);
		
		List<Role> roles = roleRepo.findByRoleType(RoleType.COMMON_USER);
		if(roles == null || roles.size() < 1) {
			Role role = new Role();
			role.setRoleType(RoleType.COMMON_USER);
			roleRepo.save(role);
			person.setRole(role);
		}	else {
			person.setRole(roles.get(0));
		}
		
		personRepo.save(person);
		
		return new PersonOut(person).toPublic();
    }
	
	/**
	 * Send mail
	 * @param user The email of the person
	 * @return {@link PersonOut} 
	 */
	@RequestMapping(value="/sendmail", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public PersonOut sendMail(@RequestParam(value="user", defaultValue="0") String user) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Person person = personRepo.findByEmail(user);
		if (person == null) {
			return new PersonOut(
					new Person()
					.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")))
					.toPublic();
		}
		
		//sendMail(person.getEmail(), "http://192.168.1.109:9003/people/enable/?pId=" + person.getId() + "&t=" + registration.getToken());
		//Mail mailControler = new Mail();
		//mailControler.email = person.getEmail();
		//mailControler.text = "http://192.168.1.109:9003/people/enable/?pId=" + person.getId() + "&t=" + person.getRegistration().getToken();
		//mailControler.run();		
		if (person.getRegistration().getStatus() == RegistrationStatus.PENDING) {
			String IP = Constants.IP;
			/*try {
				IP = InetAddress.getLocalHost().getHostName();
			}	catch (UnknownHostException e) {
				eConf.logger().error(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());
			}*/
			EmailService es = new EmailService();
			es.emailSender = eConf.getJavaMailSender();
			es.sendSimpleMessage(person.getEmail(), "Enable registration", "http://" + IP + ":" + Constants.PORT + "/people/enable/?pId=" + person.getId() + "&t=" + person.getRegistration().getToken());
			
			person.getRegistration().setStatus(RegistrationStatus.SENT);
			regRepo.save(person.getRegistration());
			personRepo.save(person);
		}
		
		return new PersonOut(person).toPublic();
    }
	
	/**
	 * Send token
	 * @param user The email of the person
	 * @return PersonOut 
	 */
	@RequestMapping(value="/sendtoken", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public PersonOut sendToken(@RequestParam(value="user", defaultValue="0") String user) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Person person = personRepo.findByEmail(user);
		if (person == null) {
			return new PersonOut(
					new Person()
					.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")))
					.toPublic();
		}
		
		if (person.getRegistration().getStatus() == RegistrationStatus.FORGOTTEN) {
			String IP = Constants.IP;

			EmailService es = new EmailService();
			es.emailSender = eConf.getJavaMailSender();
			es.sendSimpleMessage(person.getEmail(), "Password reset token", person.getRegistration().getToken());
			
			person.getRegistration().setStatus(RegistrationStatus.SENT);
			regRepo.save(person.getRegistration());
			personRepo.save(person);
		}
		
		return new PersonOut(person).toPublic();
    }
	
	/**
	 * Modify a Person
	 * @param p The Person
	 * @return PersonOut
	 */
	@RequestMapping(value="/modifyperson", method=RequestMethod.POST)
    public PersonOut modifyPerson(@RequestBody PersonOut p) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Person person = personRepo.findById(p.id);
		System.out.println(p.toString());
		
		if (person == null) {
			return new PersonOut(
					new Person()
					.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")))
					.toPublic();
		}
		
		person.setEmail(p.email);
		person.setFirstName(p.firstName);
		person.setLastName(p.lastName);
		person.setPassword(p.password);
		if (person.getNumpers() != p.numpers) {
			person.setNumpers(p.numpers);
			person.getActivities().forEach(a -> {
				paymentsRepo.removeByActivity(a);
				a.setCalculated(false);
				a.getTasks().forEach(t -> t.setCalculated(false));
			});
		}
				
		personRepo.save(person);
		
		return new PersonOut(person).toPublic();
    }
	
	/**
	 * Delete a Person
	 * @param personId The Id of the person
	 * @return PersonOut
	 */
	@RequestMapping(value="/deleteperson", method=RequestMethod.GET)
    public PersonOut deletePerson(@RequestParam(value="pId", defaultValue="0") long personId) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		Person person = personRepo.findById(personId);
		if (person == null) {
			return new PersonOut(
					new Person()
					.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")))
					.toPublic();
		}
		
		person.setEnabled(false);
		
		personRepo.save(person);
		
		return new PersonOut(person);
    }
	
	/**
	 * Try to enable a person
	 * @param personId The Id of the person
	 * @param token The corresponding token
	 * @return String
	 */
	@RequestMapping(value="/enable", method=RequestMethod.GET)
    public String enablePerson(@RequestParam(value="pId", defaultValue="0") long personId, 
    		@RequestParam(value="t", defaultValue="") String token) {
		eConf.logger().log(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName());
		
		Person person = personRepo.findById(personId);
		if (person == null) {
			return "Person not found";
		}
		
		if (person.getRegistration() == null) {
			return "Registration not found";
		}
		
		if (!person.getRegistration().getToken().equals(token)) {
			return "Wrong token";
		}
		
		if (person.isEnabled()) {
			return "Already enabled";
		}
		
		long diff = (new Date()).getTime() - person.getRegistration().getDate().getTime();       
		long diffHours = diff / (60 * 60 * 1000); 
		if (diffHours > 2) {
			Registration old = person.getRegistration();
			person.setRegistration(null);
			personRepo.save(person);
			regRepo.delete(old);
			Registration registration = new Registration();
			regRepo.save(registration);
			person.setRegistration(registration);
			personRepo.save(person);
			String IP = Constants.IP;
			/*try {
				IP = InetAddress.getLocalHost().getHostName();
			}	catch (UnknownHostException e) {
				eConf.logger().error(this.getClass(), new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());
			}*/
			return "Expired token, try this: " + "http://" + IP + ":" + Constants.PORT + "/people/enable/?pId=" + person.getId() + "&t=" + person.getRegistration().getToken();
		}
		
		person.setEnabled(true);
		person.getRegistration().setStatus(RegistrationStatus.OK);
		regRepo.save(person.getRegistration());
		personRepo.save(person);
		
		return "User enabled";
    }
}
