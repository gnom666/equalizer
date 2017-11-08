package equalizer.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import equalizer.controlermodel.Error;
import equalizer.controlermodel.Constants.RoleType;

@Entity
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private long id;
	
	@Column(nullable = false, updatable = false, unique = true)
	private RoleType roleType;
	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id", scope=Person.class)
	@OneToMany(mappedBy = "role")
	private List<Person> members;

	@Transient
	private Error error = null;
	
	public Role() {
		this.members = new ArrayList<>();
		this.roleType = RoleType.GUEST;
	}

	public Error getError() {
		return error;
	}
	
	public long getId() {
		return id;
	}
	
	public List<Person> getMembers() {
		return members;
	}

	public RoleType getRoleType() {
		return roleType;
	}	

	public Role setError(Error error) {
		this.error = error;
		return this;
	}

	public void setMembers(List<Person> members) {
		this.members = members;
	}

	public void setRoleType(RoleType roleType) {
		this.roleType = roleType;
	}
	
}
