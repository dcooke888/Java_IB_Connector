package com.IBConnector.Account;

public class AccountDetail {

	public final String key;
	public final String value;
	public final String currency;
	public final String accountName;
	
	public AccountDetail(String key, String value, String currency, String accountName) {
		this.key = key;
		this.value = value;
		this.currency = currency;
		this.accountName = accountName;
	}
	
	public void print() {
		System.out.printf("Key: %s, Value: %s, Currency: %s, AccountName: %s%n", key, value, currency, accountName);
	}
}
