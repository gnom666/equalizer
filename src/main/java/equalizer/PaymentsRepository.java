package equalizer;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "payments", path = "payments")
public interface PaymentsRepository extends PagingAndSortingRepository<Payments, Long> {

	List<Payments> findByActivity(@Param("activity") Activity activity);
	

}
