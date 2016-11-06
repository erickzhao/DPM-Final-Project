package team6.finalproject;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class LCDInfo extends PausableTimerListener{
	private Odometer odo;
	private TextLCD LCD = LocalEV3.get().getTextLCD();
	
	// arrays for displaying data
	private double [] pos;
	private double distance;
	private double lightReading;
	
	public LCDInfo(Odometer odo) {
		this.odo = odo;
		
		// initialise the arrays for displaying data
		pos = new double [3];
	}
	
	public void timedOut() {
		distance=UltrasonicPoller.getDistance();
		lightReading=LightPoller.getReading();
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawString("Distance: ",0,3);
		LCD.drawString("Light Sensor: ",0,4);
		LCD.drawInt((int)(pos[0]), 3, 0);
		LCD.drawInt((int)(pos[1]), 3, 1);
		LCD.drawInt((int)pos[2], 3, 2);
		LCD.drawInt((int)distance,11,3);
		LCD.drawInt((int)lightReading,15,4);
	}
}
