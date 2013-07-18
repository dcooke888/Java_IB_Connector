package com.IBConnector.Data;

public class SnapShot {

	public final String symbol;
	public final PriceSize bid;
	public final PriceSize Ask;
	public final double midPrice;
	public final long time;
	
	public SnapShot(PriceSize bid, PriceSize ask, long time) {
		this.symbol = bid.symbol;
		this.bid = bid;
		this.Ask = ask;
		this.time = time;
		this.midPrice = (bid.getPrice() + ask.getPrice()) / 2;
	}
}
