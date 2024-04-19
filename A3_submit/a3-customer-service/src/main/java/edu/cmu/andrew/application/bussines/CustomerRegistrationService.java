package edu.cmu.andrew.application.bussines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import edu.cmu.andrew.application.business.dto.CustomerDTO;


/**
* <h1>Customer Registration Service</h1>
* 
* Business service that publish a new customer registration event to the kafka topic
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-04-12
*/

@Service
public class CustomerRegistrationService {
	
	@Value( "${kafka.topic.id}" )
	private String topicId;

	@Autowired
	private KafkaTemplate<Object, Object> kafkaTemplate;
	
	/**
	 * This method publish the message to the Kafka topic, it represents the customer data
	 * @param customer the customer DTO with the data of the registered customer
	 */
	public void sendRegistrationEvent(CustomerDTO customer) {
		this.kafkaTemplate.send(topicId, customer.toString());
	}
	
}
