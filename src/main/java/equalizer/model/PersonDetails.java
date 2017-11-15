package equalizer.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import equalizer.controlermodel.Constants;

/**
 * Class used for OAuth to connect with a Person
 * @author jorgerios
 *
 */
public class PersonDetails implements UserDetails {

	private String username;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	private boolean enabled;
	
	public PersonDetails(Person person) {
		this.username = person.getEmail();
		this.password = person.getPassword();
		this.enabled = person.isEnabled();
		
		List<GrantedAuthority> auth = new ArrayList<>();
		auth.add(new SimpleGrantedAuthority(Constants.roleTypeName(person.getRole().getRoleType())));
		this.authorities = auth;
	}
	
	public PersonDetails() {
		this.authorities = new ArrayList<>();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
