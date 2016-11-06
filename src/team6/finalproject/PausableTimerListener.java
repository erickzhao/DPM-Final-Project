package team6.finalproject;

import lejos.utility.TimerListener;
import lejos.utility.Timer;

public abstract class PausableTimerListener implements TimerListener {

	private Timer timer;
	private static final int REFRESH_RATE = 50;
	
	public PausableTimerListener() {
		this.timer = new Timer(REFRESH_RATE, this);
	}
	
	public void stop() {
		if (this.timer != null)
			this.timer.stop();
	}
	public void start() {
		if (this.timer != null)
			this.timer.start();
	}
}
