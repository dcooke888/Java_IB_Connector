package com.IBConnector.Data;

public class PriceSize {

	public final String symbol;
	private double price = -1;
	private int size = -1;

	public PriceSize(String symbol) {
		this.symbol = symbol;
	}
	public void setPrice(double price) { this.price = price;	}
	public void setSize(int size) { this.size = size;	}
	
	public double getPrice() { return price;	}
	public int getSize()	{ return size;		}
	
}
