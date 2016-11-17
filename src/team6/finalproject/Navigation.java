package team6.finalproject;

import lejos.hardware.motor.EV3LargeRegulatedMotor;



/**
 * Class that uses the {@link #odometer} class to create and travel along a coordinate plane.
 * <o>
 * Creates a (0,0) with localization and uses hardcoded measurements to travel along the competition surface
 * 
 * @author Andrei Ungur, Kael Du 
 * @version 0.1 
 */
public class Navigation extends Thread

{
	final static int FAST = 300, SLOW = 200, ACCELERATION = 4000; 
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	final static double ANG_ERR = 10;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private double waypointX, waypointY;
	private boolean navigating = true;
	private boolean turning = false;
	public boolean cancelled = false;

	/**
	 * Constructor for Navigation. 
	 * @param odo 		The <code>Odometer</code> object used to keep track of the robot's location
	 */
	public Navigation(Odometer odo) 
	{
		this.odometer = odo;

		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
		
		// set default destination
		waypointX = 0;
		waypointY = 0;
	}
	

	
	
	/**
	 * Required function for TimerListener
	 */
	@Override
	public void run(){
		double minAng;
		while (navigating){
			while ((Math.abs(waypointX - odometer.getX()) > CM_ERR || Math.abs(waypointY - odometer.getY()) > CM_ERR)
					&& !cancelled) {
				minAng = (Math.atan2(waypointY - odometer.getY(), waypointX - odometer.getX())) * (180.0 / Math.PI);
				if (minAng < 0)
					minAng += 360.0;
				if (Math.abs(odometer.getAng() - minAng) > ANG_ERR || Math.abs(odometer.getAng() - minAng) + ANG_ERR > 360.0){
					this.turnTo(minAng, true);
				}
				this.setSpeeds(FAST, FAST);
			
				}
		if(Math.abs(waypointX - odometer.getX()) < CM_ERR && Math.abs(waypointY - odometer.getY()) < CM_ERR){
			this.setSpeeds(0, 0);
			this.navigating = false;
			}
		}
	
	}

	/**
	 * Sets both of the motor speeds jointly
	 * @param lSpd 		a <code>float</code> representing left motor speed 
	 * @param rSpd 		a <code>float</code> representing right motor speed
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/**
	 * Sets both of the motor speeds jointly
	 * @param lSpd 		an <code>int</code> representing left motor speed
	 * @param rSpd 		an <code>int</code> representing right motor speed
	 */
	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/**
	 * Float both motors
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/** 
	 * Takes as arguments the x and y position in cm. Will travel to designated position, while constantly updating its heading.
	 * @param x 	<code>double</code> X coordinate value of destination.
	 * @param y 	<code>double</code> Y coordinate value of destination.
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while ((Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR)) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			if (Math.abs(odometer.getAng() - minAng) > ANG_ERR || Math.abs(odometer.getAng() - minAng) + ANG_ERR > 360.0){
				this.turnTo(minAng, true);
			}
			this.setSpeeds(FAST, FAST);
		}
		this.setSpeeds(0, 0);
		this.navigating = false;
	}
	
	/**
	 * return whether the robot is navigating
	 * @return
	 */
	public boolean navigating(){
		return this.navigating;
	}

	/** 
	 * Takes as arguments an angle in degrees and a boolean. Turns the robot to a given heading, used in conjunciton with {@link #travelTo(double, double)}
	 * @param angle 	The angle (in degrees) to which the robot should turn
	 * @param stop 		A <code>boolean</code> dictating whether or not the motors should stop upon completion of the turn
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getAng();
		this.turning = true;
		while (Math.abs(error) > DEG_ERR) {

			error = angle - this.odometer.getAng();

			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
			this.turning = false;
		}
	}
	
	/** 
	 * Takes as arguments an angle in degrees and a boolean. Turns the robot to a given heading, used in conjunciton with {@link #travelTo(double, double)}
	 * @param angle 	The angle (in degrees) to which the robot should turn
	 * @param stop 		A <code>boolean</code> dictating whether or not the motors should stop upon completion of the turn
	 */
	public void revisedTurnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getAng();
		this.turning = true;
		this.cancelled = true;

		error = angle - this.odometer.getAng();
		
		if (error > 180){
			leftMotor.rotate(convertDistance(odometer.getWheelRadius(), odometer.getTrack(), 360 - error),true);
			rightMotor.rotate(-convertDistance(odometer.getWheelRadius(), odometer.getTrack(), 360 - error),false);
		} else {
			leftMotor.rotate(-convertDistance(odometer.getWheelRadius(), odometer.getTrack(), error),true);
			rightMotor.rotate(convertDistance(odometer.getWheelRadius(), odometer.getTrack(), error),false);
			
		}

		
		this.turning = false;
	}
	
	/**
	 * return whether the robot is turning
	 * @return
	 */
	public boolean turing(){
		return this.turning;
	}

	/**
	 * Makes the robot go forward a set distance in cm.
	 * @param distance 		The <code>double</code> distance in cm to go forward
	 */
	public void goForward(double distance) 
	{
		//Robot rotates forward until distance to object is below a threshold
		/*this.leftMotor.rotate(convertDistance(2.1, distance), false);
		this.rightMotor.rotate(convertDistance(2.1, distance), false);*/
		double x = odometer.getX();
		double y = odometer.getY();
		while(Math.hypot(odometer.getX()-x,odometer.getY()-y) < Math.abs(distance))
		{
			if (distance>0){
				this.setSpeeds(SLOW,SLOW);
			} else {
				this.setSpeeds(-SLOW,-SLOW);
			}
			
		}
		
		this.setSpeeds(0,0);
	}
	
	/**
	 * Makes the robot travel forward indefinitely until stopped/interrupted.
	 */
	public void goForward(){
		this.leftMotor.forward();
		this.rightMotor.forward();	
	}
	
	/**
	 * Used with {@link #goForward(double)} to convert distance to scale.
	 * @param radius 		the <code>double</code> radius of the wheel
	 * @param distance 		the <code>double</code> distance parameter passed by the odometer
	 * @param angle			the <code>double</code> angle to turn 
	 * @return 				the converted <code>int</code> distance value for traveling purposes 
	 */
	private static int convertDistance(double radius, double width, double angle){
		return (int) ( width * angle / 2 / radius);
	}
	
	/**
	 * Set the destionation waypoints
	 * @param x
	 * @param y
	 */
	public void setWaypoints(double x, double y){
		this.waypointX = x;
		this.waypointY = y;
	}


}
