package com.IBConnector.Orders;

import java.util.HashMap;

public class OpenOrders {
	private HashMap<Integer, OpenOrder> openOrders = new HashMap<Integer, OpenOrder>();
	private volatile boolean updateComplete = true;
	
	public void updateOpenOrder(OpenOrder update) {
		if(updateComplete) {
			openOrders.clear();
			updateComplete = false;
		}
		openOrders.put(update.orderId, update);	
	}
	
	public void updateOpenOrderEnd() {	updateComplete = true;	}
	
	public int getNumOpenOrders()				{ 	return openOrders.size();		}
	public OpenOrder getOpenOrder(int orderId) 	{	return openOrders.get(orderId);	}
	public OpenOrder[] getAllOpenOrders() 		{	
		return openOrders.values().toArray(new OpenOrder[openOrders.size()]);	
	}

	public int[] getAllOpenOrderIds() {
		if(openOrders.size() > 0)
		{
			int[] orderIds = new int[openOrders.size()];
			int i = 0;
			for(OpenOrder order: openOrders.values())
			{
				orderIds[i++] = order.orderId;
			}
			return orderIds;
		} else {
			return null;
		}
	}
}
