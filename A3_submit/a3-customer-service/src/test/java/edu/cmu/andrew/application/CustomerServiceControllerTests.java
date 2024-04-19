package edu.cmu.andrew.application;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.andrew.application.business.dto.CustomerDTO;
import edu.cmu.andrew.application.business.model.Customer;
import edu.cmu.andrew.application.persistence.CustomerRepository;

/**
* <h1>Customer Controller REST service</h1>
*  Test Class to validate isolated cases for Customer Service controller class
*
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CustomerServiceControllerTests {

	@MockBean
	private CustomerRepository mockedCustomerRepository;

	@LocalServerPort
	private int PORT;

	@Autowired
	private TestRestTemplate restTemplate;
	private static String BASE_URL = "http://localhost:";
	private static String ADD_CUSTOMER_URL = "/customers";
	private static String RETRIEVE_CUSTOMER_URL = "/customers/";
	private static String RETRIEVE_CUSTOMER_BY_USERID_URL = "/customers?userId=";
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Integer exampleID = 10;
	private final Integer exampleNonExistingIdCustomer = 15;
	private final String exampleUserId = "jc@gmail.com";
	private final String exampleNonExistingUserId = "jc1986@gmail.com";
	private final Customer customerEntity = new Customer(exampleID, "jc@gmail.com", "JuanCarlosV", "+5546984526",
			"Mexico", "Jalisco", "Zapopan", "MX", "58102");
	private final CustomerDTO customerDTO = new CustomerDTO("jc@gmail.com", "JuanCarlosV", "+5546984526", "Mexico",
			"Jalisco", "Zapopan", "MX", "58102");
	private final Optional<Customer> customerResult = Optional.of(customerEntity);
	private final Optional<Customer> customerEmptyResult = Optional.empty();

	/**
	 * Test the positive case when a customer is query it by it's ID the response code is 200
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void getCustomerShouldReturn200ForValidID() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedCustomerRepository.findById(exampleID)).thenReturn(customerResult);
		ResponseEntity<String> result = this.restTemplate
				.getForEntity(BASE_URL + PORT + RETRIEVE_CUSTOMER_URL + exampleID, String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	/**
	 * Test the negative case when a customer is query by it's ID, it is not found and the response code is 404
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void getCustomerShouldReturn404ForNoNExistingID() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedCustomerRepository.findById(exampleID)).thenReturn(customerEmptyResult);
		ResponseEntity<String> result = this.restTemplate
				.getForEntity(BASE_URL + PORT + RETRIEVE_CUSTOMER_URL + exampleNonExistingIdCustomer, String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Test the positive case when a customer is query it by it's ID and the system returns a valid Customer JSON object
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void getCustomerByIDShouldReturnValidCustomer() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedCustomerRepository.findById(exampleID)).thenReturn(customerResult);

		String result = this.restTemplate.getForObject(BASE_URL + PORT + RETRIEVE_CUSTOMER_URL + exampleID,
				String.class);
		JsonNode root = objectMapper.readTree(result);

		assertThat(result).isNotNull();
		assertThat(root.path("id").asText()).isNotNull();
		assertThat(root.path("userId").asText()).isNotNull();
		assertThat(root.path("name").asText()).isNotNull();
		assertThat(root.path("phone").asText()).isNotNull();
		assertThat(root.path("address").asText()).isNotNull();
		// assertThat(root.path("address2").asDouble()).isNotNull(); This is optional field
		assertThat(root.path("city").asInt()).isNotNull();
		assertThat(root.path("state").asInt()).isNotNull();
		assertThat(root.path("zipcode").asInt()).isNotNull();
	}

	/**
	 * Test the positive case when a customer is query it by it's userId the response code is 200
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void getCustomerShouldReturn200ForValidUserId() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedCustomerRepository.findByUserId(exampleUserId)).thenReturn(customerEntity);
		ResponseEntity<String> result = this.restTemplate
				.getForEntity(BASE_URL + PORT + RETRIEVE_CUSTOMER_BY_USERID_URL + exampleUserId, String.class);
		System.out.println(BASE_URL + PORT + RETRIEVE_CUSTOMER_BY_USERID_URL + exampleUserId);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	/**
	 * Test the negative case when a customer is query by it's userId, it is not found and the response code is 404
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void getCustomerShouldReturn404ForNoNExistingUserId() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedCustomerRepository.findByUserId(exampleNonExistingUserId)).thenReturn(null);
		ResponseEntity<String> result = this.restTemplate.getForEntity(
				BASE_URL + PORT + RETRIEVE_CUSTOMER_BY_USERID_URL + exampleNonExistingUserId, String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Test the positive case when a mocked customer is added to the system and the response code is a 201
	 */
	@Test
	void addCustomerShouldReturn201ForSuccessfulAddition() {

		Mockito.when(mockedCustomerRepository.findByUserId(exampleNonExistingUserId)).thenReturn(null);
		Mockito.when(mockedCustomerRepository.save(Mockito.any(Customer.class))).thenReturn(customerEntity);
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_CUSTOMER_URL, customerDTO,
				String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	/**
	 * Test the positive case when a mocked customer is added to the system and the http reponse header "Location" contains the path to the 
	 * customer created (resource created)
	 */
	@Test
	void addCustomerShouldHaveHeaderLocationOfNewCustomer() {

		Mockito.when(mockedCustomerRepository.findByUserId(exampleNonExistingUserId)).thenReturn(null);
		Mockito.when(mockedCustomerRepository.save(Mockito.any(Customer.class))).thenReturn(customerEntity);
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_CUSTOMER_URL, customerDTO,
				String.class);
		assertThat(result.getHeaders().get("Location").get(0))
				.isEqualTo(BASE_URL + PORT + RETRIEVE_CUSTOMER_URL + exampleID);
	}

	/**
	 * Test the positive case when a customer is added and the system returns a valid Customer JSON object
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void addCustomerShouldReturnTheAddedCustomer() throws JsonMappingException, JsonProcessingException {
		
		Mockito.when(mockedCustomerRepository.findByUserId(exampleNonExistingUserId)).thenReturn(null);
		Mockito.when(mockedCustomerRepository.save(Mockito.any(Customer.class))).thenReturn(customerEntity);
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_CUSTOMER_URL, customerDTO,
				String.class);

		JsonNode root = objectMapper.readTree(result.getBody());
		assertThat(result).isNotNull();
		assertThat(root.path("id").asText()).isNotNull();
		assertThat(root.path("userId").asText()).isNotNull();
		assertThat(root.path("name").asText()).isNotNull();
		assertThat(root.path("phone").asText()).isNotNull();
		assertThat(root.path("address").asText()).isNotNull();
		// assertThat(root.path("address2").asDouble()).isNotNull(); This is optional field
		assertThat(root.path("city").asInt()).isNotNull();
		assertThat(root.path("state").asInt()).isNotNull();
		assertThat(root.path("zipcode").asInt()).isNotNull();
	}

	/**
	 * Test the negative case when a customer tries to register to the system but it exists already returning a http code 422
	 */
	@Test
	void addCustomerShouldReturn422ForExistingUserId() {
		Mockito.when(mockedCustomerRepository.findByUserId(exampleUserId)).thenReturn(customerEntity);
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_CUSTOMER_URL, customerDTO,
				String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	/**
	 * Test the negative case when a customer tries to register to the system but it exists already returning the error message:
	 *  "This user ID already exists in the system."
	 *  
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void addCustomerShouldReturnErrorMsgForExistingUserId() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedCustomerRepository.findByUserId(exampleUserId)).thenReturn(customerEntity);
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_CUSTOMER_URL, customerDTO,
				String.class);
		JsonNode root = objectMapper.readTree(result.getBody());
		assertThat(root.path("message").asText()).isEqualTo("This user ID already exists in the system.");
	}

	/**
	 * Test the negative case when a customer tries to register to the system but some of the mandatory data is missing
	 * Test the positive case when a customer tries to register to the system and only the optional data (address2) is missing
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void addBookShouldReturn400ForAnyMissingInputExceptAddress2() throws JsonMappingException, JsonProcessingException {

		// Case 1 negative test
		Mockito.when(mockedCustomerRepository.findByUserId(exampleUserId)).thenReturn(customerEntity);
		customerDTO.setName(null);// missing data
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_CUSTOMER_URL, customerDTO,
				String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

		// Case 2 positive test
		Mockito.when(mockedCustomerRepository.findByUserId(exampleUserId)).thenReturn(null);
		Mockito.when(mockedCustomerRepository.save(Mockito.any(Customer.class))).thenReturn(customerEntity);
		customerDTO.setName("JuanCarlosV");// restoring data field
		customerDTO.setAddress2(null);// optional data
		ResponseEntity<String> result2 = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_CUSTOMER_URL,
				customerDTO, String.class);
		assertThat(result2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

	}

	/**
	 * Test the negative case when a customer tries to register to the system but its data has an invalid email
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void addBookShouldReturn400ForInvalidEmail() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedCustomerRepository.findByUserId(exampleUserId)).thenReturn(customerEntity);
		customerDTO.setUserId("jcgmail.com");// invalid email set
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_CUSTOMER_URL, customerDTO,
				String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

	}
	
	/**
	 * Test the negative case when a customer tries to register to the system but its data has an invalid US state code
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void addBookShouldReturn400ForInvalidStateCode() throws JsonMappingException, JsonProcessingException {

		// Case 1 negative test
		Mockito.when(mockedCustomerRepository.findByUserId(exampleUserId)).thenReturn(customerEntity);
		customerDTO.setState("UUS");// invalid US state code
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_CUSTOMER_URL, customerDTO,
				String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

		// Case 2 positive test
		Mockito.when(mockedCustomerRepository.findByUserId(exampleUserId)).thenReturn(null);
		Mockito.when(mockedCustomerRepository.save(Mockito.any(Customer.class))).thenReturn(customerEntity);
		customerDTO.setState("US");// invalid US state code
		ResponseEntity<String> result2 = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_CUSTOMER_URL,
				customerDTO, String.class);
		assertThat(result2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

	}

}
