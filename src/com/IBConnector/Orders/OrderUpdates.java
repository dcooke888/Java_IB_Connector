package com.IBConnector.Orders;

import java.util.HashMap;

public class OrderUpdates {
	private int uniqueOrderId;
	private HashMap<Integer, OrderUpdate> allOrders = new HashMap<Integer, OrderUpdate>();
	
	public OrderUpdates() { this.uniqueOrderId = -1;	}
	public OrderUpdates(int uniqueOrderId) { this.uniqueOrderId = uniqueOrderId;	}
	
	public void setUniqueOrderId(int id) { this.uniqueOrderId = id;	}
	public int getNextOrderId() { return (uniqueOrderId == -1) ? uniqueOrderId: uniqueOrderId++;	}
	
	public void updateOrderStatus(OrderUpdate update) {	allOrders.put(update.orderId, update);	}
	
	public OrderUpdate getOrderStatus(int orderId) {	return allOrders.get(orderId);	}
	public OrderUpdate[] getAllOrderStatus() {	
		return allOrders.values().toArray(new OrderUpdate[allOrders.size()]);
	}
	public int[] getAllOrderIds() {
		int[] orderIds = new int[allOrders.size()];
		int i = 0;
		for(OrderUpdate update: allOrders.values())
		{
			orderIds[i++] = update.orderId;
		}
		return orderIds;
	}
	
}
