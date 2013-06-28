package com.IBConnector;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TimeZone;

import com.IBConnector.Account.Account;
import com.IBConnector.Account.AccountDetail;
import com.IBConnector.Data.Bar;
import com.IBConnector.Data.BarHolder;
import com.IBConnector.Orders.OpenOrder;
import com.IBConnector.Orders.OrderAction;
import com.IBConnector.Orders.OrderStatus;
import com.IBConnector.Orders.OrderUpdate;
import com.IBConnector.Orders.OrderType;
import com.IBConnector.Orders.Orders;
import com.IBConnector.Portfolio.Portfolio;
import com.IBConnector.Portfolio.Position;
import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import com.ib.client.EWrapper;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.client.UnderComp;


public class IBConnector implements EWrapper{
	public static final int DEFAULT_CLIENT_ID = 0;
	public static final int DEFAULT_PORT = 7496;
	public static final String DEFAULT_HOST = "localHost";
	EClientSocket client = new EClientSocket(this);
	int uniqueId = 1;
	private HashMap<Integer, BarHolder> data = new HashMap<Integer, BarHolder>();
	private HashMap<Integer, String> symbols = new HashMap<Integer, String>();
	private HashMap<String, Integer> tickerIds = new HashMap<String, Integer>();
	private final Account account;
	private final Portfolio portfolio;
	private Orders orders = null;
	private static int msgDisplayLevel = 0;
	private final long dayStartTimeSec = getDayStartTimeSec();
	private String lastAccountUpdateTime = null;
	
	/*
	 * -----------------------------------------------------
	 *     				Constructors
	 * -----------------------------------------------------
	 */
	public IBConnector() {
		this.account = new Account();	
		this.portfolio = new Portfolio();
		this.orders = new Orders();
	}
	public IBConnector(String accountName) {
		this.account = new Account(accountName);
		this.portfolio = new Portfolio(accountName);
		this.orders = new Orders();
	}
	
	
	
	
	/*
	 * -----------------------------------------------------
	 *     CONNECT, DISCONNECT and isConnected Methods
	 * -----------------------------------------------------
	 */
	public void connect()									{	connect(DEFAULT_CLIENT_ID, DEFAULT_PORT, DEFAULT_HOST);	}
	public void connect(int clientId) 						{	connect(clientId, DEFAULT_PORT, DEFAULT_HOST);			}
	public void connect(int clientId, int port)				{	connect(clientId, port, DEFAULT_HOST);					}
	public void connect(int clientId, int port, String host){
		client.eConnect(host, port, clientId);
		client.reqIds(1);
	}
	public void disconnect()								{ 	client.eDisconnect();			}
	public boolean isConnected() 							{ 	return client.isConnected();	}
	
	
	/*
	 * -----------------------------------------------------
	 *                Subscription Methods
	 * -----------------------------------------------------
	 */
	
	/**
	 * called by user to subscribe to realTimeBars for a symbol
	 * @param symbol
	 */
	public void subscribeToSymbol(String symbol)
	{
		if(tickerIds.get(symbol) == null)
		{
			int tickerId = uniqueId++;
			Contract contract = getStockContract(symbol);
			client.reqRealTimeBars(tickerId, contract, 5, "TRADES", true);
			tickerIds.put(symbol, tickerId);
			symbols.put(tickerId, symbol);
		}
	}
	
	/**
	 * called by user to unsubscribe from realTimeBars for a symbol
	 * @param symbol
	 */
	public void unsubscribeFromSymbol(String symbol)
	{
		Integer tickerId = tickerIds.get(symbol);
		if(tickerId != null)
		{
			client.cancelRealTimeBars(tickerIds.get(symbol));
			data.remove(tickerIds.get(symbol));
			symbols.remove(tickerIds.get(symbol));
			tickerIds.remove(symbol);
		}
	}
	
	/**
	 * called by Interactive brokers to notify connector when a new Bar is received
	 */
	@Override
	public void realtimeBar(int reqId, long time, double open, double high, double low,
			double close, long volume, double wap, int count) {
		BarHolder holder = data.get(reqId);
		if(holder == null) holder = new BarHolder(symbols.get(reqId));
		Bar bar = new Bar(holder.symbol, reqId, time, open, high, low, close, volume, wap, count);
		holder.addBar(bar);
		data.put(reqId, holder);
	}
	
	/**
	 * creates a new IB contract that gets executed on the IB SMART exchange, in USD, and is a Stock
	 * @param equity symbol
	 * @return interactive brokers Contract
	 */
	private Contract getStockContract(String symbol)
	{
		Contract contract = new Contract();
		contract.m_symbol = symbol;
		contract.m_exchange = "SMART";
		contract.m_currency = "USD";
		contract.m_secType = "STK";
		return contract;
	}
	
