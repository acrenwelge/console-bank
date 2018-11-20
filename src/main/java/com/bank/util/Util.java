package com.bank.util;

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
	private static Logger out = Util.getConsoleLogger();
	
	public static final String TRANSACTION_DIR = "Transactions/";
	public static final String CONSOLE_LOGGER = "STDLOGGER";
	public static final String ERR_LOGGER = "STDERR";
	
	public ClassLoader getClassLoader() {
		return cl;
	}	
	
	public static Scanner getScanner() {
		return sc;
	}
	
	public static Logger getFileLogger() {
		return LogManager.getLogger(com.bank.App.class);
	}
	
	public static Logger getConsoleLogger() {
		return LogManager.getLogger(CONSOLE_LOGGER);
	}
	
	// Interfaces and generic methods for centralizing catching/handling IOExceptions
	
	public interface NoReturnMethod {
		public void doSomething() throws IOException;
	}
	
	public interface MethodReturningList<T> {
		public List<T> doSomethingAndReturnList() throws IOException;
	}
	
	public interface MethodReturningBoolean {
		public boolean doSomethingAndReturnBool() throws IOException;
	}
	
	public interface MethodReturningType<T> {
		public T doSomethingAndReturnType() throws IOException;
	}
	
	public static void catchIOExceptionsReturnVoid(NoReturnMethod m) {
		try {
			m.doSomething();
		} catch (IOException e) {
			getFileLogger().error(MessageHolder.exceptionLogMsg, e);
			out.error(MessageHolder.ioMessage);
		}
	}
	
	public static boolean catchIOExceptionsReturnBool(MethodReturningBoolean m) {
		try {
			return m.doSomethingAndReturnBool();
		} catch (IOException e) {
			getFileLogger().error(MessageHolder.exceptionLogMsg, e);
			out.error(MessageHolder.ioMessage);
		}
		return false;
	}
	
	public static <T> List<T> catchIOExceptionsReturnList(MethodReturningList<T> m) {
		try {
			return m.doSomethingAndReturnList();
		} catch (FileNotFoundException fnfe) {
			getFileLogger().error(MessageHolder.actNotFound);
		} catch (IOException e) {
			getFileLogger().error(MessageHolder.exceptionLogMsg, e);
			out.error(MessageHolder.ioMessage);
		}
		return new ArrayList<>();
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
			getFileLogger().error(MessageHolder.actNotFound);
		} catch (IOException e) {
			getFileLogger().error(MessageHolder.exceptionLogMsg, e);
			out.error(MessageHolder.ioMessage);
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
