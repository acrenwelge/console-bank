package com.bank.services;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bank.model.Account;
import com.bank.model.exception.BankException;
import com.bank.services.AccountService;

public class TestAccountService {
	
	Account acct1;
	Account acct2;

	@Before
	public void setUp() throws Exception {
		acct1 = new Account();
		acct2 = new Account();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDeposit() {
		// start from default - $0
//		try {
//			acct1.depositMoney(new BigDecimal("100"));
//		} catch (IllegalDepositException e) {
//			e.printStackTrace();
//		}
//		assertEquals(acct1.getBalance(), BigDecimal.valueOf(100));
//		try {
//			acct1.depositMoney(BigDecimal.valueOf(45.23));
//		} catch (IllegalDepositException e) {
//			e.printStackTrace();
//		}
//		assertEquals(acct1.getBalance(), BigDecimal.valueOf(145.23));
	}
	
	@Test
	public void testWithdraw() {
		acct1.setBalance(BigDecimal.valueOf(100.01));
//		try {
//			acct1.withdrawMoney(BigDecimal.valueOf(50.01));
//		} catch (BankException e) {
//			e.printStackTrace();
//		}
//		assertTrue(acct1.getBalance().compareTo(BigDecimal.valueOf(50.00)) == 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOverdraft() {
		acct1.setBalance(BigDecimal.valueOf(100.01));
//		try {
//			acct1.withdrawMoney(BigDecimal.valueOf(101.01));
//		} catch (BankException e) {
//			e.printStackTrace();
//		}
	}
	
	@Test
	public void testTransfer() {
		acct1.setBalance(BigDecimal.valueOf(39.93));
		acct2.setBalance(BigDecimal.valueOf(24.41));
		try {
			AccountService.transferFunds(acct1, acct2, BigDecimal.valueOf(10.49));
		} catch (BankException e) {
			e.printStackTrace();
		}
		assertTrue(acct1.getBalance().compareTo(BigDecimal.valueOf(29.44)) == 0);
		assertTrue(acct2.getBalance().compareTo(BigDecimal.valueOf(34.90)) == 0);
	}

}
