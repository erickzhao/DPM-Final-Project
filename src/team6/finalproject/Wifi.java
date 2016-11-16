package team6.finalproject;

import java.io.IOException;
import java.util.HashMap;
import wifi.WifiConnection;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class Wifi {
	
	private static final String SERVER_IP = "192.168.2.6"; //this IP address is specific to a certain laptop. change accordingly
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
	
	public static void getParameters(){
		LCD.clear();
		
		WifiConnection conn = null;
		try {
			System.out.println("Connecting...");
			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, true);
		} catch (IOException e) {
			System.out.println("Connection failed");
		}
		
		LCD.clear();
		
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
				
			}
		}
	}
}
