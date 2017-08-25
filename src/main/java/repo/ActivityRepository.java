package repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import model.Activity;

@RepositoryRestResource(collectionResourceRel = "activities", path = "activities")
public interface ActivityRepository extends PagingAndSortingRepository<Activity, Long> {

	List<Activity> findByName(@Param("name") String name);
	
	List<Activity> findById(@Param("id") Long id);
	

}
