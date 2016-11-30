package team6.finalproject;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
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
	
	private static final double GRID_LENGTH = 30.48;
	private static final int MAX_FILTER = 3; // Must be an odd number for performance reason
	private static final int MAX_US_DISTANCE = 255;
	private static final float DANGER_DIST = (float) 14.4;
	private static final float DEADBAND = 2;
	private static final double SAFE_DISTANCE_AWAY = -3;
	private static final double END_ANGLE_CORRECTION = 112;
	private static final int BANGBANG_SENSOR_ANGLE = -130;
	private static final int BANGBANG_TRAVEL_SPEED = 195;
	private static final int BANGBANG_ULTRASLOW_WHEEL_SPEED = 72;
	private static final int BANGBANG_SLOW_WHEEL_SPEED = 144;
	private static final int BANGBANG_FAST_WHEEL_SPEED = 275;
	private static final int THRESHOLD_ANGLE = 15;
	private static final int ROTATING_ANGLE = 63;
	private static final int ROTATING_SPEED = 360;
	private static final double ROBOT_HALF_WIDTH = 6.7;
	private static final int WALL_CHECK_TIMES = 5;
	private static final double DISTANCE_CHECK = 91;
	private static final double BLOCK_THICKNESS = 10;
	private static final double ERROR_MARGIN = 6.3;
	
	
	private boolean navigating;
	private float[] archivedValues = new float[MAX_FILTER];
	private int archivedCount = 0;
	private static List<Double> redZoneXa = new ArrayList<Double>();
	private static List<Double> redZoneYa = new ArrayList<Double>();
	private static List<Double> redZoneXb = new ArrayList<Double>();
	private static List<Double> redZoneYb = new ArrayList<Double>();
	private boolean obstacleMode = false;
		
	/**
	 * Constructor for ObjectAvoidance. 
	 * @param waypointX The x-value of the destination -- <code>double</code>
	 * @param waypointY The y-value of the destination -- <code>double</code>
	 */
	public ObjectAvoidance(Odometer odo, EV3MediumRegulatedMotor usMotor,
			UltrasonicPoller usPoller){
		this.odo = odo;
		this.nav = new Navigation(odo);	
		this.usMotor = usMotor;
		this.usPoller = usPoller;
		for (int i = 0; i < MAX_FILTER; i++){
			archivedValues[i] = MAX_US_DISTANCE;
		}
	}
	
	public void initiate(){
		addRedZone(-GRID_LENGTH, -GRID_LENGTH, 0 - (ROBOT_HALF_WIDTH + 2.5*ERROR_MARGIN), 
				0 - (ROBOT_HALF_WIDTH + 2.5*ERROR_MARGIN));  // X1
		addRedZone(Wifi.ourBadZoneX1, Wifi.ourBadZoneY1, 
				Wifi.ourBadZoneX2, Wifi.ourBadZoneY2); // Pass the Redzone coordinates
		addRedZone(10*GRID_LENGTH, -GRID_LENGTH, 11*GRID_LENGTH, 0);  // X2
		addRedZone(10*GRID_LENGTH, 10*GRID_LENGTH, 11*GRID_LENGTH, 11*GRID_LENGTH); // X3
		addRedZone(-GRID_LENGTH, 10*GRID_LENGTH, 0, 11*GRID_LENGTH); // X4
				
		nav.start();
	}
	
	/**
	 * Travel to the set destination (x,y) while avoiding obstacles
	 */
	public void travel(double x, double y){
		int index = isInRed (x, y);
		double[] waypoints = {x,y};
		if (index < redZoneXa.size()){ // if the point is inside a pre-determined red zone
			waypoints = avoidRed(odo.getX(), odo.getY(), index);
		}
		travelLogic(waypoints[0],waypoints[1]);
		
		
	}
	
	private void travelLogic(double x, double y){
		nav.setWaypoints(x, y);
		nav.setNavigating(true);
		nav.cancelled = false;
		avoiding(x, y);
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
		return archivedValues[archivedCount];
		// return distance;
		
	}
	
	/**
	 * A helper function that wrap the incrementing number
	 * @param thisNum
	 * @param limitNum
	 * @return increment the number that coul wrap around
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
	private void avoiding(double destX, double destY){
		float distance;
		navigating = true;
		usMotor.setSpeed(ROTATING_SPEED);
		int programCount = 0;
		while (navigating){
			if (!usMotor.isMoving() && usMotor.getTachoCount() <= THRESHOLD_ANGLE){
				usMotor.rotateTo(ROTATING_ANGLE, true);
			}
			else if (!usMotor.isMoving() && usMotor.getTachoCount() > THRESHOLD_ANGLE){
				usMotor.rotateTo(-ROTATING_ANGLE, true);
			}
			distance = getFilteredData();
			if (distance <= DANGER_DIST){
				if (wallAhead() && programCount < WALL_CHECK_TIMES){
					programCount = programCount + 1;
					continue;
				}
				nav.cancelled = true;
				nav.setSpeeds(0, 0);
				nav.goForward(SAFE_DISTANCE_AWAY);
				nav.turnTo(wrapAng(odo.getAng() - 90), true);
				double endAng = wrapAng(odo.getAng() + END_ANGLE_CORRECTION);
				usMotor.rotateTo(BANGBANG_SENSOR_ANGLE);
				bangbang(endAng);
				usMotor.rotateTo(0);
				programCount = 0;
				nav.cancelled = false;
			}
			int index = redZoneAhead();
			if (index < redZoneXa.size() && !obstacleMode){
				goAroundRedZone(destX, destY, index);
				break;
			}
			
			if (!nav.navigating()){
				navigating = false;
				usMotor.rotateTo(0, true);
			}
		}
	}
	
	/**
	 * Bangbang controller for object avoidance
	 */
	private void bangbang(double angle){
		double x = odo.getX();
		double y = odo.getY();
		if (odo.getAng() < angle){
			while ((odo.getAng() < angle) && (distanceTravelled(x,y) < DISTANCE_CHECK)){
				float errorDistance = getFilteredData() - DANGER_DIST;
				bangbangLogic(errorDistance);
				if(nearWall()){
					evade();
					break;
				}
			}
		} else {
			while ((odo.getAng() < angle || odo.getAng() >= 360 - END_ANGLE_CORRECTION)
					&& (distanceTravelled(x,y) < DISTANCE_CHECK)){
				float errorDistance = getFilteredData() - DANGER_DIST;
				bangbangLogic(errorDistance);
				if(nearWall()){
					evade();
					break;
				}
			}
		}
	
		
		
	}
	
	/**
	 * Internal logic of the bangbang controller
	 * @param errorDistance
	 */
	private void bangbangLogic(float errorDistance){
		if (Math.abs(errorDistance) <= DEADBAND){ 
			nav.setSpeeds(BANGBANG_TRAVEL_SPEED, BANGBANG_TRAVEL_SPEED);
		} else if (errorDistance > 0){
			nav.setSpeeds(BANGBANG_SLOW_WHEEL_SPEED, BANGBANG_FAST_WHEEL_SPEED);
		} else if (errorDistance < 0){ 
			nav.setSpeeds(BANGBANG_FAST_WHEEL_SPEED, BANGBANG_ULTRASLOW_WHEEL_SPEED);
		}
	}
	
	/**
	 * wrap the angle if it is larger than 360 degrees
	 * @param angle that is needed
	 * @return return the wrapped angle
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
	
	/**
	 * Add a new red zone that the robot shall avoid 
	 * @param x1 One of the x-coordinate of the redZone
	 * @param y1 One of the y-coordinate of the redZone
	 * @param x2 The other x-coordinate
	 * @param y2 The other y-coordinate
	 */
	public void addRedZone(double x1, double y1, double x2, double y2){
		double xa, xb, ya, yb;
		
		xa = Math.min(x1, x2) - ROBOT_HALF_WIDTH;
		xb = Math.max(x1, x2) + ROBOT_HALF_WIDTH;
		ya = Math.min(y1, y2) - ROBOT_HALF_WIDTH;
		yb = Math.max(y1, y2) + ROBOT_HALF_WIDTH;
		
		redZoneXa.add(xa);
		redZoneYa.add(ya);
		redZoneXb.add(xb);
		redZoneYb.add(yb);
	}
	
	/**
	 * Check if a point is in redzone
	 * @param x
	 * @param y
	 * @return return the index of the redzone the point is in, if not in any redzone, return
	 * the total number of redzones
	 */
	private int isInRed (double x, double y){
		int redNumber = redZoneXa.size();
		for (int i = 0; i < redNumber; i++){
			if ( x > redZoneXa.get(i) && x < redZoneXb.get(i) && y > redZoneYa.get(i) 
					&& y < redZoneYb.get(i)){
				redNumber = i;
				break;
			}
		}
		return redNumber;		
	}
	

	/**
	 * check if one-block-distance ahead is in the redzone
	 * @return return the index of the redzone
	 */
	public int redZoneAhead(){
		int res = redZoneXa.size();
		double angle = odo.getAng()/180.0*Math.PI;
		int index = isInRed(odo.getX()+Math.cos(angle)*BLOCK_THICKNESS/2.5, odo.getY()+Math.sin(angle)*BLOCK_THICKNESS/2.5);
		if (index < res && !nav.turning()){
			res = index;
		}
		return res;
	}
	
	/**
	 * Check if there's a red zone ahead
	 * @return return true if there's a red zone ahead
	 */
	public boolean redAhead(){
		return redZoneAhead() < redZoneXa.size();
	}
	
	/**
	 * Go around the red zone knowing the index and the destination coordinates
	 * @param destinationX
	 * @param destinationY
	 * @param index
	 */
	public void goAroundRedZone(double destinationX, double destinationY, int index){
		double x = odo.getX();
		double y = odo.getY();
		obstacleMode = true;
		if (x >= redZoneXb.get(index) && y >= redZoneYa.get(index)){
			travel(redZoneXb.get(index)-SAFE_DISTANCE_AWAY, redZoneYb.get(index)-SAFE_DISTANCE_AWAY);
			travel(redZoneXa.get(index)+SAFE_DISTANCE_AWAY, redZoneYb.get(index)-SAFE_DISTANCE_AWAY);
			obstacleMode = false;
			travel(destinationX, destinationY);
		}
		else if (x <= redZoneXb.get(index) && y >= redZoneYb.get(index)){
			travel(redZoneXa.get(index)+SAFE_DISTANCE_AWAY, redZoneYb.get(index)-SAFE_DISTANCE_AWAY);
			travel(redZoneXa.get(index)+SAFE_DISTANCE_AWAY, redZoneYa.get(index)+SAFE_DISTANCE_AWAY);
			obstacleMode = false;
			travel(destinationX, destinationY);
		}
		else if (x <= redZoneXa.get(index) && y <= redZoneYb.get(index)){
			travel(redZoneXa.get(index)+SAFE_DISTANCE_AWAY, redZoneYa.get(index)+SAFE_DISTANCE_AWAY);
			travel(redZoneXb.get(index)-SAFE_DISTANCE_AWAY, redZoneYa.get(index)+SAFE_DISTANCE_AWAY);
			obstacleMode = false;
			travel(destinationX, destinationY);
		}
		else if (x >= redZoneXa.get(index) && y <= redZoneYa.get(index)){
			travel(redZoneXb.get(index)-SAFE_DISTANCE_AWAY, redZoneYa.get(index)+SAFE_DISTANCE_AWAY);
			travel(redZoneXb.get(index)-SAFE_DISTANCE_AWAY, redZoneYb.get(index)-SAFE_DISTANCE_AWAY);
			obstacleMode = false;
			travel(destinationX, destinationY);
		} 
		
	}
	

	private double[] avoidRed (double x, double y, int index){
		double adjustedX = x;
		double adjustedY = y;
		
		if (Math.abs( x - redZoneXa.get(index)) < Math.abs( x - redZoneXb.get(index))){
			adjustedX = redZoneXa.get(index) - ERROR_MARGIN;
		} else {
			adjustedX = redZoneXb.get(index) + ERROR_MARGIN;
		}
		if (Math.abs( y - redZoneYa.get(index)) < Math.abs( y - redZoneYb.get(index))){
			adjustedY = redZoneYa.get(index) - ERROR_MARGIN;
		} else {
			adjustedY = redZoneYb.get(index) + ERROR_MARGIN;
		}
		
		double[] res = {adjustedX, adjustedY};
		return res;
	}
	
	
	public void saveObstacleToMap(double x, double y, double angle) {
		if (angle<=45 || angle>=315){
			addRedZone(x, y-5, x+10, y+5);
		} else if (angle>=45 && angle<=135){
			addRedZone(x-5, y, x+5, y+10);
		} else if (angle>=135 && angle <=225){
			addRedZone(x, y-5, x-10, y+5);
		} else {
			addRedZone(x-5, y, x+5, y-10);
		}
		
	}
	
	public boolean wallAhead(){
		double radHeading = odo.getAng()/180.0*Math.PI;
		boolean res = false;
		double xReading = odo.getX() + Math.cos(radHeading)*DANGER_DIST;
		double yReading = odo.getY() + Math.sin(radHeading)*DANGER_DIST;
		if (xReading > 11*GRID_LENGTH || xReading < -GRID_LENGTH || yReading > 11*GRID_LENGTH || yReading < -GRID_LENGTH){
			res = true;
		}
		return res;
	}
	
	private double distanceTravelled(double x, double y){
		return Math.sqrt(Math.pow(odo.getX() - x, 2) + Math.pow(odo.getY() - y, 2));
	}
	
	/**
	 * remove X1 from the redzone before wishing to return to starting position
	 */
	public void removeX1(){
		redZoneXa.remove(0);
		redZoneXb.remove(0);
		redZoneYa.remove(0);
		redZoneYb.remove(0);
	}
	
	private boolean nearWall(){
		double x = odo.getX();
		double y = odo.getY();
		if ( x < -20 || x > 11*30.48+20 || y<-20 || y>11*30.48+20){
			return true;
		}
		return false;
	}
	
	private void evade(){
		double minAng;
		minAng = (Math.atan2(181 - odo.getY(), 181 - odo.getX())) * (180.0 / Math.PI);
		if (minAng < 0)
			minAng += 360.0;
		nav.turnTo(minAng, true);
	}
}
