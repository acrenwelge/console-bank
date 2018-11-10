package com.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Locale;

public class Account implements Serializable {
	
	private static final long serialVersionUID = -1512918144948779050L;
	private int id;
	private String name;
	private LocalDate creationDate;
	private Currency currency = Currency.getInstance(Locale.US);
	private BigDecimal balance = BigDecimal.ZERO;
	private AccountStatus acctStatus;
	private AccountType acctType;
		
	// getters/setters
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public AccountStatus getAcctStatus() {
		return acctStatus;
	}
	public void setAcctStatus(AccountStatus acctStatus) {
		this.acctStatus = acctStatus;
	}
	public AccountType getAcctType() {
		return acctType;
	}
	public void setAcctType(AccountType acctType) {
		this.acctType = acctType;
	}
	
	@Override
	public String toString() {
		return "Account [id=" + id + ", name=" + name + ", creationDate=" + creationDate + ", currency=" + currency
				+ ", balance=" + balance + ", acctStatus=" + acctStatus + ", acctType=" + acctType + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acctStatus == null) ? 0 : acctStatus.hashCode());
		result = prime * result + ((acctType == null) ? 0 : acctType.hashCode());
		result = prime * result + ((balance == null) ? 0 : balance.hashCode());
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Account other = (Account) obj;
		if (acctStatus != other.acctStatus)
			return false;
		if (acctType != other.acctType)
			return false;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public Account(Currency currency, BigDecimal balance, AccountStatus acctStatus, AccountType acctType) {
		super();
		this.currency = currency;
		this.balance = balance;
		this.acctStatus = acctStatus;
		this.acctType = acctType;
	}
	
	public Account() {}

}
