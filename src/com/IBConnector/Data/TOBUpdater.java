package com.IBConnector.Data;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TimeZone;

import com.IBConnector.IBConnector;

public class TOBUpdater {

	HashMap<String, PriceSize> bestBid = new HashMap<String, PriceSize>();
	HashMap<String, PriceSize> bestAsk = new HashMap<String, PriceSize>();
	
	public void notifyPriceChange(String symbol, int updateType, double price) {
		if(updateType == IBConnector.TICK_PRICE_BID) {
			bestBid.put(symbol, updatePrice(symbol, bestBid.get(symbol), price));
		} else if(updateType == IBConnector.TICK_PRICE_ASK) {
			bestAsk.put(symbol, updatePrice(symbol, bestAsk.get(symbol), price));
		}
	}
	
	private PriceSize updatePrice(String symbol, PriceSize lastUpdate, double price) {
		if(lastUpdate == null) lastUpdate = new PriceSize(symbol);
		lastUpdate.setPrice(price);
		return lastUpdate;
	}
	
	public void notifySizeChange(String symbol, int updateType, int size) { 
		if(updateType == IBConnector.TICK_SIZE_BID) {
			bestBid.put(symbol, updateSize(symbol, bestBid.get(symbol), size));
		} else if (updateType == IBConnector.TICK_SIZE_ASK){
			bestAsk.put(symbol, updateSize(symbol, bestAsk.get(symbol), size));
		}
	}
	
	private PriceSize updateSize(String symbol, PriceSize lastUpdate, int size) {
		if(lastUpdate == null) lastUpdate = new PriceSize(symbol);
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
	
	public String[] getAllKeys() {
		HashSet<String> tickers = new HashSet<String>();
		tickers.addAll(bestBid.keySet());
		tickers.addAll(bestAsk.keySet());
		return tickers.toArray(new String[tickers.size()]);
	}
	
	public SnapShot getSnapShot(String symbol) {
		PriceSize bid = bestBid.get(symbol);
		PriceSize ask = bestAsk.get(symbol);
		if(isPriceSizeSet(bid) && isPriceSizeSet(ask) ) {
			return new SnapShot(bid, ask, getTime());
		}
		return null;
	}
	
	private boolean isPriceSizeSet(PriceSize priceSize) {
		if(priceSize != null && priceSize.getPrice() > 0 && priceSize.getSize() != -1) return true;
		return false;
	}
	
	
	private long getTime() {
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return cal.getTimeInMillis();
	}

	public PriceSize[] getAllBids() {
		return bestBid.values().toArray(new PriceSize[bestBid.size()]);
	}
	
	public PriceSize[] getAllAsks() {
		return bestAsk.values().toArray(new PriceSize[bestAsk.size()]);
	}
}
