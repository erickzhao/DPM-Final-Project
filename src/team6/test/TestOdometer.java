package team6.test;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import team6.finalproject.LCDInfo;
import team6.finalproject.LightPoller;
import team6.finalproject.Odometer;
import team6.finalproject.OdometryCorrection;
import team6.finalproject.SquareDriver;
import team6.finalproject.UltrasonicPoller;

public class TestOdometer extends Thread{

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
	 
	private static final Port lightPort = LocalEV3.get().getPort("S4"); 
	 
	//constants
	public static final double WHEEL_RADIUS = 2.15; //needs to be changed for robots physical configs
	public static final double TRACK = 15.2; //needs to be changed for robots physical configs

	public static void main(String[] args) {

		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true, WHEEL_RADIUS, TRACK);
		
		LCDInfo lcd = new LCDInfo(odo);

		SensorModes lightSensor = new EV3ColorSensor(lightPort);
		SampleProvider lightValue = lightSensor.getMode("Red");
		float[] lightData = new float[lightValue.sampleSize()];

		LightPoller lightpoll = new LightPoller(lightValue, lightData);
		
		OdometryCorrection odoCorrection = new OdometryCorrection(odo); 
		
		odo.start();
		odoCorrection.start();
		
		(new Thread(){
			public void run(){
				SquareDriver.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
			}
		}).start();
	}
}