	/*
	 * -----------------------------------------------------
	 *            Methods to Retrieve Price Data
	 * -----------------------------------------------------
	 */
	public double getOpenPrice(String symbol, int hr, int min, int sec)
	{
		Bar bar = getBar(symbol, hr, min, sec);
		return (bar != null) ? bar.open : -1;
	}
	
	public double getHighPrice(String symbol, int hr, int min, int sec)
	{
		Bar bar = getBar(symbol, hr, min, sec);
		return (bar != null) ? bar.high : -1;
	}
	public double getLowPrice(String symbol, int hr, int min, int sec)
	{
		Bar bar = getBar(symbol, hr, min, sec);
		return (bar != null) ? bar.low : -1;
	}
	public double getClosePrice(String symbol, int hr, int min, int sec)
	{
		Bar bar = getBar(symbol, hr, min, sec);
		return (bar != null) ? bar.close : -1;
	}
	public long getVolume(String symbol, int hr, int min, int sec)
	{
		Bar bar = getBar(symbol, hr, min, sec);
		return (bar != null) ? bar.volume : -1;
	}
	
	private Bar getBar(String symbol, int hr, int min, int sec)
	{
		Integer tickerId = tickerIds.get(symbol);
		if(tickerId != null && data.get(tickerId) != null)
		{
			Bar bar = data.get(tickerId).getBar(dayStartTimeSec + hr * 3600 + min * 60 + sec);
			if(bar != null) return bar;
			else
			{
				System.out.printf("Gap In RealTimeBars - Missing bar for %d:%d:%d %n", hr, min, sec);
				return null;
			}
		}
		else
		{
			System.out.println("Requested data for unsubscribed Ticker - Subscribe first, then request data");
			return null;
		}
	}
	
	
	/*
 	 * -----------------------------------------------------
	 *          Place and Receive Order Updates
	 * -----------------------------------------------------
	 */
	public void printAllOrderUpdates(boolean print) { orders.printAllOrderUpdates(print);	}
	public int placeOrder(Contract contract, Order order) {
		int id = getOrderId();
		if(id != -1) {
			order.m_orderId = id;
			client.placeOrder( id, contract, order);
			return id;
		} else {
			System.out.println("ERROR: Order ID not set -> Order Not sent, check connection to IB and try again");
			return -1;
		}
	}
	
	public int placeMKTOrder(String symbol, OrderAction action, int numShares) {
		Contract contract = getStockContract(symbol);
		Order order = getOrder(OrderType.MKT, action, numShares);
		return placeOrder(contract, order);
	}
	
	public int placeLMTOrder(String symbol, OrderAction action, int numShares, double limitPrice) {
		Contract contract = getStockContract(symbol);
		Order order = getOrder(OrderType.LMT, action, numShares);
		order.m_lmtPrice = limitPrice;
		return placeOrder(contract, order);
	}
	
	private Order getOrder(OrderType orderType, OrderAction action, int numShares) {
		Order order = new Order();
		order.m_orderType = orderType.toString();
		order.m_action = action.toString();
		order.m_totalQuantity = numShares;
		return order;
	}
	
	private int getOrderId() {
		if(orders.getNextOrderId() == -1) {
			client.reqIds(1);
			return -1;
		} else {
			return orders.getNextOrderId();
		}
	}
	
	
	@Override
	public void nextValidId(int orderId) {
		if(orders == null) orders = new Orders(orderId);
		else orders.setUniqueOrderId(orderId);
	}
	
	
	@Override
	public void openOrder(int orderId, Contract contract, Order order, OrderState orderState ) {
		orders.updateOpenOrder(new OpenOrder(orderId, contract, order, orderState));
	}

	@Override
	public void openOrderEnd() {	orders.updateOpenOrderEnd();	}

