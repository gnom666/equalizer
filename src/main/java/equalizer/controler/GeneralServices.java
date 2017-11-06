package equalizer.controler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import equalizer.config.EqualizerConfiguration;
import equalizer.controlermodel.Constants.RoleType;
import equalizer.model.Activity;
import equalizer.model.Person;
import equalizer.model.Role;
import equalizer.model.Task;
import equalizer.repository.ActivityRepository;
import equalizer.repository.PaymentsRepository;
import equalizer.repository.PersonRepository;
import equalizer.repository.RoleRepository;
import equalizer.repository.TaskRepository;

@RestController
@RequestMapping("/general")
public class GeneralServices {
	
	@Autowired
	private ActivityRepository activityRepo;
	
	@Autowired
	private TaskRepository taskRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private PaymentsRepository paymentRepo;
	
	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private EqualizerConfiguration eConf;
	
	@RequestMapping(value="/init", method=RequestMethod.GET)
    public String init() {
    	StringBuffer result = new StringBuffer();
    	Role roleAdm = new Role();
    	Role roleAUser = new Role();
    	Role roleCUser = new Role();
    	Role roleGuest = new Role();
    	Person personA = new Person();
    	Person personB = new Person();
    	Person personC = new Person();
    	Person personD = new Person();
    	Activity halloween = new Activity();
    	Task drinks = new Task();
    	Task rent = new Task();
    	Task transportation = new Task();
    	Task food = new Task();
    	Task decoration = new Task();
    	
    	//* Alta de roles de prueba
    	try {
    		result.append("Alta de roles de prueba...");
    		
	    	roleAdm.setRoleType(RoleType.ADMINISTRATOR);
	    	roleAUser.setRoleType(RoleType.ADVANCED_USER);
	    	roleCUser.setRoleType(RoleType.COMMON_USER);
	    	roleGuest.setRoleType(RoleType.GUEST);
	    	
	    	roleRepo.save(roleAdm);
	    	roleRepo.save(roleAUser);
	    	roleRepo.save(roleCUser);
	    	roleRepo.save(roleGuest);
	    	
    	}	catch (Exception e) {
    		result.append("ERROR\n");
    		return result.toString();
    	}
    	result.append("OK\n");
    	//*/
    	
    	//* Alta de personas de prueba
    	try {
    		result.append("Alta de personas de prueba...");
    		
    		personA.setFirstName("A.");
	    	personA.setLastName("Alce");
	    	personA.setPassword("apass");
	    	personA.setEmail("a@eq.com");
	    	personA.setNumpers(1);
	    	personA.setRole(roleAdm);
	    	personA.setEnabled(true);
	    	personB.setFirstName("B.");
	    	personB.setLastName("Beluga");
	    	personB.setPassword("bpass");
	    	personB.setEmail("b@eq.com");
	    	personB.setNumpers(2);
	    	personB.setRole(roleCUser);
	    	personB.setEnabled(true);
	    	personC.setFirstName("C.");
	    	personC.setLastName("Castor");
	    	personC.setPassword("cpass");
	    	personC.setEmail("c@eq.com");
	    	personC.setNumpers(2);
	    	personC.setRole(roleCUser);
	    	personC.setEnabled(true);
	    	personD.setFirstName("D.");
	    	personD.setLastName("Dodo");
	    	personD.setPassword("dpass");
	    	personD.setEmail("d@eq.com");
	    	personD.setNumpers(3);
	    	personD.setRole(roleGuest);
	    	personD.setEnabled(true);
	    	  	
	    	personRepo.save(personA);
	    	personRepo.save(personB);
	    	personRepo.save(personC);
	    	personRepo.save(personD);
	    	
    	}	catch (Exception e) {
    		result.append("ERROR\n");
    		return result.toString();
    	}
    	result.append("OK\n");
    	//*/
    	
    	//* Alta de actividades de prueba
    	try {
    		result.append("Alta de actividades de prueba...");
    		
	    	halloween.setDate(new Date());
	    	halloween.setName("Halloween");
	    	halloween.setOwner(personA);
	    	halloween.setCalculated(false);
	    	halloween.setTotal(0);
	    	halloween.setDescription("Halloween party");
	    	
	    	activityRepo.save(halloween);
    	
    	}	catch (Exception e) {
    		result.append("ERROR\n");
    		return result.toString();
    	}
    	result.append("OK\n");
    	//*/
    	
    	//* Alta de participantes de prueba
    	try {
    		result.append("Alta de participantes de prueba...");
    		
    		List<Person> participants = new ArrayList<>();
    		participants.add(personA);
    		participants.add(personB);
    		participants.add(personC);
    		participants.add(personD);
    		halloween.setParticipants(participants);
    		
    		activityRepo.save(halloween);
    	
    	}	catch (Exception e) {
    		result.append("ERROR\n");
    		return result.toString();
    	}
    	result.append("OK\n");
    	//*/
    	
    	//* Alta de tareas de prueba
    	try {
    		result.append("Alta de tareas de prueba...");
   
	    	drinks.setActivity(halloween);
	    	drinks.setName("Bebidas");
	    	drinks.setAmmount(100);
	    	drinks.setCalculated(false);
	    	drinks.setDescription("Cervezas, ron y refrescos");
	    	drinks.setOwner(personA);
	    	rent.setActivity(halloween);
	    	rent.setName("Renta");
	    	rent.setAmmount(80);
	    	rent.setCalculated(false);
	    	rent.setDescription("Renta del local");
	    	rent.setOwner(personB);
	    	transportation.setActivity(halloween);
	    	transportation.setName("Taxi");
	    	transportation.setAmmount(120);
	    	transportation.setCalculated(false);
	    	transportation.setDescription("Alquiler de vehiculos");
	    	transportation.setOwner(personC);
	    	food.setActivity(halloween);
	    	food.setName("Comida");
	    	food.setAmmount(150);
	    	food.setCalculated(false);
	    	food.setDescription("Elaboracion de tapas");
	    	food.setOwner(personD);
	    	decoration.setActivity(halloween);
	    	decoration.setName("Adornos");
	    	decoration.setAmmount(30);
	    	decoration.setCalculated(false);
	    	decoration.setDescription("Decoracion del local");
	    	decoration.setOwner(personB);
	    	
	    	taskRepo.save(drinks);
	    	taskRepo.save(rent);
	    	taskRepo.save(transportation);
	    	taskRepo.save(food);
	    	taskRepo.save(decoration);
    	
    	}	catch (Exception e) {
    		result.append("ERROR\n");
    		return result.toString();
    	}
    	result.append("OK\n");
    	//*/
    	
    	
		return result.toString();
    }
	
}
