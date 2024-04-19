package edu.cmu.andrew.application.business.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/**
* <h1>BookDTO</h1>
* 
* Data Transfer Object to transfer Book data from end-user to the system.
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/

@JsonPropertyOrder({ "ISBN", "title", "Author","description","genre" })
public class BookDTO {


	@JsonProperty("ISBN")
	@NotBlank
	@NotNull
	private String isbn;
	@NotBlank
	@NotNull
	private String title;
	@NotBlank
	@NotNull
	@JsonProperty("Author")
	private String author;
	@NotBlank
	@NotNull
	private String description;
	@NotBlank
	@NotNull
	private String genre;

	@NotNull
	@DecimalMin(value = "0.0", inclusive=false)
	@Digits(integer=5, fraction=2)
	private Double price;

	@NotNull
	private Integer quantity;
	
	
	
	public BookDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BookDTO(@NotBlank @NotNull String isbn, @NotBlank @NotNull String title, @NotBlank @NotNull String author,
			@NotBlank @NotNull String description, @NotBlank @NotNull String genre,
			@NotNull @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = 5, fraction = 2) Double price,
			@NotNull Integer quantity) {
		super();
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.description = description;
		this.genre = genre;
		this.price = price;
		this.quantity = quantity;
	}
	
	
	
	public BookDTO(@NotBlank @NotNull String isbn, @NotBlank @NotNull String title, @NotBlank @NotNull String author) {
		super();
		this.isbn = isbn;
		this.title = title;
		this.author = author;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	
	
	
}

