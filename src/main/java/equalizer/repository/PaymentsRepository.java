package equalizer.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import equalizer.model.Activity;
import equalizer.model.Payments;
import equalizer.model.Person;

@RepositoryRestResource(collectionResourceRel = "payments", path = "payments")
public interface PaymentsRepository extends PagingAndSortingRepository<Payments, Long> {

	List<Payments> findByActivity(@Param("activity") Activity activity);
	
	List<Payments> findByFrom(@Param("from") Person from);
	
	List<Payments> findByTo(@Param("to") Person to);
	
	@Transactional
	List<Payments> removeByActivity(@Param("activity") Activity activity);
	
	@Transactional
	long deleteByActivity(@Param("activity") Activity activity);
		
	@Query("SELECT p FROM Payments p WHERE p.activity.id = :activityId")
	List<Payments> findByActivityId (@Param("activityId") Long activityId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Payments p WHERE p.activity.id = :activityId")
	void removeByActivityId (@Param("activityId") Long activityId);
	
}
