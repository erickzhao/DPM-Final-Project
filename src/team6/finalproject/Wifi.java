package team6.finalproject;

import java.io.IOException;
import java.util.HashMap;
import wifi.WifiConnection;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class Wifi {
	
	private static final String SERVER_IP = "192.168.2.6"; //this IP address is specific to a certain laptop. change accordingly
	private static final int TEAM_NUMBER = 6;
	public static final int GRID_SIZE = 10;
	
	//Raw data

	public static int buildingTeamNumber;
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
	
	//Edited data
	
	public static int ourStartingCorner; //Our actual starting corner after determining role
	public static double ourEndZoneX; //Middle of zone
	public static double ourEndZoneY; //Middle of zone
	public static double ourBadZoneX; //Middle of avoiding zone
	public static double ourBadZoneY; //Middle of avoiding zone
	
	
	
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
				System.out.println("Transmission read");//:\n" + t.toString());
				
				if(t.get("BTN").equals(TEAM_NUMBER)) 
				{//We're a Builder
					buildingTeamNumber = t.get("BTN");;
					buildingStartingCorner = t.get("BSC");
					ourStartingCorner = buildingStartingCorner;
				}
				else if (t.get("CTN").equals(TEAM_NUMBER)) 
				{//We're a collector
					collectorTeamNumber = t.get("CTN");;
					collectingStartingCorner = t.get("CSC");
					ourStartingCorner = collectingStartingCorner;
				}
				
				lrzX = changeOrientationX(ourStartingCorner, t.get("LRZx"), t.get("LRZy"));
				lrzY = changeOrientationY(ourStartingCorner, t.get("LRZx"), t.get("LRZy"));
				urzX = changeOrientationX(ourStartingCorner, t.get("URZx"), t.get("URZy"));
				urzY = changeOrientationY(ourStartingCorner, t.get("URZx"), t.get("URZy"));
				lgzX = changeOrientationX(ourStartingCorner, t.get("LGZx"), t.get("LGZy"));
				lgzY = changeOrientationY(ourStartingCorner, t.get("LGZx"), t.get("LGZy"));
				ugzX = changeOrientationX(ourStartingCorner, t.get("UGZx"), t.get("UGZy"));
				ugzY = changeOrientationY(ourStartingCorner, t.get("UGZx"), t.get("UGZy"));
				
				//Convert coordinates to proper distances
				
				if (buildingTeamNumber == 6)
				{					
					
					ourEndZoneX = ((lgzX+ugzX)/2.0); //*30.48; //Middle with tile size
					ourEndZoneY = ((lgzY+ugzY)/2.0); //*30.48; //Middle with tile size
					ourBadZoneX = ((lrzX+urzX)/2.0); //*30.48; //Middle bad with tile size
					ourBadZoneY = ((lrzY+urzY)/2.0); //*30.48; //Middle bad with tile size	
				}
				
				if (collectorTeamNumber == 6)
				{
					
					ourEndZoneX = ((lrzX+urzX)/2.0); //*30.48; //Middle with tile size
					ourEndZoneY = ((lrzY+urzY)/2.0); //*30.48; //Middle with tile size
					ourBadZoneX = ((lgzX+ugzX)/2.0); //*30.48; //Middle bad with tile size
					ourBadZoneY = ((lgzY+ugzY)/2.0); //*30.48; //Middle bad with tile size	
				}
					
				
			}
		}
	}
	
	//Method to flip for different starting points
	
	public static int changeOrientationX(int startPos, int x, int y) {
		
		int tmp;
		
      	if (startPos == 1) {
				//We're good
      		
      	}
      	else if (startPos == 2) {
          		tmp = x;
                x = y;
          		y = GRID_SIZE - tmp;
          		
      	}
      	else if (startPos == 3) {
          		x = GRID_SIZE - x;
          		y = GRID_SIZE - y;
          		
		}
      	else if (startPos == 4) {
          		tmp = y;
          		y = x;
          		x = GRID_SIZE - tmp;
         		
		}
		return x;
			
		}
public static int changeOrientationY(int startPos, int x, int y) {
		
		int tmp;
		
      	if (startPos == 1) {
				//We're good
      		
      	}
      	else if (startPos == 2) {
          		tmp = x;
                x = y;
          		y = GRID_SIZE - tmp;
          		
      	}
      	else if (startPos == 3) {
          		x = GRID_SIZE - x;
          		y = GRID_SIZE - y;
          		
		}
      	else if (startPos == 4) {
          		tmp = y;
          		y = x;
          		x = GRID_SIZE - tmp;
         		
		}
		return y;
			
		}
	}

