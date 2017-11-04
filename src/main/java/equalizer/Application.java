package equalizer;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import equalizer.model.PersonDetails;
import equalizer.repository.PersonRepository;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Autowired
	public void authenticationManager (AuthenticationManagerBuilder builder, PersonRepository personRepo) throws Exception {
		builder.userDetailsService(new UserDetailsService() {
			
			@Override
			public UserDetails loadUserByUsername(String arg0) throws UsernameNotFoundException {
				return new PersonDetails(personRepo.findByEmail(arg0).get());
			}
		});
	}
}
