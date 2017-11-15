package equalizer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import equalizer.model.Person;

@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {

	Person findByEmail(@Param("email") String email);
	
	Person findById(@Param("id") long id);
	
	List<Person> findByLastName(@Param("name") String name);
	
	List<Person> findByLastNameContaining(@Param("name") String name);
	
	List<Person> findByFirstName(@Param("name") String name);
	
	List<Person> findByFirstNameContaining(@Param("name") String name);

}
