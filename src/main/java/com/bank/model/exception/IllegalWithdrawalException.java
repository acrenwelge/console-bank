package com.bank.model.exception;

public class IllegalWithdrawalException extends BankException {

	private static final long serialVersionUID = -666072552425121440L;

	public IllegalWithdrawalException() {
	}

	public IllegalWithdrawalException(String message) {
		super(message);
	}

	public IllegalWithdrawalException(Throwable cause) {
		super(cause);
	}

	public IllegalWithdrawalException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalWithdrawalException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
