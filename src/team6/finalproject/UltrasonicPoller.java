package team6.finalproject;

import lejos.robotics.SampleProvider;

//UltrasonicPoller gets data from the US and can output it through
//getDistance. It runs every 50 seconds as it implements TimerListener.

public class UltrasonicPoller extends PausableTimerListener {
	
	private SampleProvider us;
	private float[] usData;
	private static float distance;
	
	public UltrasonicPoller(SampleProvider us, float[] usData) {
		this.us = us;
		this.usData = usData;
	}
	
	// start the timer
	public void timedOut() {
		us.fetchSample(usData,0);							// acquire data
		distance=usData[0]*100;								// extract from buffer, cast to int
	}
	
	//"distance" is sometimes accessed in a static way from outside this class
	public static float getDistance(){
		return distance;
	}
}
