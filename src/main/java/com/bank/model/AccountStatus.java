package com.bank.model;

/**
 * AccountStatus is an enum to identify the different states that an account may be in:
 * <ul>
 *   <li>UNAPPROVED: for initial account creation, the account will remain in this status until an admin approves the account. Cannot deposit, withdraw, or transfer to/from an account in this status.</li>
 *   <li>ACTIVE: after admin approval, account moves to this status, which enables full functionality
 *   <li>INACTIVE: TBD</li>
 *   <li>SUSPENDED: an admin can manually change an account to this status, which suspends all functionality (deposit, withdrawal, transfers)</li>
 *   <li>CLOSED: the account has been closed by the user. Account balance must be empty before user can close an account. This status prevents all further transactions on the account. This account may never be modified after moving to this state.</li>
 * </ul>
 */
public enum AccountStatus {
	UNAPPROVED,
	ACTIVE,
	INACTIVE,
	SUSPENDED,
	CLOSED
}
