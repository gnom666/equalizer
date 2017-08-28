package equalizer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tmp_model.Row;


@RestController
public class PaymentsServices {
	
	@Autowired
	private ActivityRepository activityRepo;
	
	@Autowired
	private PersonRepository personRepo;
	
	@RequestMapping(value="/generatepayments", method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    public String generatePayments(@RequestParam(value="ida", defaultValue="") String idActivity, @RequestParam(value="idp", defaultValue="") String idPerson) {
    	
		Activity act = activityRepo.findById(Long.decode(idActivity));
		Person per = personRepo.findById(Long.decode(idPerson));
		if (act != null && per!= null && per.getId() == act.getOwner().getId()) {
			ArrayList<Row> table = new ArrayList<>();
			
			act.getPersons ().forEach(p->{
				Row row = new Row();
				row.idPerson = p.getId();
				row.numPersons = p.getNumpers();
				table.add(row);
			});
			
			act.getTasks().forEach(t->{
				
			});
		}
		
		//p.setEmail("loquesea@algo.com");
		//person.save(p);
		
		return "error";
    }
	
}
