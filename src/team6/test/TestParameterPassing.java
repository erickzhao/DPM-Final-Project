package team6.test;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import team6.finalproject.LCDInfo;
import team6.finalproject.LightPoller;
import team6.finalproject.Navigation;
import team6.finalproject.Odometer;
import team6.finalproject.OdometryCorrection;
import team6.finalproject.Wifi;

public class TestParameterPassing {

	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	 
	private static final Port lightPort = LocalEV3.get().getPort("S4"); 
	 
	//constants
	public static final double WHEEL_RADIUS = 2.15; //needs to be changed for robots physical configs
	public static final double TRACK = 15.2; //needs to be changed for robots physical configs
	private static final double GRID_SIZE= 30.48;
	public static void main(String[] args) {
		
		Wifi.getParameters();
		
		System.out.println("Building Team: " + Wifi.buldingTeamNumber);
		System.out.println("Building Corner: " + Wifi.buildingStartingCorner);
		System.out.println("Collecting Team: " + Wifi.collectorTeamNumber);
		System.out.println("Collecting Corner: " + Wifi.collectingStartingCorner);
		System.out.println("Lower Red X: " + Wifi.lrzX);
		System.out.println("Lower Red Y: " + Wifi.lrzY);
		System.out.println("Upper Red X: " + Wifi.urzX);
		System.out.println("Upper Red Y: " + Wifi.urzY);
		System.out.println("Lower Green X: " + Wifi.lgzX);
		System.out.println("Lower Green Y: " + Wifi.lgzY);
		System.out.println("Upper Green X: " + Wifi.ugzX);
		System.out.println("Upper Green Y: " + Wifi.ugzY);
		
		Button.waitForAnyPress();
		
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		
		
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true, WHEEL_RADIUS, TRACK);
		Navigation navig = new Navigation(odo);
		
		LCDInfo lcd = new LCDInfo(odo);
		lcd.start();

		@SuppressWarnings("resource")
		SensorModes lightSensor = new EV3ColorSensor(lightPort);
		SampleProvider lightValue = lightSensor.getMode("Red");
		float[] lightData = new float[lightValue.sampleSize()];

		LightPoller lightPoller = new LightPoller(lightValue, lightData);
		lightPoller.start();
		
		OdometryCorrection odoCorrection = new OdometryCorrection(odo); 
		
		odo.start();
		odoCorrection.start();
		
		navig.travelTo(Wifi.lgzX*GRID_SIZE, Wifi.lgzY*GRID_SIZE);
		
		/*(new Thread(){
			public void run(){
				navig.travelTo(WifiTest.lgzX, WifiTest.lgzY);
			}
		}).start();*/
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
			System.exit(0);
	}

}

