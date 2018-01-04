package equalizer.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import equalizer.model.Attachment;
import equalizer.model.Task;

@RepositoryRestResource(collectionResourceRel = "attachments", path = "attachments")
public interface AttachmentRepository extends PagingAndSortingRepository<Attachment, Long> {

	List<Attachment> findByName(@Param("name") String name);
	
	Attachment findByTask(@Param("task") Task task);
		
	Attachment findById(@Param("id") long id);
	
	@Transactional
	List<Attachment> removeByTask(@Param("task") Task task);
		
}
