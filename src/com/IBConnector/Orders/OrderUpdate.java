package com.IBConnector.Orders;

public class OrderUpdate {

	public final int orderId;
	public final OrderStatus status;
	public final int filled;
	public final int remaining;
	public final double avgFillPrice;
	public final int permId;
	public final int parentId;
	public final double lastFillPrice;
	public final int clientId;
	public final String whyHeld;
	
	public OrderUpdate(int orderId, OrderStatus status, int filled, int remaining, 
			double avgFillPrice, int permId, int parentId, double lastFillPrice, 
			int clientId, String whyHeld) {
		this.orderId = orderId;
		this.status = status;
		this.filled = filled;
		this.remaining = remaining;
		this.avgFillPrice = avgFillPrice;
		this.permId = permId;
		this.parentId = parentId;
		this.lastFillPrice = lastFillPrice;
		this.clientId = clientId;
		this.whyHeld = whyHeld;
	}
	
	public void print() {
		System.out.printf("ORDER UPDATE: OrderId: %d, OrderStatus: %s, AmtFilled: %d, AmtRemaining: %d, AvgFillPrice: %.2f, PermId: %d, ParentId: %d, LastFillPrice: %.2f, ClientId: %d, WhyHeld: %s%n",
				orderId, status.getLabel(), filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
	}
}
