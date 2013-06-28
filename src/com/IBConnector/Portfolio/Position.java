package com.IBConnector.Portfolio;

import com.ib.client.Contract;

public class Position {
	
	public final Contract contract;
	public final int positionSize;
	public final double marketPrice;
	public final double marketValue;
	public final double averageCost;
	public final double unrealizedPNL;
	public final double realizedPNL;
	public final String accountName;

	public Position(Contract contract, int positionSize, double marketPrice, double marketValue, 
			double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
		this.contract = contract;
		this.positionSize = positionSize;
		this.marketPrice = marketPrice;
		this.marketValue = marketValue;
		this.averageCost = averageCost;
		this.unrealizedPNL = unrealizedPNL;
		this.realizedPNL = realizedPNL;
		this.accountName = accountName;
	}
	
	public void printPosition() {
		System.out.printf("Symbol: %s, PositionSize: %d, MarketPrice: %.2f, MarketValue: %.2f, AvgCost: %.2f, UnrealizedPNL: %.2f, RealizedPNL: %.2f, AccountName: %s %n",
				contract.m_symbol, positionSize, marketPrice, marketValue, averageCost, unrealizedPNL, realizedPNL, accountName);
	}
}
