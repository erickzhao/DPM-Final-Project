package team6.finalproject;

import lejos.robotics.SampleProvider;

/**
 * A {@link lejos.utility.TimerListener}-based ultrasonic sensor reading fetcher.
 * <o>
 * Gets a reading from 0 to 2.55 metres from an ultrasonic sensor and returns the
 * reading as centimetres.
 * @author Andrei Ungur
 * @version 0.1
 */

public class UltrasonicPoller extends PausableTimerListener {
	
	private SampleProvider us;
	private float[] usData;
	private static float distance;
	
	/**
	 * Constructor for the Ultrasonic Poller.
	 * @param us		the <code>SampleProvider</code> that fetches the readings
	 * @param usData	the <code>float</code> array in which the <code>SampleProvider</code> stores its data
	 */
	public UltrasonicPoller(SampleProvider us, float[] usData) {
		this.us = us;
		this.usData = usData;
	}
	
	/**
	 * Fetches sampler data for every {@link lejos.utility.Timer} loop.
	 * Multiplies the ultrasonic sensor's readings by 100 to display more readable <code>int</code> values.
	 */
	public void timedOut() {
		us.fetchSample(usData,0);
		distance=usData[0]*100;
	}
	
	/**
	 * Gets the distance value fetched by the <code>SampleProvider</code>.
	 * @return	the float value of the distance read by the ultrasonic sensor
	 */
	public static float getDistance(){
		return distance;
	}
}
