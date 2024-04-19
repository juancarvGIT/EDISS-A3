package edu.cmu.andrew.application.business.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.cmu.andrew.application.business.dto.BookDTO;
import edu.cmu.andrew.application.business.dto.NotificationMsgDTO;
import edu.cmu.andrew.application.business.dto.RecommendedBookDTO;
import edu.cmu.andrew.application.business.model.Book;
import edu.cmu.andrew.application.bussines.BookStoreService;
import edu.cmu.andrew.application.bussines.RecommendationEngineService;
import edu.cmu.andrew.application.persistence.BookRepository;
import jakarta.validation.Valid;

/**
* <h1>Bookstore REST service</h1>
* 
*  Web controller that receives and controls the request and responses from the
*  rest client for the Book service operations
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/


@RestController
public class BookStoreServiceController implements BookStoreService {

	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private  RecommendationEngineService recommendationEngineService;

	/**
	 * Add a book to the system. The ISBN will be the unique identifier for the
	 * book. The book is added to the Book data table on MySql (the ISBN is the
	 * primary key). This end point will validate if the given ISBN exists in the
	 * system before trying the addition in the case of the ISBN is already in the
	 * system the HTTP status code returned will be a 422 UNPROCESSABLE_ENTITY in
	 * the case of the ISBN is not in the system the HTTP status code returned will
	 * be a 201 CREATED Any malformed request will be handled by the Context and
	 * will return a 400 status code
	 * 
	 * @param newBook a BookDTO object with the data of the book to be registered,
	 *                the newBook implements field validation to avoid missing
	 *                fields, as well as a validation for price format allowing up
	 *                to two decimals
	 * @return a ResponseEntity<Object> that will contain the book registered and
	 *         the Location header with the path of the newly resource created in
	 *         case of success or and error message otherwise
	 * 
	 * 
	 */

	@PostMapping("/books")
	public ResponseEntity<Object> addBook(@Valid @RequestBody(required = false) BookDTO newBook) {

		final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

		Optional<Book> existingBook = null;
		existingBook = bookRepository.findById(newBook.getIsbn());
		if (existingBook.isPresent()) {// If the given ISBN exists in the system

			NotificationMsgDTO msg = new NotificationMsgDTO();
			msg.setMessage("This ISBN already exists in the system.");

			return new ResponseEntity<Object>(msg, HttpStatus.UNPROCESSABLE_ENTITY);
		} else {// In the case of the given ISBN do not exists in the system

			bookRepository.save(new Book(newBook.getIsbn(), newBook.getTitle(), newBook.getAuthor(),
					newBook.getDescription(), newBook.getGenre(), newBook.getPrice(), newBook.getQuantity()));
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Location", baseUrl + "/books/" + newBook.getIsbn());
			ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(newBook, responseHeaders,
					HttpStatus.CREATED);
			return responseEntity;

		}
	}

	/**
	 * Updates a book’s information in the system. The ISBN will be the unique
	 * identifier for the book. This end point will validate that the given book
	 * ISBN exists in the system, if the book does exists will proceed with the
	 * update in the case of non existence the response will contain an HTTP status
	 * code 404 NOT FOUND
	 * 
	 * @param newBook a BookDTO object with the data of the book to be registered,
	 *                the newBook implements field validation to avoid missing
	 *                fields, as well as a validation for price format allowing up
	 *                to two decimals
	 * @return a ResponseEntity<Object> that will contain the book updated in case
	 *         of success or an empty object and a error HTTP status code otherwise
	 */
	@PutMapping("/books/{isbn}")
	public ResponseEntity<Object> updateBook(@Valid @RequestBody(required = false) BookDTO newBook,
			@PathVariable String isbn) {

		Optional<Book> existingBook = null;
		existingBook = bookRepository.findById(isbn);
		if (existingBook.isPresent()) { // if the book exists then can be updated
			bookRepository.save(new Book(isbn, newBook.getTitle(), newBook.getAuthor(), newBook.getDescription(),
					newBook.getGenre(), newBook.getPrice(), newBook.getQuantity()));
			return new ResponseEntity<Object>(newBook, HttpStatus.OK);
		} else {// if the book does not exists then return a HTTP status error code
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * This end-point query a book with the given ISBN, it validates if the book
	 * with the given ISBN exists in the system if so, then it return a JSON object
	 * of the book, otherwise will return a HTTP status error code 404 NOT_FOUND
	 * 
	 * @param isbn a String that contains the ISBN number of the book to query
	 * @return A ResponseEntity<BookDTO> that contains the book found and the HTTP
	 *         status code 200
	 */
	@RequestMapping(value = { "/books/isbn/{isbn}", "/books/{isbn}" }, method = RequestMethod.GET)
	public ResponseEntity<BookDTO> getBook(@PathVariable String isbn) {

		Optional<Book> book = null;
		book = bookRepository.findById(isbn);
		if (book.isPresent()) {

			Book entity = book.get();
			BookDTO bookDto = new BookDTO(entity.getIsbn(), entity.getTitle(), entity.getAuthor(),
					entity.getDescription(), entity.getGenre(), entity.getPrice(), entity.getQuantity());
			return new ResponseEntity<BookDTO>(bookDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<BookDTO>(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * This end-point returns a list of book titles that are related to a given book specified via its ISBN.
	 * 
	 * @param isbn a String that contains the ISBN number of the book which want to get books related to
	 * @return A ResponseEntity<Object> that contains the list of related books found and the HTTP
	 *         status code 200. If the service is not available it will return 503 http code
	 */
	@GetMapping("/books/{isbn}/related-books")
	public ResponseEntity<List<RecommendedBookDTO>> getRelatedBooks(@PathVariable String isbn) {

		ResponseEntity<List<RecommendedBookDTO>> listBooksRecommendedResponseEntity = 
				recommendationEngineService.getRecommendation(isbn);	
		return listBooksRecommendedResponseEntity;
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

}
