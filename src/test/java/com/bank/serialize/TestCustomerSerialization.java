package com.bank.serialize;

import static com.bank.serialize.CustomerReaderWriter.getCustomerByUsername;
import static com.bank.serialize.CustomerReaderWriter.saveCustomer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.bank.model.Customer;

public class TestCustomerSerialization {
	public static Customer c;

	@Before
	public void setUp() throws Exception {
		c = new Customer();
	}

	@After
	public void tearDown() throws Exception {
		c = null;
	}

	@Ignore
	@Test
	public void testCheckNewUsername() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveCustomer() throws IOException {
		c.setUsername("somecrazyus3rname");
		saveCustomer(c);
		Customer newCust = getCustomerByUsername("somecrazyus3rname");
		assertEquals(c, newCust);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSaveCustomerNull() throws IOException {
		c.setUsername("");
		saveCustomer(c);
		c.setUsername(null);
		saveCustomer(c);
	}

	@Ignore
	@Test
	public void testRegisterNewCustomer() {
		c.setFirstName("testFname");
		c.setLastName("testLname");
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetCustomerByUsername() {
		fail("Not yet implemented");
	}

}
