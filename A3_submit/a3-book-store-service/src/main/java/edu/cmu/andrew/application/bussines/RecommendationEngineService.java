package edu.cmu.andrew.application.bussines;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import edu.cmu.andrew.application.business.dto.RecommendedBookDTO;
import edu.cmu.andrew.application.rest.client.FeignRecommendationEngine;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

/**
* <h1>Recommendation Engine Service</h1>
* 
*  Service layer that controls the execution and invocation of the service recommendation engine, it also manages the circuiteBreaker status
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-04-9
*/
@Service
public class RecommendationEngineService {

	@Autowired
	FeignRecommendationEngine feignRecommendationEngine;
	
	/**
	 * Method that invokes the recommenation engine, note that is protected by a fallback method called "fallback" in case the engine service is unavailable
	 * @param isbn the book to get recommendations from
	 * @return a List of recommended books
	 */
	@CircuitBreaker(name="CircuitBreakerService", fallbackMethod = "fallback")
	public ResponseEntity<List<RecommendedBookDTO>> getRecommendation(String isbn) {
		
		ResponseEntity<List<RecommendedBookDTO>> responseE = 
				feignRecommendationEngine.getRecommendedTittles(isbn);
		if(responseE.getStatusCode()==HttpStatus.NOT_FOUND) {
			return new ResponseEntity<List<RecommendedBookDTO>>(HttpStatus.OK);
		}else {
			return responseE;
		}
	}
	
	/**
	 * This is a fallback method that will execute only if the recommendation engine service is unavailable or it produced a timeout error previously causing that the circuit opens
	 * @param e the exception produced by the unsuccessful call to the third party service in this case the recommendation engine
	 * @return a response entity with the error code 503, service unavailable
	 */
	public ResponseEntity<List<RecommendedBookDTO>> fallback(CallNotPermittedException e) {		
		System.out.println("Fall back:"+e.getMessage());
        return new ResponseEntity<List<RecommendedBookDTO>>(HttpStatus.SERVICE_UNAVAILABLE);
    }
	/**
	 * This is a fallback method that will execute in the case of a general error with the circuit closed, it will handle the timeout in the close state
	 * @param e the exception produced by the unsuccessful call to the third party service in this case the recommendation engine
	 * @return a response entity with the error code 504, service unavailable
	 */
	public ResponseEntity<List<RecommendedBookDTO>> fallback(Exception e) {		
		System.out.println("Catch error:"+e.getMessage());
        return new ResponseEntity<List<RecommendedBookDTO>>(HttpStatus.GATEWAY_TIMEOUT);
    }
	
}
