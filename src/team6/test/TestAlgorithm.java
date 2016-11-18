package team6.test;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import team6.finalproject.ColorPoller;
import team6.finalproject.LCDInfo;
import team6.finalproject.LightPoller;
import team6.finalproject.Navigation;
import team6.finalproject.ObjectAvoidance;
import team6.finalproject.ObjectSearch;
import team6.finalproject.Odometer;
import team6.finalproject.USLocalizer;
import team6.finalproject.LightLocalizer;
import team6.finalproject.UltrasonicPoller;

public class TestAlgorithm {

	/*
	 * Resources :
	 * 
	 * Motors
	 * > Port A:	Right Wheel
	 * > Port B:	Claw
	 * > Port C:	Ultrasonic sensors
	 * > Port D:	Left Wheel
	 * Sensors
	 * > Port S1:	Light (RedMode)
	 * > Port S2:	Ultrasonic (Top)
	 * > Port S3:	Color (RGB)
	 * > Port S4:	Ultrasonic (Bottom)
	 */
	
	 private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	 //private static final EV3LargeRegulatedMotor lightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	 //private static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	 private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	 private static final EV3MediumRegulatedMotor usMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));
	 private static final EV3LargeRegulatedMotor clawMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	 public static UltrasonicPoller uspoll;
	 
	 private static final Port usPort = LocalEV3.get().getPort("S4");  
	 private static final Port colorPort = LocalEV3.get().getPort("S3"); 
		
	 //constants
	 public static final double WHEEL_RADIUS = 2.15; //needs to be changed for robots physical configs
	 public static final double TRACK = 15.6; //needs to be changed for robots physical configs
	
	public static void main(String[] args) {
		//******************Basic set-up of sensor and odometer***********************
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true, WHEEL_RADIUS, TRACK);
		
		@SuppressWarnings("resource")
		EV3UltrasonicSensor sensor = new EV3UltrasonicSensor(usPort);
		SensorModes usSensor = sensor;
		SampleProvider usValue = usSensor.getMode("Distance");
		float[] usData = new float[usValue.sampleSize()];
		
		@SuppressWarnings("resource")
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("RGB");
		float[] colorData = new float[colorValue.sampleSize()];
		ColorPoller colorPoll = new ColorPoller(colorValue,colorData);
		
		uspoll = new UltrasonicPoller(usValue, usData,sensor);
		
		LCDInfo lcd = new LCDInfo(odo,uspoll,null); 
		USLocalizer usloc = new USLocalizer(odo,uspoll);
		Navigation nav = new Navigation(odo);
		ObjectAvoidance oa = new ObjectAvoidance(odo, usMotor, uspoll);
		
		odo.start();
		uspoll.start();
		colorPoll.start();
		lcd.start();
		
		ObjectSearch search = new ObjectSearch(odo, nav, uspoll,oa,clawMotor);
		nav.turnTo(0,true);
		search.doSearch();
		
	}

}
