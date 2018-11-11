package com.bank.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GeneralReaderWriter {
	
	/**
	 * Returns a typed List of all Objects read from the directory passed as a parameter.
	 * For efficiency gains, reads objects into a LinkedList, then converts to and returns an ArrayList.
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public static <T> List<T> getAllObjects(final String dir) throws IOException {
		List<T> linked = new LinkedList<>();
		File[] files = new File(dir).listFiles();
		for (File f : files) {
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
				try {
					T typedObj = (T) ois.readObject();
					linked.add(typedObj);
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new ArrayList<>(linked);
	}
}
