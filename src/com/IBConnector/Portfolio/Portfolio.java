package com.IBConnector.Portfolio;

import java.util.HashMap;

import com.ib.client.Contract;

public class Portfolio {

	private String accountName;
	private boolean printPositionUpdates = false;
	HashMap<String, Position> positions = new HashMap<String, Position>();
	
	public Portfolio() {	this.accountName = "NOT_SET";	}
	public Portfolio(String accountName) { this.accountName = accountName;	}
	
	/**
	 * for debugging purposes prints all position updates to console, default is printing off
	 * @param print
	 */
	public void printUpdates(boolean printPositionUpdates) { this.printPositionUpdates = printPositionUpdates;	}
	
	public void updatePosition(Position position) {
		if(printPositionUpdates) position.printPosition();
		if(accountName.equals("NOT_SET")) accountName = position.accountName;	
		if(position.contract.m_symbol != null) {
			positions.put(position.contract.m_symbol, position);
		} else {
			System.out.println("Portfolio ERROR: Contract Symbol String not set");
		}
	}
	
	public Position[] getAllPositions() { return positions.values().toArray(new Position[positions.size()]); }
	public Position getPosition(String symbol) { return positions.get(symbol);	}
	public void printPosition(String symbol) {
		Position position = positions.get(symbol);
		if(position != null) position.printPosition();
		else System.out.println("Error: Position for symbol[" + symbol + "] not found");
	}
	public void printAllPositions() {	for(Position position: positions.values()) position.printPosition();	}
	
	public String[] getAllSymbols() {
		String[] symbols = new String[positions.size()];
		int i = 0;
		for(Position position: positions.values()) {
			symbols[i++] = position.contract.m_symbol;
		}
		return symbols;
	}
	
	
	/*
	 * -----------------------------------------------
	 * 	  Retrieval Methods for a specific contract
	 * -----------------------------------------------
	 */
	public Contract getContract(String symbol) 	{ return positions.get(symbol).contract;		}
	public int getPositionSize(String symbol)	{ return positions.get(symbol).positionSize;	}
	public double getMarketPrice(String symbol)	{ return positions.get(symbol).marketPrice;		}
	public double getMarketValue(String symbol) { return positions.get(symbol).marketValue;		}
	public double getAverageCost(String symbol) { return positions.get(symbol).averageCost;		}
	public double getUnrealizedPNL(String symbol) { return positions.get(symbol).unrealizedPNL;	}
	public double getRealizedPNL(String symbol) { return positions.get(symbol).unrealizedPNL;	}
	public String getAccountName() { 
		if(accountName.equals("NOT_SET")) return "AccountName not set";
		else return accountName;
	}
}
