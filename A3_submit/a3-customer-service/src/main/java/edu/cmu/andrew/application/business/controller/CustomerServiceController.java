package edu.cmu.andrew.application.business.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.cmu.andrew.application.business.dto.CustomerDTO;
import edu.cmu.andrew.application.business.dto.NotificationMsgDTO;
import edu.cmu.andrew.application.business.model.Customer;
import edu.cmu.andrew.application.bussines.CustomerRegistrationService;
import edu.cmu.andrew.application.bussines.CustomerService;
import edu.cmu.andrew.application.persistence.CustomerRepository;
import jakarta.validation.Valid;

/**
* <h1>Customer REST service</h1>
* 
*  Web controller that receives and controls the request and responses from the
*  rest client for the Customer service operations
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/
@RestController
public class CustomerServiceController implements CustomerService {

	@Autowired
	private CustomerRepository customerRepo;
	
	@Autowired
	private CustomerRegistrationService customerRegistrationService;

	/**
	 * Adds a Customer to the system. This end point will receive a JSON
	 * representation of a customer and proceed to register, in the case of the
	 * customer ID already exists the method will return an HTTP error code 422
	 * UNPROCESSABLE_ENTITY for the case of he customer ID does not exists already,
	 * the system will create a new customer entity instance and save it if this is
	 * the case it will return a HTTP status code 201 CREATED
	 * 
	 * @param customerDTO an instance of CustomerDTO class this object will contain
	 *                    the customer data to be registered in the system
	 * @return ResponseEntity<Object> will contain the created customer along with
	 *         the Location header indicating the resource location otherwise will
	 *         contain an error message "This user ID already exists in the system."
	 *         and a HTTP status error code 422
	 * 
	 */
	@PostMapping("/customers")
	public ResponseEntity<Object> addCustomer(@Valid @RequestBody(required = false) CustomerDTO customerDTO) {

		final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

		Customer existingCustomer = customerRepo.findByUserId(customerDTO.getUserId());
		if (existingCustomer != null) {

			NotificationMsgDTO message = new NotificationMsgDTO();
			message.setMessage("This user ID already exists in the system.");
			return new ResponseEntity<Object>(message, HttpStatus.UNPROCESSABLE_ENTITY);
		} else {

			Customer newCustomer = new Customer();
			newCustomer.setUserId(customerDTO.getUserId());
			newCustomer.setName(customerDTO.getName());
			newCustomer.setPhone(customerDTO.getPhone());
			newCustomer.setAddress(customerDTO.getAddress());
			newCustomer.setAddress2(customerDTO.getAddress2());
			newCustomer.setCity(customerDTO.getCity());
			newCustomer.setState(customerDTO.getState());
			newCustomer.setCity(customerDTO.getCity());
			newCustomer.setZipcode(customerDTO.getZipcode());

			Customer customerSaved = customerRepo.save(newCustomer);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Location", baseUrl + "/customers/" + customerSaved.getId());
			ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(customerSaved, responseHeaders,
					HttpStatus.CREATED);
			customerRegistrationService.sendRegistrationEvent(customerDTO);
			return responseEntity;
		}
	}

	/**
	 * This method will query the customer by its userId (email) and return a
	 * customer instance in the case is found otherwise will return a 404 NOT_FOUND
	 * HTTP status code error.
	 * 
	 * @param String userId representing the userId field to search in the system
	 * @return Response<Customer> a response entity with the customer object and a
	 *         200 HTTP status code, a 400 HTTP status code will be returned id the
	 *         email is invalid, and 404 if the customer is not found
	 * 
	 */
	@GetMapping("/customers")
	public ResponseEntity<Customer> getCustomerbyUserId(@RequestParam String userId) {

		if (!emailPatternMatches(userId)) {
			return new ResponseEntity<Customer>(HttpStatus.BAD_REQUEST);
		}

		Customer existingCustomer = customerRepo.findByUserId(userId);
		if (existingCustomer != null) {
			return new ResponseEntity<Customer>(existingCustomer, HttpStatus.OK);
		} else {
			return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * This method will query the customer by its ID (PK in the DB) and return a
	 * customer instance in the case is found otherwise will return a 404 NOT_FOUND
	 * HTTP status code error.
	 * 
	 * @param String id representing the ID field to search in the system
	 * @return Response<Customer> a response entity with the customer object and a
	 *         200 HTTP status code, a 404 HTTP status code will be returned
	 *         otherwise
	 */
	@GetMapping("/customers/{id}")
	public ResponseEntity<Customer> getCustomerbyId(@PathVariable Integer id) {
		Optional<Customer> customer = null;
		customer = customerRepo.findById(id);
		if (customer.isPresent()) {
			return new ResponseEntity<Customer>(customer.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Method to handle bad requests and validation errors. This method will
	 * intercept errors coming from the client data and will produce a comprehensive
	 * error message to the end user
	 * 
	 * @param ex a exception if type MethodArgumentNotValidException produced by the
	 *           context when an error is produced
	 * @return a map Map<String, String> with the error messages for the end user in
	 *         the case of missing data, or wrong format
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return errors;
	}

	/**
	 * Method to validate email address
	 * @param emailAddress the String email to validate
	 * @return true if the given email is valid, false otherwise
	 */
	public static boolean emailPatternMatches(String emailAddress) {
		String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
				+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

		return Pattern.compile(regexPattern).matcher(emailAddress).matches();
	}

}
