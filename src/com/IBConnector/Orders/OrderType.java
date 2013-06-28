package com.IBConnector.Orders;

public enum OrderType {
	MKT("MKT")
	, MKTCLS("MKTCLS")
	, LMT("LMT")
	, LMTCLS("LMTCLS")
//	, PEG_MKT("PEGMKT")
//	, SCALE("SCALE")
//	, STOP("STP")
//	, STOP_LIMIT("STPLMT")
//	, TRAILING("TRAIL")
//	, RELATIVE("REL")
//	, VWAP("VWAP")
//	, TRAILING_LIMIT("TRAILLIMIT")
	;
	
	
	private OrderType(String textValue) {
		this.textValue = textValue;
	}
	
	private final String textValue;
	
	@Override
	public String toString(){
		return textValue;
	}
}
