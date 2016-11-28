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
import team6.finalproject.CountdownTimer;
import team6.finalproject.LCDInfo;
import team6.finalproject.LightPoller;
import team6.finalproject.ObjectAvoidance;
import team6.finalproject.Odometer;
import team6.finalproject.OdometryCorrection;
import team6.finalproject.USLocalizer;
import team6.finalproject.LightLocalizer;
import team6.finalproject.UltrasonicPoller;

public class TestLocalize {

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
	 //private static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	 private static final EV3MediumRegulatedMotor usMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));
	 private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	 public static UltrasonicPoller uspoll;
	 private static final Port usPort = LocalEV3.get().getPort("S2");  
	 private static final Port lightPort = LocalEV3.get().getPort("S1"); 

	 //constants
	 public static final double WHEEL_RADIUS = 2.15; //needs to be changed for robots physical configs
	 public static final double TRACK = 15.6; //needs to be changed for robots physical configs
	
	public static void main(String[] args) {
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true, WHEEL_RADIUS, TRACK);
		CountdownTimer countdown = new CountdownTimer();
		
		//Bottom US : Object recognition and localization
		@SuppressWarnings("resource")
		EV3UltrasonicSensor bottomSensor = new EV3UltrasonicSensor(usBottomPort);
		SensorModes usSensor = bottomSensor;
		SampleProvider usValue = usSensor.getMode("Distance");
		float[] usData = new float[usValue.sampleSize()];
		
		//Upper US : Obstacle avoidance
		@SuppressWarnings("resource")
		EV3UltrasonicSensor topSensor = new EV3UltrasonicSensor(usTopPort);
		SensorModes usSensorTop = topSensor;
		SampleProvider usValueTop = usSensorTop.getMode("Distance");
		float[] usDataTop = new float[usValueTop.sampleSize()];
		
		//Light sensor for localization
		@SuppressWarnings("resource")
		SensorModes lightSensor = new EV3ColorSensor(lightPort);
		SampleProvider lightValue = lightSensor.getMode("Red");
		float[] lightData = new float[lightValue.sampleSize()];
		
		
		//Initialize US Pollers
		uspoll = new UltrasonicPoller(usValue, usData,bottomSensor);
		
		//Initialize LIGHT Pollers
		LightPoller lightpoll = new LightPoller(lightValue,lightData);
		ColorPoller colorpoll = new ColorPoller(colorValue,colorData);
		
		//Initialize LCD Display, US & LIGHT Localizers and ODO Correction
		LCDInfo lcd = new LCDInfo(odo,uspoll,topus); 
		USLocalizer usloc = new USLocalizer(odo,topus);
		LightLocalizer lightloc = new LightLocalizer(odo,LStoWB);
		OdometryCorrection odoCorrection = new OdometryCorrection(odo); 
		
		//Initialize Obstacle Avoidance
		ObjectAvoidance oa = new ObjectAvoidance(odo, usMotor, topus);
		
		// ----------------------------------------------------------------
		/* BASIC SETUP : Start the following threads:
		 *  1. Odometer + Odometry correction;
		 *  2. TOP and BOTTOM US Pollers
		 *  3. LIGHT and COLOR Pollers
		 *  4. LCD Display
		 */
		
		odo.start();
		// odoCorrection.start();
		
		uspoll.start();
		topus.start();
		
		lightpoll.start();
		colorpoll.start();
				
		//lcd.start();
		
		oa.initiate();
		// Basic set-up ends here 
		// ----------------------------------------------------------------
		//Ensures the claw starts at the position 0
		
			
		ObjectAvoidance oa = new ObjectAvoidance(odo, usMotor, uspoll);
		oa.initiate();

		//Do US Localization
				usloc.doLocalization();
				
				//Do LIGHT Localization
				
				lightloc.doLocalization();
		oa.travel(60, 60);
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
	}

}
