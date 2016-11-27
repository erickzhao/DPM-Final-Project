package team6.test;

//import java.io.IOException;
//import java.util.HashMap;
//import wifi.WifiConnection;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
//import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
//import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
//import team6.finalproject.LCDInfo;
import team6.finalproject.LightPoller;
import team6.finalproject.Navigation;
import team6.finalproject.Odometer;
import team6.finalproject.OdometryCorrection;
import team6.finalproject.UltrasonicPoller;
import team6.finalproject.Wifi;

public class TestRole {

	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	//private static final EV3LargeRegulatedMotor clawMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	//private static final EV3MediumRegulatedMotor usMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	private static final Port lightPort = LocalEV3.get().getPort("S1");
	/*private static final Port usTopPort = LocalEV3.get().getPort("S2");
	private static final Port colorPort = LocalEV3.get().getPort("S3");
	private static final Port usBottomPort = LocalEV3.get().getPort("S4");
	*/ 
	public static UltrasonicPoller uspoll; //US Poller for localization/Object recognition
	public static UltrasonicPoller topus; //US Poller for obstacle avoidance
	 
	//constants
	public static final double WHEEL_RADIUS = 2.15; //needs to be changed for robots physical configs
	public static final double TRACK = 15.6; //needs to be changed for robots physical configs
	//private static final double LStoWB = 7.5; //Light Sensor to Wheel Base value
	
	public static void main(String[] args) {
		Wifi.getParameters();
		if (!(Wifi.ourStartingCorner==1 || Wifi.ourStartingCorner==2 || Wifi.ourStartingCorner==3 || Wifi.ourStartingCorner==4)){
			System.exit(0);
		}
		
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true, WHEEL_RADIUS, TRACK);
		Navigation navig = new Navigation(odo);
		
		@SuppressWarnings("resource")
		SensorModes lightSensor = new EV3ColorSensor(lightPort);
		SampleProvider lightValue = lightSensor.getMode("Red");
		float[] lightData = new float[lightValue.sampleSize()];

		LightPoller lightPoller = new LightPoller(lightValue, lightData);
		lightPoller.start();
		
		OdometryCorrection odoCorrection = new OdometryCorrection(odo); 
		
		odo.start();
		odoCorrection.start();
		
		while(Button.waitForAnyPress() == Button.ID_ENTER);
		
		navig.travelTo(Wifi.ourEndZoneX, Wifi.ourEndZoneY);
	}
}
