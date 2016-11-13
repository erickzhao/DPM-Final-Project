package team6.finalproject;

import lejos.robotics.SampleProvider;

/**
 * Class that periodically polls a color sensor's <code>SampleProvider</code> to fetch RGB
 * values.
 * <o>
 * Uses the color sensor's <code>RGBMode</code> readings to determine the color value of the
 * surface to be examined. Each reading is an array of 3 <code>float</code> values, which are
 * the Red, Green, and Blue intensities, respectively. These values are normalized from 0 to 1,
 * where 0 is no color detected and 1 is the maximum color value.
 * @author Erick Zhao
 * @version 0.11
 */
public class ColorPoller extends PausableTimerListener {
	private SampleProvider colorSensor;
	private float[] colorData;
	private static boolean isObject = false;
	private static boolean isBlock = false;
	
	private static final float MIN_OBJECT_READING_VALUE= (float)0.015;
	/**
	 * Constructor for Color Poller.
	 * @param colorSensor	the <code>SampleProvider</code> that fetches the readings
	 * @param colorData		the <code>float</code> array in which the <code>SampleProvider</code> stores its data
	 */
	public ColorPoller(SampleProvider colorSensor, float[] colorData) {
		this.colorSensor = colorSensor;
		this.colorData = colorData;
	}
	
	/**
	 * Refreshes the color sensor data every <code>Timer</code> loop.
	 */
	@Override
	public void timedOut() {
		colorSensor.fetchSample(colorData, 0);
		readSensor();
	}
	
	/**
	 * Reads the sensor and determines whether we're facing an object or block.
	 * With styrofoam blocks, the green reading is approximately double the value of the red reading
	 * and vice-versa for the wooden obstacles. Therefore, the green to red ratio in the RGB reading
	 * determines whether or not the object detected is a block or an obstacle. There is also a threshold
	 * in color value that determines whether or not the object is close enough to be read properly.
	 */
	private void readSensor() {
		float redReading = colorData[0];
		float greenReading = colorData[1];
		
		isObject = (redReading >MIN_OBJECT_READING_VALUE || greenReading > MIN_OBJECT_READING_VALUE) ? true:false;
		isBlock = (greenReading > redReading && isObject) ? true:false;
	}
	
	/**
	 * Getter for color data.
	 * @return 		a <code>float</code> array of RGB readings.
	 */
	public float[] getReadings() {
		return this.colorData;
	}
	
	/**
	 * Determines whether or not an object is detected by the color sensor.
	 * @return 		a <code>boolean</code> indicating whether nor not we have an object detected
	 */
	public static boolean isObject() {
		return isObject;
	}
	
	/**
	 * Determines whether or not a styrofoam block is detected by the color sensor.
	 * @return 		a <code>boolean</code> indicating whether or not we have a styrofoam block detected.
	 */
	public static boolean isBlock() {
		return isBlock;
	}
}
