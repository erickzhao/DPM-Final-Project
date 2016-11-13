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
	
	/**
	 * Constructor for Object Search.
	 */
	public ObjectSearch(Odometer odo, Navigation nav) {
		this.odo = odo;
		this.nav = nav;
	}
	
	/**
	 * Sweeps the current neighborhood by rotating about a point, 
	 * keeping track of if objects are detected in the neighborhood
	 */
	public void sweep(){
	}
	
	/**
	 * Uses light sensor to determine if block is wooden or if block is blue styrofoam
	 */
	public void inspectBlock(){}
	
	/**
	 * Moves the robot to the starting point for the next neighborhood to scan
	 * @param wayPoint	the <code>int</code> coordinate for start of next neighborhood to scan
	 */
	public void travelToWaypoint(int wayPoint){}
}
