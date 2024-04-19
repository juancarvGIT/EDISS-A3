package edu.cmu.andrew.application.business.dto;

/**
* <h1>NotificationMsgDTO</h1>
* 
* Data Transfer Object to send Error Messages to the end-user from the system.
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/
public class NotificationMsgDTO {
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
