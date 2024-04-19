package edu.cmu.andrew.application.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
* <h1>CRMService</h1>
* 
* Business service consumes a kafka topic and send a confirmation email to the given user
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-04-12
*/

@Service
public class CRMService {
	
	private final Logger logger = LoggerFactory.getLogger(CRMService.class);
	
	@Autowired
	private EmailService emailService;
	
	private static final String TOPIC_ID = "juancarv.customer.evt";

	/**
	 * Listener of the topic TOPIC_ID 
	 * @param customerStr the message received by the Kafka topic
	 */
	@KafkaListener(id = "fooGroup", topics = TOPIC_ID)
	public void listen(String customerStr) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode;
		String customerName = null;
		String customerAddress = null;
		try {
			jsonNode = objectMapper.readTree(customerStr);
			ObjectNode object = (ObjectNode) jsonNode;
			customerName = object.get("name").asText();
			customerAddress = object.get("userId").asText();
		} catch (JsonProcessingException e) {			
			e.printStackTrace();
		}
		
		
		System.out.println("Received: " + customerStr);
		logger.info("Received: " + customerStr);
		System.out.println("Welcome "+ customerName);
		
		emailService.sendSimpleMessage(customerAddress, "Welcome to book store", 
				"Dear "+customerName+",\n"
				+ "Welcome to the Book store created by juancarv.\n"
				+ "Exceptionally this time we won’t ask you to click a link to activate your account. \n"
				+ "");
		
	}
	
}
