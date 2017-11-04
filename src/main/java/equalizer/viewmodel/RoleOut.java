package equalizer.viewmodel;

import java.util.ArrayList;
import java.util.List;

import equalizer.controlermodel.Constants.RoleType;
import equalizer.model.Role;

public class RoleOut {
	
	public long id;
	public RoleType roleType;
	public List<Long> members;
	
	public RoleOut(long id, RoleType roleType, List<Long> members) {
		super();
		this.id = id;
		this.roleType = roleType;
		this.members = members;
	}
	
	public RoleOut(Role role) {
		this.members = new ArrayList<>();
		
		if (role != null) {
			this.id = role.getId();
			this.roleType = role.getRole();
			role.getMembers().forEach(p -> this.members.add(p.getId()));
		}
	}
		
}
