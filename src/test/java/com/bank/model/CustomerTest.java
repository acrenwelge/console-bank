package com.bank.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Month;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CustomerTest {
	private Customer cust;
 
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		cust = new Customer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=IllegalArgumentException.class)
	public void testDobSad() {
		LocalDate ld = LocalDate.of(LocalDate.now().getYear()+1, 1, 1); // next year - invalid
		cust.setDob(ld);
	}
	
	@Test
	public void testDobHappy() {
		LocalDate ld = LocalDate.of(1990, Month.DECEMBER, 31); // valid
		cust.setDob(ld);
		assertEquals(cust.getDob(), ld);
	}

	@Test
	public void testCustomerSinceHappy() {
		LocalDate ld = LocalDate.now(); // today - valid
		cust.setCustomerSince(ld);
		assertEquals(cust.getCustomerSince(), ld);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCustomerSinceSad() {
		LocalDate ld = LocalDate.of(LocalDate.now().getYear()+1, Month.APRIL, 1); // next year - invalid (April Fools! :P) 
		cust.setCustomerSince(ld);
	}

	@Test
	public void testEmailHappy() {
		final String[] validEmails = {
				"andrew@google.com",
				"Andr3w123@google.com",
				"a.j.c@yahoo.net"
		};
		for (String email : validEmails) {
			try {
				cust.setEmail(email);
				assertEquals(cust.getEmail(), email);
			} catch (IllegalArgumentException iae) {
				fail("Rejected valid email: " + email);
			}
		}
	}
	
	@Test
	public void testEmailSad() {
		final String[] invalidEmails = {
				"just some text",
				"in@complete",
				"not2good!nopenet",
				"john.doe.com",
				"onlyAURL.org"
		};
		int excepts = 0;
		for (String email : invalidEmails) {
			try {
				cust.setEmail(email);
			} catch(IllegalArgumentException iae) {
				excepts++;
			}
		}
		assertEquals(excepts, invalidEmails.length);
	}

	@Test
	public void testPhoneNumberHappy() {
		String[] validNums = {
				"1234567890",
				"123-456-7890",
				"+1 496-243-0076",
				"789 321 7364",
				"789.321.7364"
		};
		for (String number : validNums) {
			try {
				cust.setPhoneNumber(number);
				assertEquals(cust.getPhoneNumber(), number);
			} catch (IllegalArgumentException iae) {
				fail("Rejected valid phone number: " + number);
			}
		}
	}
	
	@Test
	public void testPhoneNumberSad() {
		String[] invalidNumbers = {
				"512345678901",
				"1234-567-890",
				"123-4567-890",
				"1 2 3 4 5 6 7 8 9 0",
				"yellowdogs",
				"i23-j45-o0o"
		};
		int excepts = 0;
		for (String num : invalidNumbers) {
			try {
				cust.setPhoneNumber(num);
			} catch(IllegalArgumentException iae) {
				excepts++;
			}
		}
		assertEquals(excepts, invalidNumbers.length);
	}

}
