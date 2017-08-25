package equalizer;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "activity", path = "activity")
public interface ActivityRepository extends PagingAndSortingRepository<Activity, Long> {

	List<Person> findByName(@Param("name") String name);

}