	@Override
	public void orderStatus(int orderId, String status, int filled, int remaining, 
			double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
		orders.updateOrderStatus(new OrderUpdate(orderId, OrderStatus.fromLabel(status), filled, remaining,
				avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld));
	}
	
	
	/*
 	 * -----------------------------------------------------
	 *          Retrieve Order Status and Open Orders
	 * -----------------------------------------------------
	 */
	public int getNextOrderId() 						{ 	return orders.getNextOrderId();			}
	public OrderUpdate getOrderStatus(int orderId) 		{	return orders.getOrderStatus(orderId);	}
	public OrderUpdate[] getAllOrderStatus() 			{ 	return orders.getAllOrderStatus();		}
	public int[] getAllOrderIds() 						{	return orders.getAllOrderIds();			}
	public int getNumOpenOrders()						{ 	return orders.getNumOpenOrders();		}
	public OpenOrder getOpenOrder(int orderId) 			{	return orders.getOpenOrder(orderId);	}
	public OpenOrder[] getAllOpenOrders() 				{	return orders.getAllOpenOrders();		}
	public int[] getAllOpenOrderIds() 					{ 	return orders.getAllOpenOrderIds();		}
	
	
	/*
 	 * -----------------------------------------------------
	 *            Request and Receive Account Updates
	 * -----------------------------------------------------
	 */
	public String getAccountName() {
		String portfolioAccountName = portfolio.getAccountName();
		String accountAccountName = account.getAccountName();
		if(portfolio.getAccountName().equalsIgnoreCase(account.getAccountName())) return portfolio.getAccountName();
		else return new StringBuilder("AccountNames Not Equal: Portfolio accountName: ").append(portfolioAccountName).append(", Account accountName: ").append(accountAccountName).toString();
	}
	public void reqAccountUpdates() 	{	client.reqAccountUpdates(true, "");	}
	public void stopAccountUpdates() 	{	client.reqAccountUpdates(false, "");	}
	public void printAllUpdates(boolean print) {
		account.printUpdates(print);
		portfolio.printUpdates(print);
	}
	public String reqlastUpdateTime()	{	
		if(lastAccountUpdateTime != null) return lastAccountUpdateTime;	
		else return "Request Account Updates before calling: No account updates have been received";
	}
	

	@Override
	public void updateAccountTime(String timeStamp)	{	lastAccountUpdateTime = timeStamp;	}

	@Override
	public void updateAccountValue(String key, String value, String currency, String accountName) {
		account.updateAccountDetail(new AccountDetail(key, value, currency, accountName));
		
	}
	
	@Override
	public void updatePortfolio(Contract contract, int positionSize, double marketPrice, double marketValue,
			double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
		portfolio.updatePosition(new Position(contract, positionSize, marketPrice, marketValue,
				averageCost, unrealizedPNL, realizedPNL, accountName));
		
	}
	
	
	
	/*
 	 * -----------------------------------------------------
	 *           	 Retrieve Account Info
	 * -----------------------------------------------------
	 */
	public AccountDetail[] getAllAccountInfo() 		{ 	return account.getAllAccountInfo();	}
	public AccountDetail getAccountInfo(String key) { 	return account.getAccountInfo(key);	}
	public String getValue(String key) 				{ 	return account.getValue(key); 		}
	public String getCurrency(String key) 			{ 	return account.getCurrency(key); 	}
	public void printAccountInfo(String key) 		{ 	account.printAccountInfo(key);		}
	public void printAllAccountInfo() 				{	account.printAllAccountInfo();		}

	/*
 	 * -----------------------------------------------------
	 *         	 Retrieve Portfolio Position Info
	 * -----------------------------------------------------
	 */
	public Position[] getAllPositions() 			{ 	return portfolio.getAllPositions();		}
	public Position getPosition(String symbol) 		{ 	return portfolio.getPosition(symbol);	}
	public void printPosition(String symbol) 		{ 	portfolio.printPosition(symbol); 		}
	public void printAllPositions() 				{	portfolio.printAllPositions();			}
	public String[] getAllSymbols() 				{ 	return portfolio.getAllSymbols();		}
	
	public Contract getContract(String symbol) 	{ return portfolio.getContract(symbol);			}
	public int getPositionSize(String symbol)	{ return portfolio.getPositionSize(symbol);		}
	public double getMarketPrice(String symbol)	{ return portfolio.getMarketPrice(symbol);		}
	public double getMarketValue(String symbol) { return portfolio.getMarketValue(symbol);		}
	public double getAverageCost(String symbol) { return portfolio.getAverageCost(symbol);		}
	public double getUnrealizedPNL(String symbol) { return portfolio.getUnrealizedPNL(symbol);	}
	public double getRealizedPNL(String symbol) { return portfolio.getRealizedPNL(symbol);		}
	

	/*
	 * -----------------------------------------------------
	 *            Message Display preferences
	 * -----------------------------------------------------
	 */
	public int getMsgDisplayLevel() 					{	return IBConnector.msgDisplayLevel;				}
	public void setMsgDisplayLevel(int msgDisplayLevel) { 	IBConnector.msgDisplayLevel = msgDisplayLevel;	}
	
	@Override
	public void connectionClosed() {
		if(msgDisplayLevel < 1)	System.out.println(" [API.connectionClosed] Closed connection with TWS");
	}

