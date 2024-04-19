package edu.cmu.andrew.application.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cmu.andrew.application.business.model.Book;

/**
* <h1>BookRepository</h1>
* 
* JPA repository to interact with the table BOOK using the ISBN String as PK, only use inhered methods for this implementation
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/
public interface BookRepository extends JpaRepository<Book, String>{

}
