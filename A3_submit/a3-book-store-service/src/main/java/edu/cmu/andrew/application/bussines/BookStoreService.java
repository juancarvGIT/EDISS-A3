package edu.cmu.andrew.application.bussines;

import org.springframework.http.ResponseEntity;


import edu.cmu.andrew.application.business.dto.BookDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
* <h1>BookStore Service</h1>
* 
* Interface for any implementation of BookStoreService aims to standardize the method exposed as end points to the end-user
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/
@Tag(name = "Books", description = "API to interact with the BOOK service")
public interface BookStoreService  {
	
	/**
	 * Method for add a new book instance to the system
	 * @param bookDTO a data transfer object with the book data to be stored in the system
	 * @return a Response entity of Object type allowing flexibility for returning either a book instance or a DTO with information to the user
	 */
	@Operation(
            summary = "Add a book to the system",
            description = "The book is added to the Book datasource (the ISBN is the primary key)")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "201", description = "the book has been added"),
    		@ApiResponse(responseCode = "400", description = "The request is malformed"),
    		@ApiResponse(responseCode = "422", description = "the ISBN is already in the system"),
    		@ApiResponse(responseCode = "404", description = "the service is unavailable"),
    		
    })
	public ResponseEntity<Object> addBook(BookDTO bookDTO);
	
	/**
	 * Method to update a existing book in the system
	 * @param newBook the book instance to be updated
	 * @param isbn the unique key identifier of the book to be updated
	 * @return a Response entity of Object type allowing flexibility for returning either a book instance or a DTO with information to the user
	 */
	@Operation(
            summary = "Update a book in the system",
            description = "The book is added to the Book datasource (the ISBN is the primary key)")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description = "The book has been updated successfully"),
    		@ApiResponse(responseCode = "404", description = "The book with the given ISBN is not in the system")
    		
    })
	public ResponseEntity<Object> updateBook(BookDTO  newBook,  String isbn);
	
	/**
	 * Method to get a book from the system
	 * @param isbn the unique key identifier of the book to be query
	 * @return a Response entity of BookDTO, a data transfer object of the book found
	 */
	@Operation(
            summary = "Query the book information in the system",
            description = "The book is added to the Book datasource (the ISBN is the primary key)")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description = "The bookin formation is returned"),
    		@ApiResponse(responseCode = "404", description = "The book with the given ISBN is not in the system")
    		
    })
	public ResponseEntity<BookDTO> getBook(String isbn);

}
