package team6.finalproject;

import java.io.IOException;
import java.util.HashMap;
import wifi.WifiConnection;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class Wifi {
	
	private static final String SERVER_IP = "192.168.2.35"; //this IP address is specific to a certain laptop. change accordingly
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
				urzY = t.get("LRZy");
				lgzX = t.get("LGZx");
				lgzY = t.get("LGZy");
				ugzX = t.get("UGZx");
				ugzY = t.get("UGZy");
				
				/*
				System.out.println("Building Team: " + buldingTeamNumber);
				System.out.println("Building Corner: " + buildingStartingCorner);
				System.out.println("Collecting Team: " + collectorTeamNumber);
				System.out.println("Collecting Corner: " + collectingStartingCorner);
				System.out.println("Lower Red X: " + lrzX);
				System.out.println("Lower Red Y: " + lrzY);
				System.out.println("Upper Red X: " + urzX);
				System.out.println("Upper Red X: " + urzY);
				System.out.println("Lower Green X: " + lgzX);
				System.out.println("Lower Green Y: " + lgzY);
				System.out.println("Upper Green X: " + ugzX);
				System.out.println("Upper Green Y: " + ugzY);
				*/
			}
		}
	}
}
