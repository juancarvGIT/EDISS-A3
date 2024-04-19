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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cmu.andrew.application.business.dto.BookDTO;
import edu.cmu.andrew.application.business.model.Book;
import edu.cmu.andrew.application.persistence.BookRepository;

/**
 * Test Class to validate isolated cases for BookStoreService controller class
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BookStoreServiceControllerTests {

	@MockBean
	private BookRepository mockedBookRepository;

	@LocalServerPort
	private int PORT;

	@Autowired
	private TestRestTemplate restTemplate;
	private static String BASE_URL = "http://localhost:";
	private static String ADD_BOOK_URL = "/books";
	private static String UPDATE_BOOK_URL = "/books/";
	private static String RETRIEVE_BOOK_URL = "/books/";
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String exampleISBN = "ISBN456777";
	private final String exampleNonExistingISBN = "AAAAAAA1111";
	private final Book bookEntity = new Book(exampleISBN, "SW Architecture", "Bass", "A book of SW", "technical",
			100.55d, 10);
	private final BookDTO bookDTO = new BookDTO(exampleISBN, "SW Architecture", "Bass", "A book of SW", "technical",
			100.55d, 10);
	private final Optional<Book> bookResult = Optional.of(bookEntity);
	private final Optional<Book> bookEmptyResult = Optional.empty();

	/**
	 * Test the positive case when a book is query it by it's ISBN and the response code is 200
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void getBookShouldReturn200ForValidISBN() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedBookRepository.findById(exampleISBN)).thenReturn(bookResult);
		ResponseEntity<String> result = this.restTemplate
				.getForEntity(BASE_URL + PORT + RETRIEVE_BOOK_URL + exampleISBN, String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	/**
	 * Test the negative case when a book is query it by it's ISBN and the response code is 404 not found
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void getBookShouldReturn404ForNoNExistingISBN() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedBookRepository.findById(exampleNonExistingISBN)).thenReturn(bookEmptyResult);
		ResponseEntity<String> result = this.restTemplate
				.getForEntity(BASE_URL + PORT + RETRIEVE_BOOK_URL + exampleNonExistingISBN, String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Test the positive case when a book is query it by it's ISBN, the book exists and the system returns a valid JSON object book
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void getBookShouldReturnValidBook() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedBookRepository.findById(exampleISBN)).thenReturn(bookResult);
		String result = this.restTemplate.getForObject(BASE_URL + PORT + RETRIEVE_BOOK_URL + exampleISBN, String.class);
		JsonNode root = objectMapper.readTree(result);

		assertThat(result).isNotNull();
		assertThat(root.path("ISBN").asText()).isNotNull();
		assertThat(root.path("title").asText()).isNotNull();
		assertThat(root.path("Author").asText()).isNotNull();
		assertThat(root.path("description").asText()).isNotNull();
		assertThat(root.path("genre").asText()).isNotNull();
		assertThat(root.path("price").asDouble()).isNotNull();
		assertThat(root.path("quantity").asInt()).isNotNull();
	}

	/**
	 * Test the positive case when a book is added successfully and the response code is 201 created
	 */
	@Test
	void addBookShouldReturn201ForSuccessfulAddition() {

		Mockito.when(mockedBookRepository.save(bookEntity)).thenReturn(bookEntity);
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_BOOK_URL, bookDTO,
				String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	/**
	 * Test the positive case when a mocked book is added to the system and the http reponse header "Location" contains the path to the 
	 * book created (resource created)
	 */
	@Test
	void addBookShouldHaveHeaderLocationOfNewBook() {

		Mockito.when(mockedBookRepository.save(bookEntity)).thenReturn(bookEntity);
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_BOOK_URL, bookDTO,
				String.class);
		assertThat(result.getHeaders().get("Location").get(0))
				.isEqualTo(BASE_URL + PORT + RETRIEVE_BOOK_URL + exampleISBN);
	}

	/**
	 * Test the positive case when a book is added to the system successfully and the entity created is returned in the service response 
	 * as a valid JSON object of book.
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void addBookShouldReturnTheAddedBook() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedBookRepository.save(bookEntity)).thenReturn(bookEntity);
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_BOOK_URL, bookDTO,
				String.class);
		JsonNode root = objectMapper.readTree(result.getBody());

		assertThat(result).isNotNull();
		assertThat(root.path("ISBN").asText()).isNotNull();
		assertThat(root.path("title").asText()).isNotNull();
		assertThat(root.path("Author").asText()).isNotNull();
		assertThat(root.path("description").asText()).isNotNull();
		assertThat(root.path("genre").asText()).isNotNull();
		assertThat(root.path("price").asDouble()).isNotNull();
		assertThat(root.path("quantity").asInt()).isNotNull();
	}

	/**
	 * Test the negative case when a book is tried to add but the ISBN exists in the system already and returns a http code 422
	 */
	@Test
	void addBookShouldReturn422ForExistingISBN() {

		Mockito.when(mockedBookRepository.findById(exampleISBN)).thenReturn(bookResult);
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_BOOK_URL, bookDTO,
				String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	/**
	 * Test the negative case when a book is tried to add but the ISBN exists in the system already and return the erro message:
	 * "This ISBN already exists in the system."
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void addBookShouldReturnErrorMsgForExistingISBN() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedBookRepository.findById(exampleISBN)).thenReturn(bookResult);
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_BOOK_URL, bookDTO,
				String.class);
		JsonNode root = objectMapper.readTree(result.getBody());
		assertThat(root.path("message").asText()).isEqualTo("This ISBN already exists in the system.");
	}

	/**
	 * Test the negative case when a book is tried to add but some of the mandatory data is missing returning a http code 400
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void addBookShouldReturn400ForAnyMissingInput() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedBookRepository.save(bookEntity)).thenReturn(bookEntity);
		bookDTO.setPrice(null);// missing data
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_BOOK_URL, bookDTO,
				String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	/**
	 * Test the negative case when a book is tried to add but the price format is wrong by having more than 2 decimals
	 * Test the positive case when a book is tried to add and the price format is correct by having up to 2 decimals
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void addBookShouldReturn400IfPriceIsNotValidNumberWithTwoDecimalsAtMost()
			throws JsonMappingException, JsonProcessingException {

		//Case 1 negative case three decimals
		Mockito.when(mockedBookRepository.save(bookEntity)).thenReturn(bookEntity);
		bookDTO.setPrice(1525.266);// wrong data format
		ResponseEntity<String> result = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_BOOK_URL, bookDTO,
				String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

		//Case 2 negative case five decimals
		bookDTO.setPrice(1525.20005);// wrong data format
		ResponseEntity<String> result2 = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_BOOK_URL, bookDTO,
				String.class);
		assertThat(result2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

		//Case 3 positive case two decimals
		bookDTO.setPrice(1525.22);// good data format should return a valid response
		ResponseEntity<String> result3 = this.restTemplate.postForEntity(BASE_URL + PORT + ADD_BOOK_URL, bookDTO,
				String.class);
		assertThat(result3.getStatusCode()).isEqualTo(HttpStatus.CREATED);

	}

	/**
	 * Test the positive case when a book is updated successfully and the response code is 200 OK
	 */
	@Test
	void updateBookShouldReturn200ForSuccessfulUpdate() {

		Mockito.when(mockedBookRepository.findById(exampleISBN)).thenReturn(bookResult);
		Mockito.when(mockedBookRepository.save(bookEntity)).thenReturn(bookEntity);
		HttpEntity<BookDTO> requestEntity = new HttpEntity<BookDTO>(bookDTO);
		ResponseEntity<String> result = this.restTemplate.exchange(BASE_URL + PORT + UPDATE_BOOK_URL + exampleISBN,
				HttpMethod.PUT, requestEntity, String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	/**
	 * Test the positive case when a book is updated successfully and the response body contains a valid JSON object instance of book
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void updateBookShouldReturnTheUpdatedBookForSuccessfulOperation()
			throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedBookRepository.findById(exampleISBN)).thenReturn(bookResult);
		Mockito.when(mockedBookRepository.save(bookEntity)).thenReturn(bookEntity);
		HttpEntity<BookDTO> requestEntity = new HttpEntity<BookDTO>(bookDTO);
		ResponseEntity<String> result = this.restTemplate.exchange(BASE_URL + PORT + UPDATE_BOOK_URL + exampleISBN,
				HttpMethod.PUT, requestEntity, String.class);

		JsonNode root = objectMapper.readTree(result.getBody());
		assertThat(result).isNotNull();
		assertThat(root.path("ISBN").asText()).isNotNull();
		assertThat(root.path("title").asText()).isNotNull();
		assertThat(root.path("Author").asText()).isNotNull();
		assertThat(root.path("description").asText()).isNotNull();
		assertThat(root.path("genre").asText()).isNotNull();
		assertThat(root.path("price").asDouble()).isNotNull();
		assertThat(root.path("quantity").asInt()).isNotNull();
	}

	/**
	 * Test the negative case when a book that tries to update send a ISBN that not exists in the system returning a 404 not found code
	 */
	@Test
	void updateBookShouldReturn404ForISBNnotFound() {

		Mockito.when(mockedBookRepository.findById(exampleNonExistingISBN)).thenReturn(bookEmptyResult);
		HttpEntity<BookDTO> requestEntity = new HttpEntity<BookDTO>(bookDTO);
		ResponseEntity<String> result = this.restTemplate.exchange(BASE_URL + PORT + UPDATE_BOOK_URL + exampleISBN,
				HttpMethod.PUT, requestEntity, String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	/**
	 * Test the negative case when a book that tries to update, do not send the mandatory data and the service responds a 400 error code
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void updateBookShouldReturn400ForAnyMissingInput() throws JsonMappingException, JsonProcessingException {

		Mockito.when(mockedBookRepository.save(bookEntity)).thenReturn(bookEntity);
		bookDTO.setPrice(null);// missing data
		HttpEntity<BookDTO> requestEntity = new HttpEntity<BookDTO>(bookDTO);
		ResponseEntity<String> result = this.restTemplate.exchange(BASE_URL + PORT + UPDATE_BOOK_URL + exampleISBN,
				HttpMethod.PUT, requestEntity, String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	/**
	 * Test the negative case when a book is tried to update but the price format is wrong by having more than 2 decimals
	 * Test the positive case when a book is tried to update and the price format is correct by having up to 2 decimals
	 * 
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Test
	void updateBookShouldReturn400IfPriceIsNotValidNumberWithTwoDecimalsAtMost()
			throws JsonMappingException, JsonProcessingException {

		//Case 1 negative case three decimals
		Mockito.when(mockedBookRepository.save(bookEntity)).thenReturn(bookEntity);
		Mockito.when(mockedBookRepository.findById(exampleISBN)).thenReturn(bookResult);
		bookDTO.setPrice(1525.266);// wrong data format
		HttpEntity<BookDTO> requestEntity = new HttpEntity<BookDTO>(bookDTO);
		ResponseEntity<String> result = this.restTemplate.exchange(BASE_URL + PORT + UPDATE_BOOK_URL + exampleISBN,
				HttpMethod.PUT, requestEntity, String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
		//Case 2 negative case five decimals
		bookDTO.setPrice(1525.20005);// wrong data format
		requestEntity = new HttpEntity<BookDTO>(bookDTO);
		ResponseEntity<String> result2 = this.restTemplate.exchange(BASE_URL + PORT + UPDATE_BOOK_URL + exampleISBN,
				HttpMethod.PUT, requestEntity, String.class);
		assertThat(result2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

		//Case 3 positive case two decimals
		bookDTO.setPrice(1525.23);// good data format should return a valid response
		requestEntity = new HttpEntity<BookDTO>(bookDTO);
		ResponseEntity<String> result3 = this.restTemplate.exchange(BASE_URL + PORT + UPDATE_BOOK_URL + exampleISBN,
				HttpMethod.PUT, requestEntity, String.class);
		assertThat(result3.getStatusCode()).isEqualTo(HttpStatus.OK);

	}

}
