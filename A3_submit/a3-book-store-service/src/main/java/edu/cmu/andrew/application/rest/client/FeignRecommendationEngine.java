package edu.cmu.andrew.application.rest.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import edu.cmu.andrew.application.business.dto.RecommendedBookDTO;

/**
* <h1>REST client for the Recommendation Engine</h1>
* 
*  Web controller that servers to monitor the liveness of the REST service
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-04-9
*/

@FeignClient(value = "recommendationEngineClient", url= "${recommendation.engine.base.path}")
public interface FeignRecommendationEngine {

	
	@GetMapping("/recommended-titles/isbn/{isbn}")
	ResponseEntity<List<RecommendedBookDTO>> getRecommendedTittles(@PathVariable String isbn);
}
