package edu.cmu.andrew.application.business.model;


import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


/**
* <h1>Book Entity</h1>
* 
* Entity Mapping for the table BOOK in the DB
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name="BOOK")
public class Book {

	@Id
	@Column(name="ISBN", unique = true, nullable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	private String isbn;
	private String title;
	private String author;
	private String description;
	private String genre;
	private Double price;
	private Integer quantity;
	
	
	
	
	public Book() {
		super();
		// TODO Auto-generated constructor stub
	}

	



	public Book(String isbn, String title, String author, String description, String genre, Double price,
			Integer quantity) {
		super();
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.description = description;
		this.genre = genre;
		this.price = price;
		this.quantity = quantity;
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

