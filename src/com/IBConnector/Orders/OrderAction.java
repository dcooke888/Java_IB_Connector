package com.IBConnector.Orders;

public enum OrderAction {
	BUY("BUY"), SELL("SELL"), SELL_SHORT("SSHORT");
	
	
	private OrderAction(String textValue) {
		this.textValue = textValue;
	}
	
	private final String textValue;
	
	@Override
	public String toString(){
		return textValue;
	}
}
