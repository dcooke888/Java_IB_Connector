package com.IBConnector.Orders;


public class Orders {

	private final OrderUpdates orderUpdates;
	private final OpenOrders openOrders = new OpenOrders();
	private boolean printUpdates = false;
	
	public Orders() { this.orderUpdates = new OrderUpdates();	}
	public Orders(int uniqueOrderId) { this.orderUpdates = new OrderUpdates(uniqueOrderId);	}

	/**
	 * for debugging purposes prints all order updates to console, default is printing off
	 * @param print
	 */
	public void printAllOrderUpdates(boolean print) { printUpdates = print;	}
	
	// orderStatus methods
	public void setUniqueOrderId(int id) 				{ 	orderUpdates.setUniqueOrderId(id);			}
	public int getNextOrderId() 						{ 	return orderUpdates.getNextOrderId();		}
	public void updateOrderStatus(OrderUpdate update) 	{
		if(printUpdates) update.print();
		orderUpdates.updateOrderStatus(update);		
	}
	public OrderUpdate getOrderStatus(int orderId) 		{	return orderUpdates.getOrderStatus(orderId);}
	public OrderUpdate[] getAllOrderStatus() 			{ 	return orderUpdates.getAllOrderStatus();	}
	public int[] getAllOrderIds() 						{	return orderUpdates.getAllOrderIds();		}
	
	
	
	// openOrder methods
	public void updateOpenOrder(OpenOrder update) 		{ 	
		if(printUpdates) update.print();
		openOrders.updateOpenOrder(update);			
	}
	public void updateOpenOrderEnd() 					{ 	openOrders.updateOpenOrderEnd();			}
	public int getNumOpenOrders()						{ 	return openOrders.getNumOpenOrders();		}
	public OpenOrder getOpenOrder(int orderId) 			{	return openOrders.getOpenOrder(orderId);	}
	public OpenOrder[] getAllOpenOrders() 				{	return openOrders.getAllOpenOrders();		}
	public int[] getAllOpenOrderIds() 					{ 	return openOrders.getAllOpenOrderIds();		}
	
	
	

	
}
