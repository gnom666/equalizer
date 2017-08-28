package equalizer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@RepositoryRestResource(collectionResourceRel = "activities", path = "activities")
public interface ActivityRepository extends PagingAndSortingRepository<Activity, Long> {

	List<Activity> findByName(@Param("name") String name);
	
	Activity findById(@Param("id") Long id);
	
	
}
