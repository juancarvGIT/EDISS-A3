package edu.cmu.andrew.application.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
* <h1>CustomerDTO</h1>
* 
* Data Transfer Object to transfer Customer data from end-user to the system.
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/
public class CustomerDTO {

	@NotBlank
	@NotNull
	@Email
	private String userId;
	@NotBlank
	@NotNull
	private String name;
	@NotBlank
	@NotNull
	private String phone;
	@NotBlank
	@NotNull
	private String address;

	private String address2;
	@NotBlank
	@NotNull
	private String city;
	@NotBlank
	@NotNull
	@Size(max = 2, min = 2)
	private String state;
	@NotBlank
	@NotNull
	private String zipcode;
	
	
	
	public CustomerDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CustomerDTO(@NotBlank @NotNull @Email String userId, @NotBlank @NotNull String name,
			@NotBlank @NotNull String phone, @NotBlank @NotNull String address, String address2,
			@NotBlank @NotNull String city, @NotBlank @NotNull @Size(max = 2, min = 2) String state,
			@NotBlank @NotNull String zipcode) {
		super();
		this.userId = userId;
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.address2 = address2;
		this.city = city;
		this.state = state;
		this.zipcode = zipcode;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	@Override
	public String toString() {
		return "{\"userId\": \"" + userId + "\", \"name\":\"" + name + "\", \"phone\":\"" + phone + "\", \"address\":\"" + address
				+ "\", \"address2\":\"" + address2 + "\", \"city\":\"" + city + "\", \"state\":\"" + state +"\", \"zipcode\":\"" + zipcode + "\"}";
	}
	

}
