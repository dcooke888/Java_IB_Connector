package com.IBConnector.Data;

public class SnapShotCreator implements Runnable{

	private final TOBUpdater TOB;
	private final SnapShotHolder snapShots;
	
	public SnapShotCreator(TOBUpdater TOB, SnapShotHolder snapShots) {
		this.TOB = TOB;
		this.snapShots = snapShots;
	}
	@Override
	public void run() {
		String[] symbols = TOB.getAllKeys();
		for(String symbol: symbols) {
			SnapShot snapShot = TOB.getSnapShot(symbol);
			if(snapShot != null) snapShots.addSnapShot(snapShot);
		}
	}
}
