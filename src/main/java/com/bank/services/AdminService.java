package com.bank.services;

import org.apache.logging.log4j.Logger;

import com.bank.Util;
import com.bank.model.Admin;
import com.bank.serialize.AdminReaderWriter;

public class AdminService {
	private AdminService() {}
	private static Logger log = Util.getLogger();

	public static Admin getAdminByUsername(String username) {
		return Util.catchIOExceptionsReturnType(() -> AdminReaderWriter.getAdminByUsername(username));
	}
	
	public static void saveAdmin(Admin a) {
		Util.catchIOExceptionsVoid(() -> AdminReaderWriter.saveAdmin(a));
	}
	
	public static void registerAdmin(Admin ad) {
		Util.catchIOExceptionsVoid(() -> AdminReaderWriter.registerNewAdmin(ad));
		log.info("Admin " + ad.getUsername() + " registered");
	}
	
}
