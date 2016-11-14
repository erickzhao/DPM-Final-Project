package team6.test;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import team6.finalproject.LCDInfo;
import team6.finalproject.LightPoller;
import team6.finalproject.Navigation;
import team6.finalproject.ObjectSearch;
import team6.finalproject.Odometer;
import team6.finalproject.USLocalizer;
import team6.finalproject.LightLocalizer;
import team6.finalproject.UltrasonicPoller;

public class TestSweep {

	/*
	 * Resources :
	 * Motors
	 * > Port A:	Right
	 * > Port B:	Light
	 * > Port C:	Arm
	 * > Port D:	Left
	 * Sensors
	 * > Port S1:	UltraSonic
	 * > Port S2:	None
	 * > Port S3:	None
	 * > Port S4:	Color
	 */
	
	 private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	 //private static final EV3LargeRegulatedMotor lightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	 //private static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	 private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	 public static UltrasonicPoller uspoll;
	 private static final Port usPort = LocalEV3.get().getPort("S4");  

	 //constants
	 public static final double WHEEL_RADIUS = 2.15; //needs to be changed for robots physical configs
	 public static final double TRACK = 15.6; //needs to be changed for robots physical configs
	
	public static void main(String[] args) {
		//******************Basic set-up of sensor and odometer***********************
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true, WHEEL_RADIUS, TRACK);
		
		@SuppressWarnings("resource")
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance");
		float[] usData = new float[usValue.sampleSize()];
		
		uspoll = new UltrasonicPoller(usValue, usData);
		
		LCDInfo lcd = new LCDInfo(odo,uspoll); 
		USLocalizer usloc = new USLocalizer(odo,uspoll);
		Navigation nav = new Navigation(odo);
		
		odo.start();
		uspoll.start();
		lcd.start();
		
		ObjectSearch search = new ObjectSearch(odo, nav, uspoll);
		nav.turnTo(0,true);
		search.sweep();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
	}

}
