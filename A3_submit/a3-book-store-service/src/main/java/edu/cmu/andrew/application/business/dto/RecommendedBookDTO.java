package edu.cmu.andrew.application.business.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
* <h1>BookDTO</h1>
* 
* Data Transfer Object to transfer Book data from end-user to the system.
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-04-09
*/

@JsonPropertyOrder({ "title", "isbn", "authors","publisher" })
public class RecommendedBookDTO {


	private String title;

	private String isbn;

	private String authors;

	private String publisher;
	
	

	public RecommendedBookDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RecommendedBookDTO(String title, String isbn, String authors, String publisher) {
		super();
		this.title = title;
		this.isbn = isbn;
		this.authors = authors;
		this.publisher = publisher;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	
	

	
}
