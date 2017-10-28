package equalizer.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import equalizer.model.Activity;
import equalizer.model.Payment;
import equalizer.model.Person;

@RepositoryRestResource(collectionResourceRel = "payments", path = "payments")
public interface PaymentsRepository extends PagingAndSortingRepository<Payment, Long> {

	List<Payment> findByActivity(@Param("activity") Activity activity);
	
	List<Payment> findByFrom(@Param("from") Person from);
	
	List<Payment> findByTo(@Param("to") Person to);
	
	Payment findById(@Param("id") Long id);
	
	@Transactional
	List<Payment> removeByActivity(@Param("activity") Activity activity);
	
	@Transactional
	long deleteByActivity(@Param("activity") Activity activity);
		
	@Query("SELECT p FROM Payment p WHERE p.activity.id = :activityId")
	List<Payment> findByActivityId (@Param("activityId") Long activityId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Payment p WHERE p.activity.id = :activityId")
	void removeByActivityId (@Param("activityId") Long activityId);
	
}
