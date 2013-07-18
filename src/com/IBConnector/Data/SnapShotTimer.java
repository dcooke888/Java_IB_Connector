package com.IBConnector.Data;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SnapShotTimer implements Runnable{
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private final TOBUpdater TOB;
	private final SnapShotHolder holder;
	private final int secsPerSnap;
	private ScheduledFuture<?> beeperHandle;
	
	public SnapShotTimer(int secsPerSnap, TOBUpdater TOB, SnapShotHolder holder) {
		this.TOB = TOB;
		this.secsPerSnap = secsPerSnap;
		this.holder = holder;
	}

	@Override
	public void run() {
		if(beeperHandle == null || beeperHandle.isDone()){
			this.beeperHandle = scheduler.scheduleAtFixedRate(new SnapShotCreator(TOB, holder),
					0, secsPerSnap, TimeUnit.SECONDS);
		}
	}
	
	public void stopSnaps() {
		if(beeperHandle != null){
			beeperHandle.cancel(true);
		}
	}
}
