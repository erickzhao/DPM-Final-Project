package team6.finalproject;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
/**
 * Navigation Class
 * <p>
 * Uses the {@link #odometer} class to create and travel along a coordinate plane.
 * Creates a (0,0) with localization and uses hardcoded measurements to travel along the competition surface
 * 
 * @author Andrei Ungur, Myriam Ayad 
 * @version 0.1 
 */
public class Navigation 
{
	final static int FAST = 300, SLOW = 200, ACCELERATION = 4000; 
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	//Determines side which head faces
	public static int headSide;

	/**
	 * Constructor for navigation. 
	 * @param odo The odometer object used to keep track of the robot's location
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
	}

	/**
	 * Sets both of the motor speeds jointly
	 * @param lSpd Left motor speed -- <code>float</code>
	 * @param rSpd Right motor speed -- <code>float</code>
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
	 * @param lSpd Left motor speed -- <code>int</code>
	 * @param rSpd Right motor speed -- <code>int</code>
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
	 * Takes as arguments the x and y position in cm. Will travel to designated position, while constantly updating it's heading
	 * @param x X coordinate of destination
	 * @param y Y coordinate of destination
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			this.setSpeeds(FAST, FAST);
		}
		this.setSpeeds(0, 0);
	}

	/** 
	 * Takes as arguments an angle in degrees and a boolean. Turns the robot to a given heading, used in conjunciton with {@link #travelTo(double, double)}
	 * @param angle The angle (in degrees) to which the robot should turn
	 * @param stop A <code>boolean</code> dictating whether or not the motors should stop upon completion of the turn
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getAng();

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
		}
	}

	/**
	 * Makes the robot go forward a set distance in cm
	 * @param distance The distance in cm to go forward
	 */
	public void goForward(double distance) 
	{
		//Robot rotates forward until distance to object is below a threshold
		this.leftMotor.rotate(convertDistance(2.1, distance), false);
		this.rightMotor.rotate(convertDistance(2.1, distance), false);
		double x = odometer.getX();
		double y = odometer.getY();
		while(Math.hypot(odometer.getX()-x,odometer.getY()-y) < distance)
		{
			this.setSpeeds(SLOW,SLOW);
		}
		this.setSpeeds(0,0);
	}
	
	/**
	 * makes the robot travel forward until stopped/interrupted
	 */
	public void goForward(){
		this.leftMotor.forward();
		this.rightMotor.forward();	
	}
	
	/**
	 * used with {@link #goForward(double)} to convert distance to scale
	 * @param radius radius of the wheel
	 * @param distance distance parameter passed by the odometer
	 * @return the converted distance value for travelling purposes 
	 */
	private static int convertDistance(double radius, double distance){
		return (int) ((180.0*distance) / (Math.PI*radius));
	}
}
