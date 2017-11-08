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
import equalizer.model.Person;
import equalizer.repository.PersonRepository;
import equalizer.viewmodel.PersonOutPublic;

@RestController
@RequestMapping("/people")
public class PersonServices {

	@Autowired
	private PersonRepository personRepo;

	@Autowired
	private EqualizerConfiguration eConf;
	
	@RequestMapping(value="/userslist", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public List<PersonOutPublic> usersList() {
		List<PersonOutPublic> all = new ArrayList<>();
		personRepo.findAll().forEach(p -> all.add(new PersonOutPublic(p)));
		return all;
    }
	
	@RequestMapping(value="/checkuser", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public PersonOutPublic checkUser(@RequestParam(value="uName", defaultValue="0") String userName) {
    	Person person = personRepo.findByEmail(userName);
		if (person != null) {
			return new PersonOutPublic(person);
		}
		return new PersonOutPublic(
				new Person()
				.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")));
    }
	
	@RequestMapping(value="/checkpassword", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public PersonOutPublic checkPassword(@RequestParam(value="uId", defaultValue="0") long userId, 
    									 @RequestParam(value="uPass", defaultValue="0") String password) {
    	Person person = personRepo.findById(userId);
		if (person != null) {
			if (person.getPassword().equals(password)) {
				return new PersonOutPublic(person);
			}	else {
				return new PersonOutPublic(
						new Person()
						.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.INCORRECT_PASSWORD, "Incorrect password")));
			}
		}
		return new PersonOutPublic(
				new Person()
				.setError(eConf.lastError().updateError(ErrorCode.PERSON_SERVICES, ErrorType.PERSON_NOT_FOUND, "Person not found")));
    }
}
