package equalizer.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import equalizer.model.Activity;
import equalizer.model.Person;
import equalizer.model.Task;

@RepositoryRestResource(collectionResourceRel = "tasks", path = "tasks")
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {

	List<Task> findByName(@Param("name") String name);
	
	List<Task> findByActivity(@Param("activity") Activity activity);
	
	List<Task> findByOwner(@Param("owner") Person owner);
	
	Task findById(@Param("id") long id);
	
	@Transactional
	List<Task> removeByActivity(@Param("activity") Activity activity);
	
	@Transactional
	List<Task> removeByOwner(@Param("owner") Person owner);
	
	List<Task> findByActivityAndOwner (@Param("activity") Activity activity, @Param("owner") Person owner);

	@Query(value="SELECT * FROM Task WHERE Task.activity_id=:ida AND Task.owner_id=:idp", nativeQuery=true)
	List<Task> findByActivityAndPerson (@Param("ida") Long ida, @Param("idp") Long idp);
	
}
