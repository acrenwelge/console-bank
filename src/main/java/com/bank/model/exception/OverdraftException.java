package com.bank.model.exception;

public class OverdraftException extends BankException {

	private static final long serialVersionUID = -3841840061850649550L;

	public OverdraftException() {
	}

	public OverdraftException(String message) {
		super(message);
	}

	public OverdraftException(Throwable cause) {
		super(cause);
	}

	public OverdraftException(String message, Throwable cause) {
		super(message, cause);
	}

	public OverdraftException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
