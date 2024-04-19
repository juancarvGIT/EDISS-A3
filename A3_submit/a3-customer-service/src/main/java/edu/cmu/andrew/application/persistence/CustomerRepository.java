package edu.cmu.andrew.application.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cmu.andrew.application.business.model.Customer;

/**
* <h1>BookRepository</h1>
* 
* JPA repository to interact with the table CUSTOMER using the ID Integer as PK
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/
public interface CustomerRepository extends JpaRepository<Customer, Integer>{

	/**
	 * Method to get query a customer from the DB from its userId
	 * @param userId the userId to search in the DB
	 * @return a Customer instance if the given userId is found, null otherwise
	 */
	public Customer findByUserId(String userId);
}
