package com.bank.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.bank.util.Util;
import com.bank.model.AccountAction;
import com.bank.model.AccountType;
import com.bank.model.Transaction;
import com.bank.serialize.GeneralReaderWriter;
import com.bank.serialize.TransactionReaderWriter;

public class TransactionService {
	private TransactionReaderWriter trw;
	
	public TransactionService(TransactionReaderWriter trw) {
		this.trw = trw;
	}
	
	// CRUD OPERATIONS

	public void saveTransaction(Transaction tr) {
		Util.catchIOExceptionsReturnVoid(() -> trw.saveTransaction(tr));
	}
	
	public List<Transaction> getAllTransactions() {
		return Util.catchIOExceptionsReturnList(() -> GeneralReaderWriter.getAllObjects(Util.TRANSACTION_DIR));
	}
	
	public List<Transaction> getTransactionsByUsername(String username) {
		List<Transaction> list = Util.catchIOExceptionsReturnList(() -> GeneralReaderWriter.getAllObjects(Util.TRANSACTION_DIR));
		return filterByUser(list, username);
	}
	
	// FILTERS
	
	public static List<Transaction> filterByUser(List<Transaction> list, String username) {
		return list.stream().filter((Transaction t) ->
			// transaction was initiated by user if usernames match (other fields may differ - may have been updated)
			t.getInitiator().getUsername().equals(username)
		).collect(Collectors.toList());
	}
	
	public static List<Transaction> filterBeforeDate(List<Transaction> list, LocalDate date) {
		return list.stream().filter((Transaction t) -> 
			t.getDateTime().toLocalDate().isBefore(date)
		).collect(Collectors.toList());
	}
	
	public static List<Transaction> filterAfterDate(List<Transaction> list, LocalDate date) {
		return list.stream().filter((Transaction t) -> 
			t.getDateTime().toLocalDate().isAfter(date)
		).collect(Collectors.toList());
	}
	
	public static List<Transaction> filterBiggerThan(List<Transaction> list, BigDecimal size) {
		return list.stream().filter((Transaction t) -> 
			t.getAmount().compareTo(size) > 0
		).collect(Collectors.toList());
	}
	
	public static List<Transaction> filterSmallerThan(List<Transaction> list, BigDecimal size) {
		return list.stream().filter((Transaction t) -> 
			t.getAmount().compareTo(size) < 0
		).collect(Collectors.toList());
	}
	
	public static List<Transaction> filterByTransactionType(List<Transaction> list, AccountAction action) {
		return list.stream().filter((Transaction t) -> 
			t.getType().equals(action)
		).collect(Collectors.toList());
	}
	
	public static List<Transaction> filterByAccountType(List<Transaction> list, AccountType type) {
		return list.stream().filter((Transaction t) -> 
			t.getAcct().getAcctType().equals(type)
		).collect(Collectors.toList());
	}
}
