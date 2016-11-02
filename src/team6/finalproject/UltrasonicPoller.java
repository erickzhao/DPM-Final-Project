package team6.finalproject;

import lejos.robotics.SampleProvider;
import lejos.utility.TimerListener;
import lejos.utility.Timer;

//UltrasonicPoller gets data from the US and can output it through
//getDistance. It runs every 50 seconds as it implements TimerListener.

public class UltrasonicPoller implements TimerListener{
	private Timer timer;
	private final int refreshRate = 50;
	
	private SampleProvider us;
	private float[] usData;
	private static float distance;
	
	public UltrasonicPoller(SampleProvider us, float[] usData) {
		this.us = us;
		this.usData = usData;
		this.timer = new Timer(refreshRate, this);
		timer.start();
	}
	
	// start the timer
	Thread navigate = null;
	public void timedOut() {
		us.fetchSample(usData,0);							// acquire data
		distance=usData[0]*100;								// extract from buffer, cast to int
		try { Thread.sleep(50); } catch(Exception e){}		// Timed sampling
	}
	
	//"distance" is sometimes accessed in a static way from outside this class
	public static float getDistance(){
		return distance;
	}
}
