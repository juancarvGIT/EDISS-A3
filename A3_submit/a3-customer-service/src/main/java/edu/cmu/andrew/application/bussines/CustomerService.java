package edu.cmu.andrew.application.bussines;

import org.springframework.http.ResponseEntity;

import edu.cmu.andrew.application.business.dto.CustomerDTO;
import edu.cmu.andrew.application.business.model.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
* <h1>Customer Service</h1>
* 
* Interface for any implementation of CustomerService aims to standardize the method exposed as end points to the end-user
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/
@Tag(name = "Customers", description = "API to interact with the Customer service")
public interface CustomerService {

	/**
	 * Method for adding a new customer to the system
	 * @param customerDTO the customer data to be stored in the system
	 * @return a Response entity of Object type allowing flexibility for returning either a customer instance or a DTO with information to the user
	 */
	@Operation(
            summary = "Adding a new customer to the system",
            description = "A new customer is added to the system, all fields except address2 are mandatory)")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "201", description = "A new customer is added to the system"),
    		@ApiResponse(responseCode = "400", description = "The request is malformed"),
    		@ApiResponse(responseCode = "422", description = "The customer ID already exists"),
    		@ApiResponse(responseCode = "404", description = "the service is unavailable")
    		
    })
	public ResponseEntity<Object> addCustomer(CustomerDTO customerDTO);
	/**
	 * Method to retrieve a customer instance from the system with the given id
	 * @param id the unique customer identifier to search in the system
	 * @return the customer entity found with the given id
	 */
	@Operation(
            summary = "Query a customer by its userId (email) ",
            description = "A new customer is added to the system, all fields except address2 are mandatory)")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description = "The information of the custmer with the given id is returned"),
    		@ApiResponse(responseCode = "400", description = "The id (email) is invalid"),
    		@ApiResponse(responseCode = "404", description = "The customer is not found")
    		
    })
	public ResponseEntity<Customer> getCustomerbyId(Integer id);
	
	/**
	 * Method to retrieve a customer instance from the system using the idCustomer(email) field
	 * @param idCustomer the unique customer field to search in the system
	 * @return the customer entity found with the given idCustomer
	 */
	@Operation(
            summary = "Query a customer by its ID (numeric value) ",
            description = "A new customer is added to the system, all fields except address2 are mandatory)")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description = "The information of the custmer with the given ID is returned"),
    		@ApiResponse(responseCode = "404", description = "The customer is not found")
    		
    })
	public ResponseEntity<Customer> getCustomerbyUserId(String idCustomer);



}
