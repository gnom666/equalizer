package equalizer.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import equalizer.model.Person;
import equalizer.model.Registration;

@RepositoryRestResource(collectionResourceRel = "Registrations", path = "Registrations")
public interface RegistrationRepository extends PagingAndSortingRepository<Registration, Long> {

	List<Registration> findByToken(@Param("token") String token);
	
	Registration findByPerson(@Param("person") Person person);
		
	Registration findById(@Param("id") long id);
	
	@Transactional
	List<Registration> removeByPerson(@Param("person") Person person);
		
}
