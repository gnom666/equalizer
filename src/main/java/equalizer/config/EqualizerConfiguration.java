package equalizer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import equalizer.controlermodel.Error;
import equalizer.controlermodel.Constants.*;

@Configuration
public class EqualizerConfiguration {
	
	@Autowired
	@Bean
	public Error lastError() {
		return new Error(ErrorCode.UNKNOWN, ErrorType.UNKNOWN, "");
	}
	
}
