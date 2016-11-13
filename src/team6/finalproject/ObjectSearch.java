package team6.finalproject;

/**
 * Class that scans the field for objects and determines if objects are to be avoided or if they are to be collected.
 * 
 * @author Myriam Ayad
 * @version 0.1
 */

public class ObjectSearch {
	
	private Odometer odo;
	private Navigation nav;
	private float speed = 350;
	private UltrasonicPoller lowerpoll;
	private double THRESHOLD = 35;
	private double sweepAng=90;
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
		nav.setSpeeds(-speed,speed);
		while(odo.getAng()<sweepAng){
			
		}
		nav.setSpeeds(0, 0);
	}
	
	/**
	 * Uses light sensor to determine if block is wooden or if block is blue styrofoam
	 */
	public void inspectBlock(){}
	
	/**
	 * Moves the robot to the starting point for the next neighborhood to scan
	 * @param wayPoint	coordinate for start of next neighborhood to scan
	 */
	public void travelToWaypoint(int wayPoint){}
}
