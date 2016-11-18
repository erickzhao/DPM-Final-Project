package team6.test;

/*
* @author Sean Lawlor
* @date November 3, 2011
* @class ECSE 211 - Design Principle and Methods
* 
* Modified by F.P. Ferrie
* February 28, 2014
* Changed parameters for W2014 competition
* 
* Modified by Francois OD
* November 11, 2015
* Ported to EV3 and wifi (from NXT and bluetooth)
* Changed parameters for F2015 competition
* 
* Modified by Michael Smith
* November 1, 2016
* Cleaned up print statements, old code, formatting
* 
*/
import java.io.IOException;
import java.util.HashMap;
import wifi.WifiConnection;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
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

public class WifiTest {
	/*
	 * Example call of the transmission protocol 
	 * We use System.out.println() instead of LCD printing so that 
	 * full debug output (e.g. the very long string containing the transmission) 
	 * can be read on the screen or a remote console such as the 
	 * EV3Control program via Bluetooth or WiFi
	 */

	/* *** INSTRUCTIONS ***
	 * There are two variables to set manually on the EV3 client:
	 * 1. SERVER_IP: the IP address of the computer running the server application
	 * 2. TEAM_NUMBER: your project team number
	 * */

	private static final String SERVER_IP = "192.168.2.3"; //this IP address is specific to a certain laptop. change accordingly
	private static final int TEAM_NUMBER = 6;

	public static int buldingTeamNumber;
	public static int collectorTeamNumber;
	public static int buildingStartingCorner;
	public static int collectingStartingCorner;
	public static int lrzX;
	public static int lrzY;
	public static int urzX;
	public static int urzY;
	public static int lgzX;
	public static int lgzY;
	public static int ugzX;
	public static int ugzY;
	
	private static TextLCD LCD = LocalEV3.get().getTextLCD();

	
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	 
	private static final Port lightPort = LocalEV3.get().getPort("S4"); 
	 
	//constants
	public static final double WHEEL_RADIUS = 2.15; //needs to be changed for robots physical configs
	public static final double TRACK = 15.2; //needs to be changed for robots physical configs
	
	private static final double GRID_SIZE= 30.48;
	
	public static void main(String[] args) {

		LCD.clear();

		/*
		 * WiFiConnection will establish a connection to the server and wait for data
		 * If the server is not running, this will throw an IOException
		 * If the server is running but the user has yet to press start on the Java GUI with some data,
		 * this will wait forever
		 * During the competition, this means you can start your code, place it on the field, and it will wait
		 * for data from the professor's computer
		 * If you need it to stop, access the robot via the EV3Control program and click "Stop Program"
		 * Alternatively, you can reset the robot but you risk SD card corruption
		 * Note that you can set the final argument debugPrint as false to disable printing to the LCD if desired.
		 */ 
		WifiConnection conn = null;
		try {
			System.out.println("Connecting...");
			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, true);
		} catch (IOException e) {
			System.out.println("Connection failed");
		}
		
		LCD.clear();

		/*
		 * This section of the code reads and prints the data received from the server,
		 * stored as a HashMap with String keys and Integer values.
		 */
		if (conn != null) {
			HashMap<String, Integer> t = conn.StartData;
			if (t == null) {
				System.out.println("Failed to read transmission");
			} else {
				System.out.println("Transmission read:\n" + t.toString());
				
				if(t.get("BTN").equals(TEAM_NUMBER)) 
				{//We're a Builder
					buldingTeamNumber = t.get("BTN");;
					buildingStartingCorner = t.get("BSC");
				}
				else if (t.get("CTN").equals(TEAM_NUMBER)) 
				{//We're a collector
					collectorTeamNumber = t.get("CTN");;
					collectingStartingCorner = t.get("CSC");
				}
				
				lrzX = t.get("LRZx");
				lrzY = t.get("LRZy");
				urzX = t.get("URZx");
				urzY = t.get("URZy");
				lgzX = t.get("LGZx");
				lgzY = t.get("LGZy");
				ugzX = t.get("UGZx");
				ugzY = t.get("UGZy");
				
				System.out.println("Building Team: " + buldingTeamNumber);
				System.out.println("Building Corner: " + buildingStartingCorner);
				System.out.println("Collecting Team: " + collectorTeamNumber);
				System.out.println("Collecting Corner: " + collectingStartingCorner);
				System.out.println("Lower Red X: " + lrzX);
				System.out.println("Lower Red Y: " + lrzY);
				System.out.println("Upper Red X: " + urzX);
				System.out.println("Upper Red Y: " + urzY);
				System.out.println("Lower Green X: " + lgzX);
				System.out.println("Lower Green Y: " + lgzY);
				System.out.println("Upper Green X: " + ugzX);
				System.out.println("Upper Green Y: " + ugzY);
				
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
				
				
				@SuppressWarnings("resource")
				SensorModes lightSensor = new EV3ColorSensor(lightPort);
				SampleProvider lightValue = lightSensor.getMode("Red");
				float[] lightData = new float[lightValue.sampleSize()];

				LightPoller lightPoller = new LightPoller(lightValue, lightData);
				lightPoller.start();
				
				OdometryCorrection odoCorrection = new OdometryCorrection(odo); 
				
				odo.start();
				odoCorrection.start();
				
				navig.travelTo(lgzX*GRID_SIZE, lgzY*GRID_SIZE);
				
				/*(new Thread(){
					public void run(){
						navig.travelTo(WifiTest.lgzX, WifiTest.lgzY);
					}
				}).start();*/
				while (Button.waitForAnyPress() != Button.ID_ESCAPE);
					System.exit(0);
			}
		}

		// Wait until user decides to end program
		Button.waitForAnyPress();
	}
}
