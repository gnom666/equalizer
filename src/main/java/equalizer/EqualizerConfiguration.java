package equalizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import equalizer.controlermodel.Error;
import equalizer.controlermodel.Constants.*;

@Configuration
public class EqualizerConfiguration {
	
	@Bean
	public Error lastError() {
		return new Error(ErrorCode.UNKNOWN, ErrorType.UNKNOWN, "");
	}
	
	@Bean
	public EntityManagerBean entityManagerBean() {
	    return new EntityManagerBean();
	}
	
}
