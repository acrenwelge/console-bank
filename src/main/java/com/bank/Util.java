package com.bank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bank.model.MessageHolder;

public class Util {
	private static ClassLoader cl = Thread.currentThread().getContextClassLoader();
	private static Scanner sc = new Scanner(System.in);
	public static final String TRANSACTION_DIR = "Transactions/";
	
	public ClassLoader getClassLoader() {
		return cl;
	}	
	
	public static Scanner getScanner() {
		return sc;
	}
	
	public static Logger getLogger() {
		return LogManager.getLogger(com.bank.App.class);
	}
	
	// Interfaces and generic methods for centralizing catching/handling IOExceptions
	
	public interface NoReturnMethod {
		public void doSomething() throws IOException;
	}
	
	public interface MethodReturningList<T> {
		public List<T> doSomethingAndReturnList() throws IOException;
	}
	
	public interface MethodReturningType<T> {
		public T doSomethingAndReturnType() throws IOException;
	}
	
	public static void catchIOExceptionsVoid(NoReturnMethod m) {
		try {
			m.doSomething();
		} catch (IOException e) {
			getLogger().error(MessageHolder.exceptionLogMsg, e);
			System.err.println(MessageHolder.ioMessage);
		}
	}
	
	public static <T> List<T> catchIOExceptionsReturnList(MethodReturningList<T> m) {
		try {
			return m.doSomethingAndReturnList();
		} catch (FileNotFoundException fnfe) {
			getLogger().error(MessageHolder.actNotFound);
		} catch (IOException e) {
			getLogger().error(MessageHolder.exceptionLogMsg, e);
			System.err.println(MessageHolder.ioMessage);
		}
		return new ArrayList<T>();
	}
	
	/**
	 * Will attempt to invoke the method and return a specific object. 
	 * If IOException is thrown, the error will be logged and <code>null</code> is returned. 
	 * @param m
	 * @return
	 */
	public static <T> T catchIOExceptionsReturnType(MethodReturningType<T> m) {
		try {
			return m.doSomethingAndReturnType();
		} catch (FileNotFoundException fnfe) {
			getLogger().error(MessageHolder.actNotFound);
		} catch (IOException e) {
			getLogger().error(MessageHolder.exceptionLogMsg, e);
			System.err.println(MessageHolder.ioMessage);
		}
		return null;
	}
	
	/**
	 * Gets a file from src/main/resources
	 * @param fileName
	 * @return
	 */
	public static File getFileFromResources(String fileName) {
		return new File(cl.getResource(fileName).getFile());
	}
	
	/**
	 * Returns the number of decimal places in the BigDecimal
	 * @param bigDecimal
	 * @return
	 */
	public static int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
	    String string = bigDecimal.stripTrailingZeros().toPlainString();
	    int index = string.indexOf('.');
	    return index < 0 ? 0 : string.length() - index - 1;
	}
}
