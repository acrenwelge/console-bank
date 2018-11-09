package com.bank;

import java.io.File;
import java.math.BigDecimal;
import java.util.Scanner;

public class Util {
	
	private static ClassLoader cl = Thread.currentThread().getContextClassLoader();
	
	public ClassLoader getClassLoader() {
		return cl;
	}
	
	private static Scanner sc = new Scanner(System.in);
	
	public static Scanner getScanner() {
		return sc;
	}
	
	public static File getFileFromResources(String fileName) {
		return new File(cl.getResource(fileName).getFile());
	}
	
	public static void inputLoop() {
		
	}
	
	public static int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
	    String string = bigDecimal.stripTrailingZeros().toPlainString();
	    int index = string.indexOf('.');
	    return index < 0 ? 0 : string.length() - index - 1;
	}
}
