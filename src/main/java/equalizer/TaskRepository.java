package equalizer;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "tasks", path = "tasks")
public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {

	List<Task> findByName(@Param("name") String name);

	@Query(value="SELECT * FROM Task WHERE Task.activity_id=:ida AND Task.owner_id=:idp", nativeQuery=true)
	List<Task> findByActivityAndPerson (@Param("ida") Long ida, @Param("idp") Long idp);
	
}
