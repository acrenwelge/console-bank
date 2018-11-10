package com.bank.model;

import java.io.Serializable;

public class Admin extends User implements Serializable {
	private static final long serialVersionUID = -75333213669647063L;
	
	@Override
	public String toString() {
		return "Admin [id=" + id + ", firstname=" + firstName + ", lastname=" + lastName + ", username=" + username
				+ ", password=" + password + "]";
	}
	
	public Admin(int id, String firstname, String lastname, String username, String password) {
		super();
		this.id = id;
		this.firstName = firstname;
		this.lastName = lastname;
		this.username = username;
		this.password = password;
	}
	
	public Admin() {}
	
}
