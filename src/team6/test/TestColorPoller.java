package team6.test;

import team6.finalproject.ColorPoller;
import team6.finalproject.LCDInfo;
import team6.finalproject.Odometer;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class TestColorPoller {
	
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	 
	private static final Port colorPort = LocalEV3.get().getPort("S4"); 
	 
	//constants
	public static final double WHEEL_RADIUS = 2.15; //needs to be changed for robots physical configs
	public static final double TRACK = 15.2; //needs to be changed for robots physical configs
	
	public static void main(String[] args) {
		
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true, WHEEL_RADIUS, TRACK);

		@SuppressWarnings("resource")
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("RGB");
		float[] colorData = new float[colorValue.sampleSize()];
		ColorPoller colorPoll = new ColorPoller(colorValue,colorData);
		
		LCDInfo lcd = new LCDInfo(odo);
		
		odo.start();
		lcd.start();
		colorPoll.start();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}

}
