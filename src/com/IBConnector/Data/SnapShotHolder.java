package com.IBConnector.Data;

import java.util.ArrayDeque;
import java.util.HashMap;

public class SnapShotHolder {
	
	private HashMap<String, ArrayDeque<SnapShot>> allSnapShots = new HashMap<String, ArrayDeque<SnapShot>>();
	
	public void addSnapShot(SnapShot snapShot) {
		ArrayDeque<SnapShot> snapShots = allSnapShots.get(snapShot.symbol);
		if(snapShots == null) snapShots = new ArrayDeque<SnapShot>();
		snapShots.addLast(snapShot);
		allSnapShots.put(snapShot.symbol, snapShots);
	}
	
	public SnapShot[] getAllSymbolSnapShots(String symbol) {
		ArrayDeque<SnapShot> snapShots = allSnapShots.get(symbol);
		if(snapShots != null) return snapShots.toArray(new SnapShot[snapShots.size()]);
		return null;
	}
	
	public SnapShot getRecentSnapShot(String symbol) {
		return allSnapShots.get(symbol).getLast();
	}
}
