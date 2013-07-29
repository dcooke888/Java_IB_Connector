package com.IBConnector.Data;

public class SnapShot {

	public final String symbol;
	public final double bidPrice;
	public final double askPrice;
	public final int bidSize;
	public final int askSize;
	public final double midPrice;
	public final long time;
	
	public SnapShot(PriceSize bid, PriceSize ask, long time) {
		this.symbol = bid.symbol;
		this.bidPrice = bid.getPrice();
		this.bidSize = bid.getSize();
		this.askPrice = ask.getPrice();
		this.askSize = ask.getSize();
		this.time = time;
		this.midPrice = (bid.getPrice() + ask.getPrice()) / 2;
	}
	
	public void printSnapShot() {
		System.out.printf("Ticker: %s, Bid: %.2f, %d, Ask: %.2f, %d, Mid: %.2f, %d %n", symbol, bidPrice, bidSize, askPrice, askSize, midPrice, time);
	}
}
