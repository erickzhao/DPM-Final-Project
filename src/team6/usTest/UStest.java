package usTest;


import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class UStest {

	static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final Port usPort = LocalEV3.get().getPort("S3");
	
	private static final int WHEEL_SPEED = 150;
	
	
	public static void main(String[] args) {
		
		int buttonChoice;
		final TextLCD t = LocalEV3.get().getTextLCD();
		
		// Ultrasonic setting
		@SuppressWarnings("resource")    
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance");
		float[] usData = new float[usValue.sampleSize()];
		
		DataLogger dl = new DataLogger(usValue, usData);
		Turn720 ttt = new Turn720(WHEEL_SPEED, leftMotor, rightMotor);
		
		do {
			t.clear();
			
			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString(" turn  | stati--", 0, 2);
			t.drawString(" around| --onary", 0, 3);
			
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		
		
		if (buttonChoice == Button.ID_LEFT) {
			
			t.clear();
			t.drawString("Running!", 0, 0);
			
			ttt.start();
			dl.start();
			
			
			
			while(Button.waitForAnyPress() != Button.ID_ESCAPE);
			System.exit(0);
		} else{
			t.clear();
			t.drawString("Running!", 0, 0);
			
			dl.start();
			
			while(Button.waitForAnyPress() != Button.ID_ESCAPE);
			System.exit(0);
		}
	
	}
}
