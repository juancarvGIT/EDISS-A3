package edu.cmu.andrew.application.bussines;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.cmu.andrew.application.business.dto.RecommendedBookDTO;
import edu.cmu.andrew.application.rest.client.FeignRecommendationEngine;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class RecommendationEngineService {

	@Autowired
	FeignRecommendationEngine feignRecommendationEngine;
	
	@CircuitBreaker(name="CircuitBreakerService", fallbackMethod = "fallback")
	public ResponseEntity<List<RecommendedBookDTO>> getRecommendation(String isbn) {
		return feignRecommendationEngine.getRecommendedTittles(isbn);

	}
	
	public ResponseEntity<List<RecommendedBookDTO>> fallback(Exception e) {
		System.out.println("Fall back:"+e.getMessage());
        return new ResponseEntity<List<RecommendedBookDTO>>(HttpStatus.GATEWAY_TIMEOUT);
    }
	
}
