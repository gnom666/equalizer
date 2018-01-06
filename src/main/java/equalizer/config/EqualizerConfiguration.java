package equalizer.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

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

	@Bean
	public JavaMailSender getJavaMailSender() {
	    JavaMailSender mailSender = new JavaMailSenderImpl();
	    ((JavaMailSenderImpl) mailSender).setHost("smtp.gmail.com");
	    ((JavaMailSenderImpl) mailSender).setPort(587);
	     
	    ((JavaMailSenderImpl) mailSender).setUsername("equalizerregister@gmail.com");
	    ((JavaMailSenderImpl) mailSender).setPassword("Qazwsxedc.123");
	     
	    Properties props = ((JavaMailSenderImpl) mailSender).getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
	     
	    return mailSender;
	}
}
