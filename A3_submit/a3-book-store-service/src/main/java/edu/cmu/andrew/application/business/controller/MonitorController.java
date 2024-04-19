package edu.cmu.andrew.application.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
* <h1>Monitor Controller service</h1>
* 
*  Web controller that servers to monitor the liveness of the REST service
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-04-9
*/

@RestController
public class MonitorController {

	@GetMapping("/status")
	public String livenessProbe() {
		return "OK";
	}
}
