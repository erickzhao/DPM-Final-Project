package team6.finalproject;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * Starting point of the program.
 * <o> Initializes sensor and motor ports. Houses the <code>main</code> method for the system
 * @author Andrei Ungur
 * @version 2.0
 */
public class Final {

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
	 
	 public static UltrasonicPoller uspoll; //US Poller for localization/Object recognition
	 public static UltrasonicPoller topus; //US Poller for obstacle avoidance
	 
	 //constants
	 public static final double WHEEL_RADIUS = 2.15; //needs to be changed for robots physical configs
	 public static final double TRACK = 15.6; //needs to be changed for robots physical configs
	 private static final double LStoWB = 7.5; //Light Sensor to Wheel Base value
	 
	public static void main(String[] args) {
		
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true, WHEEL_RADIUS, TRACK);
		
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
		
		//Color sensor for block inspection
		@SuppressWarnings("resource")
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("RGB");
		float[] colorData = new float[colorValue.sampleSize()];
		
		//Initialize US Pollers
		uspoll = new UltrasonicPoller(usValue, usData,bottomSensor);
		topus = new UltrasonicPoller(usValueTop, usDataTop,topSensor);
		
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
		//Get Wi-Fi data
		Wifi.getParameters();
		if (!(Wifi.ourStartingCorner==1 || Wifi.ourStartingCorner==2 || Wifi.ourStartingCorner==3 || Wifi.ourStartingCorner==4)){
			System.exit(0);
		}
		
		CountdownTimer countdown = new CountdownTimer();
		countdown.start();
		
		//We don't yet have the claw implemented, so we force it to stay at 0.
		clawMotor.rotate(0);
		
		//Do US Localization
		usloc.doLocalization();
		
		//Do LIGHT Localization
		
		lightloc.doLocalization();
		Sound.beepSequenceUp();
		// END LOCALIZATION
		
		// BEGIN ALGORITHM
		Navigation nav = new Navigation(odo);
		
		ObjectSearch search = new ObjectSearch(odo, nav, uspoll,oa,clawMotor,countdown);
		//Do ALGORITHM
		search.doSearch();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
	}

}
