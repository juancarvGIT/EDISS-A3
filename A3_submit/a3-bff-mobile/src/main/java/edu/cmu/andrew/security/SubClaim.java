package edu.cmu.andrew.security;

/**
 * <h1>SubClaim enum</h1>
 * 
 * This enumeration represents the valid subjects expected in every JWT request
 * 
 * @author Juan Carlos Villegas Montiel
 * @version 1.0
 * @since 2024-03-29
 */
public enum SubClaim {
	
	STARLORD("starlord"), GAMORA("gamora"), DRAX("drax"), ROCKET("rocket"), GROOT("groot");   

	private String value;  

	private SubClaim(String value){  

	this.value=value;  

	}  
	
	public String getValue() {
		return this.value;
	}

}
