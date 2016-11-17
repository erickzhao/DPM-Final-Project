package team6.test;
//Author : Andrei Ungur
//NOTE: This hasn't yet been tested and doesn't implement obstacle avoidance.
import team6.finalproject.*;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class TestFinal {

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
	 private static final EV3LargeRegulatedMotor clawMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	 private static final EV3MediumRegulatedMotor usMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));
	 private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	 private static final Port lightPort = LocalEV3.get().getPort("S1");
	 private static final Port usTopPort = LocalEV3.get().getPort("S2");
	 private static final Port colorPort = LocalEV3.get().getPort("S3");
	 private static final Port usBottomPort = LocalEV3.get().getPort("S4");
	 public static UltrasonicPoller uspoll;
	 //constants
	 public static final double WHEEL_RADIUS = 2.15; //needs to be changed for robots physical configs
	 public static final double TRACK = 15.6; //needs to be changed for robots physical configs
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true, WHEEL_RADIUS, TRACK);
		
		@SuppressWarnings("resource")
		SensorModes usSensor = new EV3UltrasonicSensor(usBottomPort);
		SampleProvider usValue = usSensor.getMode("Distance");
		float[] usData = new float[usValue.sampleSize()];
		
		@SuppressWarnings("resource")
		SensorModes lightSensor = new EV3ColorSensor(lightPort);
		SampleProvider lightValue = lightSensor.getMode("Red");
		float[] lightData = new float[lightValue.sampleSize()];
		
		uspoll = new UltrasonicPoller(usValue, usData);
		LightPoller lightpoll = new LightPoller(lightValue,lightData);
		LCDInfo lcd = new LCDInfo(odo,uspoll); 
		USLocalizer usloc = new USLocalizer(odo,uspoll);
		OdometryCorrection odoCorrection = new OdometryCorrection(odo); 
		
		ObjectAvoidance oa = new ObjectAvoidance(odo, usMotor, uspoll);
		
		odo.start();
		odoCorrection.start();
		uspoll.start();
		lightpoll.start();
		lcd.start();
		usloc.doLocalization();
		
		LightLocalizer lightloc = new LightLocalizer(odo,9.8);
		lightloc.doLocalization();
		// END LOCALIZATION
		// BEGIN ALGORITHM
		Navigation nav = new Navigation(odo);
		
		ObjectSearch search = new ObjectSearch(odo, nav, uspoll,oa);
		search.doSearch();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
	}

}
