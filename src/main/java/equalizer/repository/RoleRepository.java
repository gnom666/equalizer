package equalizer.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import equalizer.controlermodel.Constants.RoleType;
import equalizer.model.Person;
import equalizer.model.Role;


@RepositoryRestResource(collectionResourceRel = "roles", path = "roles")
public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {

	List<Role> findByRoleType(@Param("roleType") RoleType roleType);
	
	Role findById(@Param("id") long id);
	
	List<Role> findByMembersIn(@Param("person") Person person);
}
