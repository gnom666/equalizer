package equalizer.viewmodel;

import java.util.ArrayList;

import java.util.List;

import equalizer.controlermodel.Constants.RoleType;
import equalizer.model.Role;
import equalizer.controlermodel.Error;

/**
 * Class to be used as interface of the Role class by the roleServices and personServices methods
 * @author jorgerios
 *
 */
public class RoleOut {
	
	public long id;
	public RoleType roleType;
	public List<Long> members;
	public Error error;
	
	public RoleOut(long id, RoleType roleType, List<Long> members, Error error) {
		this.id = id;
		this.roleType = roleType;
		this.members = members;
		this.error = error;
	}
	
	public RoleOut(Role role) {
		this.members = new ArrayList<>();
		
		if (role != null) {
			this.id = role.getId();
			this.roleType = role.getRoleType();
			role.getMembers().forEach(p -> this.members.add(p.getId()));
			this.error = role.getError();
		}
	}
		
}
