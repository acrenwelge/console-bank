package com.bank.services;

import org.apache.logging.log4j.Logger;

import com.bank.util.Util;
import com.bank.model.Admin;
import com.bank.serialize.AdminReaderWriter;

public class AdminService {
	private static Logger log = Util.getFileLogger();
	private AdminReaderWriter arw;
	
	public AdminService(AdminReaderWriter arw) {
		this.arw = arw;
	}

	public Admin getAdminByUsername(String username) {
		return Util.catchIOExceptionsReturnType(() -> arw.getAdminByUsername(username));
	}
	
	public void saveAdmin(Admin a) {
		Util.catchIOExceptionsReturnVoid(() -> arw.saveAdmin(a));
	}
	
	public void registerAdmin(Admin ad) {
		Util.catchIOExceptionsReturnVoid(() -> arw.registerNewAdmin(ad));
		log.info("Admin " + ad.getUsername() + " registered");
	}
	
}
