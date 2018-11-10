package com.bank.model.exception;

import com.bank.model.exception.BankException;

public class IllegalDepositException extends BankException {

	private static final long serialVersionUID = 126452007049225641L;

	public IllegalDepositException() {
	}

	public IllegalDepositException(String message) {
		super(message);
	}

	public IllegalDepositException(Throwable cause) {
		super(cause);
	}

	public IllegalDepositException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalDepositException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
