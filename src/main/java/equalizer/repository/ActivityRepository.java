package equalizer.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import equalizer.model.Activity;
import equalizer.model.Person;


@RepositoryRestResource(collectionResourceRel = "activities", path = "activities")
public interface ActivityRepository extends PagingAndSortingRepository<Activity, Long> {

	List<Activity> findByName(@Param("name") String name);
	
	List<Activity> findByNameContaining(@Param("name") String name);
	
	Activity findById(@Param("id") long id);
	
	@Transactional
	Activity removeById(@Param("id") long id);
	
	List<Activity> findByOwner(@Param("owner") Person owner);
	
	List<Activity> findByParticipantsIn(@Param("person") Person person);
	
	List<Activity> findByParticipantsInOrderByDateDesc(@Param("person") Person person);
	
	/*@Query("SELECT a FROM Activity a WHERE a.owner.id = :ownerId")
	List<Activity> findByOwnerEmail(@Param("ownerId") String ownerId);*/
}
