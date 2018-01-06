package equalizer.controler;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;

import equalizer.config.EqualizerConfiguration;

public class Mail implements Runnable {
	
	@Autowired
	private EqualizerConfiguration eConf;
	
	public String email;
	public String text;

	@Override
	public void run() {
		sendMail();	
		//sendSimpleMail();
	}

	
	private void sendMail() {
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("equalizerregister", "Qazwsxedc.123");
					}
				  });

				try {

					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress("equalizerregister@gmail.com"));
					message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(email));
					message.setSubject("Activate your account");
					message.setText(text);

					Transport.send(message);

					System.out.println("Done");

				} 	catch (MessagingException e) {
					throw new RuntimeException(e);
				}
		
	}
	
	private void sendSimpleMail () {
		EmailService es = new EmailService();
		es.emailSender = eConf.getJavaMailSender();
		es.sendSimpleMessage(email, "Enable registration", text);
		
	}
	
	
}
