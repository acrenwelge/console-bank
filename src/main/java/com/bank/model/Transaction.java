package com.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
	private static final long serialVersionUID = 7931675956101877444L;
	private AccountAction type;
	private LocalDateTime dateTime;
	private BigDecimal amount;
	private User initiator;
	// acct used for deposits/withdrawals
	private Account acct;
	// transferFrom and transferTo are used for transfers of funds
	private Account transferFrom;
	private Account transferTo;
	
	public Account getTransferFrom() {
		return transferFrom;
	}
	public void setTransferFrom(Account transferFrom) {
		this.transferFrom = transferFrom;
	}
	public Account getTransferTo() {
		return transferTo;
	}
	public void setTransferTo(Account transferTo) {
		this.transferTo = transferTo;
	}
	public Account getAcct() {
		return acct;
	}
	public void setAcct(Account acct) {
		this.acct = acct;
	}
	public AccountAction getType() {
		return type;
	}
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public User getInitiator() {
		return initiator;
	}
	public void setInitiator(User initiator) {
		this.initiator = initiator;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + ((initiator == null) ? 0 : initiator.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Transaction other = (Transaction) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		if (initiator == null) {
			if (other.initiator != null)
				return false;
		} else if (!initiator.equals(other.initiator))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	@Override
	public String toString() {
		String user = (initiator instanceof Admin) ? "Admin " : "Customer ";
		String action = "";
		String word = "";
		switch(type) {
		case DEPOSIT:  {action = " deposited ";  word=" into";}  break;
		case WITHDRAW: {action = " withdrew ";   word=" from";}  break;
		case TRANSFER: {action = " transferred ";} break;
		}
		String begin = dateTime.toString()+ " - "+ user 
				+ initiator.getUsername() 
				+ action 
				// TODO: FIX THIS
				//+ acct.getCurrency().getSymbol() // throws NullPointerException if transaction is a transfer 
				+ amount;
		if (type.equals(AccountAction.TRANSFER))
			return begin + " from account #"+transferFrom.getId() + " to account #"+transferTo.getId();
		else
			return begin + word + " account #"+acct.getId();
	}
	
	/**
	 * This constructor is for creating Deposit and Withdrawal transactions
	 * @param type
	 * @param dateTime
	 * @param amount
	 * @param initiator
	 * @param acct
	 */
	public Transaction(AccountAction type, LocalDateTime dateTime, BigDecimal amount, User initiator, Account acct) {
		super();
		this.type = type;
		this.dateTime = dateTime;
		this.amount = amount;
		this.initiator = initiator;
		this.acct = acct;
	}
	
	/**
	 * This constructor is for creating Transfer transactions
	 * @param dateTime
	 * @param amount
	 * @param initiator
	 * @param acctFrom
	 * @param acctTo
	 */
	public Transaction(LocalDateTime dateTime, BigDecimal amount, User initiator, Account acctFrom, Account acctTo) {
		super();
		this.type = AccountAction.TRANSFER;
		this.dateTime = dateTime;
		this.amount = amount;
		this.initiator = initiator;
		this.transferFrom = acctFrom;
		this.transferTo = acctTo;
	}
	
}
