package com.bank;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestScanner {
	class InputOutput {
		public String getInput() {
	        try(Scanner sc = new Scanner(System.in)) {
	        	return sc.nextLine();
	        }
	    }
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldTakeUserInput() {
	    InputOutput inputOutput= new InputOutput();

	    String input = "add 5";
	    InputStream in = new ByteArrayInputStream(input.getBytes());
	    System.setIn(in);

	    assertEquals("add 5", inputOutput.getInput());
	}

}
