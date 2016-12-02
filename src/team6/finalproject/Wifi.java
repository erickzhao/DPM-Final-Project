package team6.finalproject;

import java.io.IOException;
import java.util.HashMap;
import wifi.WifiConnection;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

/**
 * Class that allows the robot to receive coordinates via WiFi. Displays transmitted coordinates.
 * @author Sean Lawlor, Myriam Ayad 
 * @version 1.0
 */

public class Wifi {
	
	private static final String SERVER_IP = "192.168.2.3"; //this IP address is specific to a certain laptop. change accordingly
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
	public static double ourBadZoneX1, ourBadZoneX2; //X-coordinates of avoiding zone
	public static double ourBadZoneY1, ourBadZoneY2; //Y-coordinates of avoiding zone
	
	private static TextLCD LCD = LocalEV3.get().getTextLCD();
	
	/**
	 * Gets the parameters from the WiFi conenction and stores them into static variables.
	 */
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
					
					ourEndZoneX = ((lgzX+ugzX)/2.0)*30.48; //Middle with tile size
					ourEndZoneY = ((lgzY+ugzY)/2.0)*30.48; //Middle with tile size
					// Badzone coordinates with tile size
					ourBadZoneX1 = lrzX*30.48;
					ourBadZoneX2 = urzX*30.48;
					ourBadZoneY1 = lrzY*30.48;
					ourBadZoneY2 = urzY*30.48; 
				}
				
				if (collectorTeamNumber == 6)
				{
					
					ourEndZoneX = ((lrzX+urzX)/2.0)*30.48; //Middle with tile size
					ourEndZoneY = ((lrzY+urzY)/2.0)*30.48; //Middle with tile size
					// Badzone coordinates with tile size
					ourBadZoneX1 = lgzX*30.48;
					ourBadZoneX2 = ugzX*30.48;
					ourBadZoneY1 = lgzY*30.48;
					ourBadZoneY2 = ugzY*30.48;	
				}
					
				
			}
		}
	}
	
	/**
	 * Flips the x coordinate given for the green and red zones according to which corner
	 * the robot is starting in. This is done to make the search algorithm identical for
	 * all four starting corners.
	 * @param startPos	the corner in which we are starting in
	 * @param x			the x coordinate being passed in by WiFi
	 * @param y			the y coordinate being passed in by WiFi
	 * @return			the x portion of the flipped coordinate
	 */
	public static int changeOrientationX(int startPos, int x, int y) {
		
		int tmp;
		
      	if (startPos == 1) {
      		//no change
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
	/**
	 * Flips the y coordinate given for the green and red zones according to which corner
	 * the robot is starting in. This is done to make the search algorithm identical for
	 * all four starting corners.
	 * @param startPos	the corner in which we are starting in
	 * @param x			the x coordinate being passed in by WiFi
	 * @param y			the y coordinate being passed in by WiFi
	 * @return			the y portion of the flipped coordinate
	 */
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
