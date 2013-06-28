package com.IBConnector.Orders;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

public class OpenOrder {

	public int orderId;
	public Contract contract;
	public Order order;
	public OrderState orderState;
	
	public OpenOrder(int orderId, Contract contract, Order order, OrderState orderState ){
		this.orderId = orderId;
		this.contract = contract;
		this.order = order;
		this.orderState = orderState;
	}
	
	public void print() {
		System.out.printf("OPEN ORDER UPDATE: Order Id: %d, Symbol: %s, OrderType: %s, OrderAction: %s, OrderSize: %s %n",
				orderId, contract.m_symbol, order.m_orderType, order.m_action, order.m_totalQuantity);
	}
}
