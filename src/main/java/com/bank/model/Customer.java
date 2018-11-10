package com.bank.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class Customer extends User implements Serializable {
	private static final long serialVersionUID = 6163435983108208454L;
	private LocalDate dob; // date of birth
	private Set<Integer> accounts = new TreeSet<>();
	private LocalDate customerSince;
	private String address;
	private String email;
	private String phoneNumber;
	private CustomerStatus custStatus;
	
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(LocalDate dob) {
		LocalDate today = LocalDate.now();
		if (dob.isBefore(today))
			this.dob = dob;
		else 
			throw new IllegalArgumentException("Birthday must be before today");
	}
	public Set<Integer> getAccounts() {
		return accounts;
	}
	public void setAccounts(Set<Integer> accounts) {
		this.accounts = accounts;
	}
	public void addNewAccount(int acctId) {
		if (this.accounts == null) this.accounts = new TreeSet<>();
		this.accounts.add(acctId);
	}
	public LocalDate getCustomerSince() {
		return customerSince;
	}
	public void setCustomerSince(LocalDate customerSince) {
		LocalDate today = LocalDate.now();
		if (customerSince.isEqual(today) || customerSince.isBefore(today))
			this.customerSince = customerSince;
		else
			throw new IllegalArgumentException("Must enter a date after today");
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		if (Pattern.matches(".+@.+\\..+", email))
			this.email = email;
		else
			throw new IllegalArgumentException("Must enter valid email address");
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		if (Pattern.matches("^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$", phoneNumber))
			this.phoneNumber = phoneNumber;
		else
			throw new IllegalArgumentException("Must enter valid phone number");
	}
	public CustomerStatus getCustStatus() {
		return custStatus;
	}
	public void setCustStatus(CustomerStatus custStatus) {
		this.custStatus = custStatus;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((custStatus == null) ? 0 : custStatus.hashCode());
		result = prime * result + ((customerSince == null) ? 0 : customerSince.hashCode());
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + id;
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (custStatus != other.custStatus)
			return false;
		if (customerSince == null) {
			if (other.customerSince != null)
				return false;
		} else if (!customerSince.equals(other.customerSince))
			return false;
		if (dob == null) {
			if (other.dob != null)
				return false;
		} else if (!dob.equals(other.dob))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id != other.id)
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Customer [id=" + id + ", username=" + username + ", accountStatus=" + custStatus + ", firstName=" + firstName + 
				", lastName=" + lastName + ", dob=" + dob + ", accounts=" + accounts + ", customerSince=" + customerSince + ", address="
				+ address + ", email=" + email + ", phoneNumber=" + phoneNumber + "]";
	}
	
	public Customer(int id, String firstName, String lastName, String username, String pword, LocalDate dob, Set<Integer> accounts,
			LocalDate customerSince, String address, String email, String phoneNumber) {
		super(id, username, pword, firstName, lastName);
		this.dob = dob;
		this.accounts = accounts;
		this.customerSince = customerSince;
		this.address = address;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}
	
	public Customer() {}
}
