package edu.cmu.andrew.application.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
* <h1>EmailService</h1>
* 
* Business service that sends with a single method to send a simple Email
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-04-12
*/
@Service
public class EmailService {

	@Autowired
    private JavaMailSender emailSender;
	
	private static final String NOREPLY_ADDRESS = "noreply@cmu.com";
	
	/**
	 * Method to send a simple Email
	 * @param to address to whom send the email
	 * @param subject Subject of the Email
	 * @param text body of the Email
	 */
	 public void sendSimpleMessage(String to, String subject, String text) {
	        try {
	            SimpleMailMessage message = new SimpleMailMessage();
	            message.setFrom(NOREPLY_ADDRESS);
	            message.setTo(to);
	            message.setSubject(subject);
	            message.setText(text);

	            emailSender.send(message);
	        } catch (MailException exception) {
	            exception.printStackTrace();
	        }
	    }
}
