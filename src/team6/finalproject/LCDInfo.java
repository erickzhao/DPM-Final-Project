package team6.finalproject;

import team6.test.TestLocalize;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

/**
 * Class that manages the LCD display for the leJOS EV3 Brick. Displays odometer and sensor readings.
 * @author Andrei Ungur
 * @version 0.11
 */

public class LCDInfo extends PausableTimerListener{
	private Odometer odo;
	private TextLCD LCD = LocalEV3.get().getTextLCD();
	
	// arrays for displaying data
	private double [] pos;
	private double distance;
	private double lightReading;
	private UltrasonicPoller uspoll;
	/**
	 * Constructor for LCDInfo.
	 * @param odo	the <code>Odometer</code> object whose readings are displayed on the screen
	 */
	public LCDInfo(Odometer odo,UltrasonicPoller uspoll) {
		this.odo = odo;
		this.uspoll=uspoll;
		
		// initialise the arrays for displaying data
		pos = new double [3];
	}
	
	/**
	 * Refreshes the sensor and odometer data on every <code>Timer</code> loop.
	 */
	
	public void timedOut() {
		distance=uspoll.getDistance();
		lightReading=LightPoller.getReading();
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawString("Distance: ",0,3);
		LCD.drawString("Light Sensor: ",0,4);
		LCD.drawString("Object: ",0,5);
		LCD.drawString("Block: ",0,6);
		LCD.drawInt((int)(pos[0]), 3, 0);
		LCD.drawInt((int)(pos[1]), 3, 1);
		LCD.drawInt((int)pos[2], 3, 2);
		LCD.drawInt((int)distance,11,3);
		LCD.drawInt((int)lightReading,15,4);
		LCD.drawString(String.valueOf(ColorPoller.isObject()), 8, 5);
		LCD.drawString(String.valueOf(ColorPoller.isBlock()), 7, 6);
	}
}
