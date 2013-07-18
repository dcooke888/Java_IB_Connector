package com.IBConnector.Data;

import java.util.HashMap;

import com.IBConnector.IBConnector;

public class TOBUpdater {

	HashMap<String, PriceSize> bestBid = new HashMap<String, PriceSize>();
	HashMap<String, PriceSize> bestAsk = new HashMap<String, PriceSize>();
	
	public void notifyPriceChange(String symbol, int updateType, double price) {
		if(updateType == IBConnector.TICK_PRICE_BID) {
			bestBid.put(symbol, updatePrice(bestBid.get(symbol), price));
		} else if(updateType == IBConnector.TICK_PRICE_ASK) {
			bestAsk.put(symbol, updatePrice(bestAsk.get(symbol), price));
		}
	}
	
	private PriceSize updatePrice(PriceSize lastUpdate, double price) {
		if(lastUpdate == null) lastUpdate = new PriceSize();
		lastUpdate.setPrice(price);
		return lastUpdate;
	}
	
	public void notifySizeChange(String symbol, int updateType, int size) { 
		if(updateType == IBConnector.TICK_SIZE_BID) {
			bestBid.put(symbol, updateSize(bestBid.get(symbol), size));
		} else if (updateType == IBConnector.TICK_SIZE_ASK){
			bestAsk.put(symbol, updateSize(bestAsk.get(symbol), size));
		}
	}
	
	private PriceSize updateSize(PriceSize lastUpdate, int size) {
		if(lastUpdate == null) lastUpdate = new PriceSize();
		lastUpdate.setSize(size);
		return lastUpdate;
	}
	
	public void removeBidAsk(String symbol) {
		bestBid.remove(symbol);
		bestAsk.remove(symbol);
	}
	
	public PriceSize getBestBid(String symbol) {
		return bestBid.get(symbol);
	}
	
	public PriceSize getBestAsk(String symbol) {
		return bestAsk.get(symbol);
	}
	
	public double getMidPrice(String symbol) {
		PriceSize bid = bestBid.get(symbol);
		PriceSize ask = bestAsk.get(symbol);
		if(bid == null) return -1;
		else if(ask == null) return -1;
		
		return (bid.getPrice() + ask.getPrice()) / 2;
	}
}