	@Override
	public void error(Exception e) {
		if(msgDisplayLevel < 2)
			System.out.println((new StringBuilder(" [API.msg3] ")).append("Exception").append(" {").append(e.getMessage()).append(", ").append(e.getLocalizedMessage()).append("}").toString());
		
	}

	@Override
	public void error(String str) {
		if(msgDisplayLevel < 2)
			System.out.println((new StringBuilder(" [API.msg1] ")).append(str).toString());
	}

	@Override
	public void error(int data1, int data2, String str) {
		if(msgDisplayLevel < 1 || msgDisplayLevel == 1 && !str.contains("Market data farm connection is OK:"))
			System.out.println((new StringBuilder(" [API.msg2] ")).append(str).append(" {").append(data1).append(", ").append(data2).append("}").toString());
	}

	
	/*
	 * -----------------------------------------------------
	 *            Helper and Debug methods
	 * -----------------------------------------------------
	 */
	/**
	 * retrieves the number of seconds since Jan 1st, 1970 until 12:00AM today in the New York time zone
	 * @return numberOfSeconds
	 */
	public long getDayStartTimeSec() {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH);
	    int day = cal.get(Calendar.DATE);
	    cal.set(year, month, day, 0, 0, 0);
	    return cal.getTimeInMillis() / 1000;
	}
	
	
	/**
	 * example debug code to connect, subscribe to realtime bars for GOOG, and print data, then disconnect;
	 * @param does not take input
	 */
	public static void main(String[] args)
	{
		IBConnector test = new IBConnector();
		test.connect();
		test.waitForUserInput();
		
		// subscribe and print recorded data example
//		debugSubscribeAndPrintData(test);

		// request portfolio and account updates and print out results
//		accountAndPortfolioDebug(test);
		
		// place order and listen to updates
		debugOrders(test);
		
		test.waitForUserInput();
		test.disconnect();
		test.waitForUserInput();
	}
	
	private static void debugSubscribeAndPrintData(IBConnector test) {
		
		test.subscribeToSymbol("GOOG");
		test.waitForUserInput();
		test.printData();
		test.waitForUserInput();
	}
	
	private static void debugAccountAndPortfolio(IBConnector test) {
		test.printAllUpdates(true);
		test.reqAccountUpdates();
		test.waitForUserInput();
	}
	
	private static void debugOrders(IBConnector test) {
		test.printAllOrderUpdates(true);
		test.placeMKTOrder("GOOG", OrderAction.BUY, 10);
		test.waitForUserInput();
	}
	
	/**
	 * prints all recorded bars for all data values that were subscribed
	 */
	public void printData()	{	for(BarHolder barHolder: data.values()) barHolder.printAllBars();	}
	
	/**
	 * simple scanner that allows the user to control the pace of the program
	 */
	public void waitForUserInput()	{	if((new Scanner(System.in)).nextLine() != null) System.out.print("");	}
	
	
	
	/*
	 *  ---------------------------------------------------------
	 *     ALL OTHER METHOD SIGNATURES BELOW ARE PLACEHOLDERS
	 *  ---------------------------------------------------------
	 */
	
	@Override
	public void accountDownloadEnd(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bondContractDetails(int arg0, ContractDetails arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commissionReport(CommissionReport arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contractDetails(int arg0, ContractDetails arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contractDetailsEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void currentTime(long arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deltaNeutralValidation(int arg0, UnderComp arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execDetails(int arg0, Contract arg1, Execution arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execDetailsEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fundamentalData(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void managedAccounts(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void marketDataType(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void receiveFA(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scannerData(int arg0, int arg1, ContractDetails arg2,
			String arg3, String arg4, String arg5, String arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scannerDataEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scannerParameters(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickEFP(int arg0, int arg1, double arg2, String arg3,
			double arg4, int arg5, String arg6, double arg7, double arg8) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickGeneric(int arg0, int arg1, double arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickOptionComputation(int arg0, int arg1, double arg2,
			double arg3, double arg4, double arg5, double arg6, double arg7,
			double arg8, double arg9) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickPrice(int arg0, int arg1, double arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickSize(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickSnapshotEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickString(int arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMktDepth(int arg0, int arg1, int arg2, int arg3,
			double arg4, int arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMktDepthL2(int arg0, int arg1, String arg2, int arg3,
			int arg4, double arg5, int arg6) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateNewsBulletin(int arg0, int arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historicalData(int arg0, String arg1, double arg2, double arg3,
			double arg4, double arg5, int arg6, int arg7, double arg8,
			boolean arg9) {
		// TODO Auto-generated method stub
		
	}


}
