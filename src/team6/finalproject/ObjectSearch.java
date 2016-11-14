package team6.finalproject;

import java.util.ArrayList;
import java.util.List;

import lejos.hardware.Sound;

/**
 * Class that scans the field for objects and determines if objects are to be avoided or if they are to be collected.
 * 
 * @author Myriam Ayad
 * @version 0.1
 */

public class ObjectSearch {
	
	private Odometer odo;
	private Navigation nav;
	private float speed = 150;
	private UltrasonicPoller lowerpoll;
	private List<Float> obstacles = new ArrayList<Float>();
	private double THRESHOLD = 60;
	private double sweepAng=90;
	private boolean sameObject=false; //Determines if the robot is looking at the same object
	/**
	 * Constructor for Object Search.
	 */
	public ObjectSearch(Odometer odo, Navigation nav,UltrasonicPoller uspoll) {
		this.odo = odo;
		this.nav = nav;
		this.lowerpoll = uspoll;
	}
	
	/**
	 * Sweeps the current neighborhood by rotating about a point, 
	 * keeping track of if objects are detected in the neighborhood
	 */
	public void sweep(){
		//Start sweeping counter-clockwise (I think it increases the angle)
		nav.setSpeeds(-speed,speed);
		while(odo.getAng()<sweepAng || odo.getAng()>(sweepAng+180)){
			//An object is seen
			if(lowerpoll.getDistance()<=THRESHOLD){
				Sound.beep();
				//Add to object list
				obstacles.add(new Float(odo.getAng()));
				//Continue turning as long as it sees the same object
				while(lowerpoll.getDistance()<=THRESHOLD){
					continue;
				}
				//Stop, to show that the object isn't seen anymore
				nav.setSpeeds(0,0);
				try{Thread.sleep(500);}catch(Exception e){};
				//Continue turning
				nav.setSpeeds(-speed, speed);
			}
		}
		nav.setSpeeds(0, 0);
	}
	
	/**
	 * Uses light sensor to determine if block is wooden or if block is blue styrofoam
	 */
	public void inspectBlock(){}
	
	/**
	 * Moves the robot to the starting point for the next neighborhood to scan
	 * @param wayPoint	the <code>int</code> coordinate for start of next neighborhood to scan
	 */
	public void travelToWaypoint(int wayPoint){
		
	}
}
