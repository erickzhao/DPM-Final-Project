package team6.finalproject;

import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

/**
 * A {@link lejos.utility.TimerListener}-based ultrasonic sensor reading fetcher.
 * <o>
 * Gets a reading from 0 to 2.55 metres from an ultrasonic sensor and returns the
 * reading as centimetres.
 * @author Andrei Ungur
 * @version 1.0
 */

public class UltrasonicPoller extends PausableTimerListener {
	
	private SampleProvider us;
	private float[] usData;
	private static float distance;
	private EV3UltrasonicSensor sensor;
	
	/**
	 * Constructor for Ultrasonic Poller.
	 * @param us		the <code>SampleProvider</code> that fetches the readings
	 * @param usData	the <code>float</code> array in which the <code>SampleProvider</code> stores its data
	 */
	public UltrasonicPoller(SampleProvider us, float[] usData,EV3UltrasonicSensor inputSensor) {
		this.sensor = inputSensor;
		this.us = us;
		this.usData = usData;
	}
	
	/**
	 * Fetches sampler data for every <code>Timer</code> loop.
	 * Multiplies the ultrasonic sensor's readings by 100 to display more readable <code>int</code> values.
	 */
	public void timedOut() {
		if (sensor.isEnabled()){
			us.fetchSample(usData,0);
			distance=usData[0]*100;
		}
	}
	
	/**
	 * Gets the distance value fetched by the <code>SampleProvider</code>.
	 * @return		the <code>float</code> value of the distance read by the ultrasonic sensor
	 */
	public float getDistance(){
		return distance;
	}
	public void enable(boolean on){
		if (on && !this.sensor.isEnabled()){
			Sound.beepSequenceUp();
			this.sensor.enable();
		} else if (!on && this.sensor.isEnabled()) {
			Sound.beepSequence();
			this.sensor.disable();
		}
	}
}
