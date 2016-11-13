package team6.finalproject;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * Class that periodically polls a color sensor's <code>SampleProvider</code> to fetch brightness
 * values.
 * <o>
 * Uses the color sensor's <code>RedMode</code> readings to determine the
 * brightness of a surface. These readings are normalized from 0 to 1, where 0 is black and 1 is white.
 * The class multiplies these readings by 100 to get more readable <code>int</code> values.
 * Can also interpret sudden drops in light readings as passing over a black line in the grid.
 * @author Myriam Ayad
 * @version 0.1
 */
public class LightPoller extends PausableTimerListener{
	
	private SampleProvider light;
	private float[] lightData;
	private static float lightValue;
	private static float currentLight;
	private static float previousLight = -1;
	private static double significantPercentThreshold = 20;
	
	/**
	 * Constructor for Light Poller.
	 * @param light		the <code>SampleProvider</code> that fetches the readings
	 * @param lightData	the <code>float</code> array in which the <code>SampleProvider</code> stores its data
	 */
	public LightPoller(SampleProvider light, float[] lightData) {
		this.light = light;
		this.lightData = lightData;
	}
	
	// start the timer
	public void timedOut() {
		light.fetchSample(lightData,0);						// acquire data
		lightValue=lightData[0]*100;						// extract from buffer, cast to int
	}
	
	/**
	 * Returns a <code>boolean</code> indicating if we are currently crossing a black line by comparing
	 * the previous light sensor reading to the current one. If there is a very significant drop in
	 * the readings, then we know we have passed over a black line.
	 * @return	whether or not we are crossing a black grid line or not
	 */
	public static boolean blackLine(){
		currentLight=lightValue;	
		if(100*Math.abs(currentLight - previousLight)/previousLight > significantPercentThreshold){
			if (currentLight < previousLight){
				Sound.beep();
				return true;
			}
		}
		previousLight = currentLight;
		return false;
	}
	
	/**
	 * Fetches the current light sensor reading.
	 * @return		a <code>float</code> representing
	 */
	public static float getReading(){
		return lightValue;
	}
}
