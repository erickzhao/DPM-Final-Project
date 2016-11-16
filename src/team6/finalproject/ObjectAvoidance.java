package team6.finalproject;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3MediumRegulatedMotor;


/**
 * ObjectAvoidance Class
 * <p>
 * Travel to the destination while avoiding any obstacles
 * @author Kael Du 
 * @version 0.2
 */
public class ObjectAvoidance {
	private Odometer odo;
	private Navigation nav;
	private EV3MediumRegulatedMotor usMotor;
	private UltrasonicPoller usPoller;
	
	private static final int MAX_FILTER = 3; // Must be an odd number for performance reason
	private static final int MAX_US_DISTANCE = 255;
	private static final float DANGER_DIST = 10;
	private static final float DEADBAND = 2;
	private static final double END_ANGLE_CORRECTION = 155;
	private static final int BANGBANG_SENSOR_ANGLE = -135;
	
	private boolean navigating;
	private float[] archivedValues = new float[MAX_FILTER];
	private int archivedCount = 0;
	/**
	 * Constructor for ObjectAvoidance. 
	 * @param waypointX The x-value of the destination -- <code>double</code>
	 * @param waypointY The y-value of the destination -- <code>double</code>
	 */
	public ObjectAvoidance(double waypointX, double waypointY, Odometer odo, EV3MediumRegulatedMotor usMotor,
			UltrasonicPoller usPoller){
		this.odo = odo;
		this.nav = new Navigation(odo, waypointX, waypointY);	
		this.usMotor = usMotor;
		this.usPoller = usPoller;
		for (int i = 0; i < MAX_FILTER; i++){
			archivedValues[i] = MAX_US_DISTANCE;
		}
	}
	
	
	/**
	 * Travel to the set destination (x,y) while avoiding obstacles
	 */
	public void travel(){
		nav.start();
		avoiding();
	}
	
	/**
	 * Median filter for the ultrasonic data
	 * @return filtered distance
	 */
	public float getFilteredData() {
		// Fetch data
		float distance = usPoller.getDistance();
		// Trunk all oversize data
		if (distance > MAX_US_DISTANCE){
			distance = MAX_US_DISTANCE;
		}
		archivedValues[archivedCount] = distance;
		archivedCount = customIncrement(archivedCount, MAX_FILTER);
		
		// Apply the median filter
		float median =  archivedValues[(MAX_FILTER/2 + archivedCount) % MAX_FILTER];
		if (archivedValues[archivedCount] > median){
			archivedValues[archivedCount] = median;
		}
		//return archivedValues[archivedCount];
		return distance;
		
	}
	
	/**
	 * A helper function that wrap the incrementing number
	 * @param thisNum
	 * @param limitNum
	 * @return
	 */
	public int customIncrement(int thisNum, int limitNum){
		int res;
		if (thisNum +1 < limitNum){
			res = thisNum + 1;
		} else {
			res = 0;
		}
		return res;
	}
	
	/**
	 * Avoid the obstacles with bangbang controller
	 */
	public void avoiding(){
		float distance;
		navigating = true;
		while (navigating){
			distance = getFilteredData();
			if (distance <= DANGER_DIST){
				nav.cancelled = true;
				nav.setSpeeds(0, 0);
				Sound.beepSequence();
				nav.turnTo(wrapAng(odo.getAng() - 90), true);
				Sound.beepSequenceUp();
				double endAng = wrapAng(odo.getAng() + END_ANGLE_CORRECTION);
				usMotor.rotate(BANGBANG_SENSOR_ANGLE);
				bangbang(endAng);
				usMotor.rotateTo(0);
				nav.cancelled = false;
			}
			if (!nav.navigating()){
				navigating = false;
			}
		}
	}
	
	/**
	 * Bangbang controller for object avoidance
	 */
	public void bangbang(double angle){
		if (odo.getAng() < angle){
			while (odo.getAng() < angle){
				float errorDistance = getFilteredData() - DANGER_DIST;
				bangbangLogic(errorDistance);
			}
		} else {
			while (odo.getAng() < angle || odo.getAng() >= 360 - END_ANGLE_CORRECTION){
				float errorDistance = getFilteredData() - DANGER_DIST;
				bangbangLogic(errorDistance);
			}
		}
	
		
		
	}
	
	/**
	 * Internal logic of the bangbang controller
	 * @param errorDistance
	 */
	private void bangbangLogic(float errorDistance){
		if (Math.abs(errorDistance) <= DEADBAND){ //moving in straight line
			nav.setSpeeds(150, 150);
		} else if (errorDistance > 0){ //too close to wall
			nav.setSpeeds(100, 250);
		} else if (errorDistance < 0){ // getting too far from the wall
			nav.setSpeeds(250, 100);
		}
	}
	
	/**
	 * wrap the angle if it is larger than 360 degrees
	 * @param angle
	 * @return
	 */
	public double wrapAng(double angle){
		double res;
		if (angle >= 0){
			res = angle % 360.0;
		} else {
			res = angle + 360.0;
		}
		return res;
	}
	 
	
	
}
