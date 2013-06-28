package com.IBConnector.Account;

import java.util.HashMap;

public class Account {

	private String accountName;
	private HashMap<String, AccountDetail> accountInfo = new HashMap<String, AccountDetail>();
	private boolean printAccountUpdates = false;
	
	public Account() {	this.accountName = "NOT_SET";	}
	public Account(String accountName) { this.accountName = accountName;	}
	
	
	/**
	 * for debugging purposes prints all account updates to console, default is printing off
	 * @param print
	 */
	public void printUpdates(boolean printAccountUpdates) { this.printAccountUpdates  = printAccountUpdates;	}
	public void updateAccountDetail(AccountDetail detail) {
		if(printAccountUpdates) detail.print();
		if(accountName.equalsIgnoreCase(detail.accountName)) {
			accountInfo.put(detail.key, detail);
		} else if( accountName.equals("NOT_SET")) {
			accountName = detail.accountName;
			accountInfo.put(detail.key, detail);
		} else {
			System.out.println("Wrong account number");
		}
	}
	
	public AccountDetail[] getAllAccountInfo() {
		return accountInfo.values().toArray(new AccountDetail[accountInfo.size()]);
	}

	public String getAccountName() { 
		if(accountName.equals("NOT_SET")) return "AccountName not set";
		else return accountName;
	}
	
	public void printAccountInfo(String key) {
		AccountDetail detail = accountInfo.get(key);
		if(detail != null) detail.print();
		else System.out.println("Error: Key[" + key + "] not found");
	}
	
	public void printAllAccountInfo() {
		for(AccountDetail detail: accountInfo.values()) {
			detail.print();
		}
	}
	
	/*
	 * -------------------------------------------------------
	 * 	  Retrieval Methods for a specific Account parameter
	 * -------------------------------------------------------
	 */
	public AccountDetail getAccountInfo(String key) { return accountInfo.get(key); }
	public String getValue(String key) { return accountInfo.get(key).value; }
	public String getCurrency(String key) { return accountInfo.get(key).currency; }
}
