package equalizer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import equalizer.controlermodel.Error;
import equalizer.controlermodel.Logger;
import equalizer.controler.CustomErrorController;
import equalizer.controlermodel.Constants.*;

@Configuration
public class EqualizerConfiguration {
	
	@Autowired
	@Bean
	public Error lastError() {
		return new Error(ErrorCode.UNKNOWN, ErrorType.UNKNOWN, "");
	}
	
	@Autowired
	private ErrorAttributes errorAttributes;

	@Bean
	public CustomErrorController customErrorController(){return new CustomErrorController(errorAttributes);}
	
	@Bean
	public Logger logger() { return new Logger();}
	
}
