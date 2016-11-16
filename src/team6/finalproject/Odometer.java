package team6.finalproject;
/* File: Odometer.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * 
 * Class which controls the odometer for the robot
 * 
 * Odometer defines cooridinate system as such...
 * 
 * 					90Deg:pos y-axis
 * 							|
 * 							|
 * 							|
 * 							|
 * 180Deg:neg x-axis------------------0Deg:pos x-axis
 * 							|
 * 							|
 * 							|
 * 							|
 * 					270Deg:neg y-axis
 * 
 * The odometer is initialized to 90 degrees, assuming the robot is facing up the positive y-axis
 * 
 */

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Class that creates a coordinate plane to allow the robot to understand where it is and to simplify navigation.
 * @author Sean Lawlor, Andrei Ungur 
 * @version 1.0
 */
public class Odometer extends PausableTimerListener {

	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private double leftRadius, rightRadius, width;
	private double x, y, theta;
	private double[] oldDH, dDH;
	/**
	 * Constructor for Odometer.
	 * @param leftMotor 		the <code>EV3LargeRegulatedMotor</code> that is our left motor
	 * @param rightMotor 		the <code>EV3LargeRegulatedMotor</code> that is our right motor
	 * @param INTERVAL 			the <code>int</code> that is our polling interval
	 * @param autostart 		the <code>boolean</code> telling us to start
	 * @param wheelRadius 		the <code>double</code> that is the radius of the wheels
	 * @param robotWidth 		the <code>double</code> that is the length of robot's chassis
	 */
	public Odometer (EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, int INTERVAL, boolean autostart, double wheelRadius, double robotWidth) 
	{

		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;

		//Calibrated values
		this.rightRadius = wheelRadius;
		this.leftRadius = wheelRadius;
		this.width = robotWidth;

		this.x = 0.0;
		this.y = 0.0;
		this.theta = 90.0;
		this.oldDH = new double[2];
		this.dDH = new double[2];
	}
	/** 
	 * Calculates displacement and heading of the robot.
	 * @param data 		a <code>double</code> array representing our input data.
	 */
	private void getDisplacementAndHeading(double[] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();

		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) * Math.PI / 360.0;
		data[1] = (rightTacho * rightRadius - leftTacho * leftRadius) / width;
	}

	/**
	 * Recompute the odometer values using the displacement and heading changes every <code>Timer</code> loop. 
	 */
	public void timedOut() {
		this.getDisplacementAndHeading(dDH);
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];

		// update the position in a critical region
		synchronized (this) {
			theta += dDH[1];
			theta = fixDegAngle(theta);

			x += dDH[0] * Math.cos(Math.toRadians(theta));
			y += dDH[0] * Math.sin(Math.toRadians(theta));
		}

		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
	}

	/**
	 * Getter for X value
	 * @return 		a <code>double</code> X value to be used with navigation methods.
	 */
	public double getX() {
		synchronized (this) {
			return x;
		}
	}

	/**
	 * Getter for Y value
	 * @return 		a <code>double</code> Y value to be used with navigation methods.
	 */
	public double getY() {
		synchronized (this) {
			return y;
		}
	}

	/**
	 * Getter for theta value
	 * @return 		a <code>double</code> theta value to be used with navigation methods
	 */
	public double getAng() {
		synchronized (this) {
			return theta;
		}
	}

	/**
	 * Set position in the position array
	 * @param position 		a <code>double</code> array representing our position by holding x and y coordinates
	 * @param update 		a <code>boolean</code> array signalling if an update is needed or not
	 */
	public void setPosition(double[] position, boolean[] update) {
		synchronized (this) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	/** 
	 * Getter for the total position array
	 * @param position 		the <code>double</code> array holding the x, y, and theta values for the odometer
	 */
	public void getPosition(double[] position) {
		synchronized (this) {
			position[0] = x;
			position[1] = y;
			position[2] = theta;
		}
	}
	/**
	 * Getter for position
	 * <o>
	 * but in a synchronized block to return a new position.
	 * @return 		position <code>double</code> array holding the x, y, and theta values for the odometer
	 */
	public double[] getPosition() {
		synchronized (this) {
			return new double[] { x, y, theta };
		}
	}

	/**
	 * Getter for both motors
	 * @return 		the <code>EV3LargeRegulatedMotor</code> array holding both our left and right motors (to stay synchronized)
	 */
	public EV3LargeRegulatedMotor [] getMotors() {
		return new EV3LargeRegulatedMotor[] {this.leftMotor, this.rightMotor};
	}
	/**
	 * Getter for left motor
	 * @return 		the <code>EV3LargeRegulatedMotor</code> that is our left motor.
	 */
	public EV3LargeRegulatedMotor getLeftMotor() {
		return this.leftMotor;
	}
	/**
	 * Getter for right motor
	 * @return 		the <code>EV3LargeRegulatedMotor</code> that is our right motor.
	 */
	public EV3LargeRegulatedMotor getRightMotor() {
		return this.rightMotor;
	}
	
	public double getWheelRadius(){
		return this.leftRadius;
	}
	
	public double getTrack(){
		return this.width;
	}
	
	// static 'helper' methods
	/** 
	 * Helper method to convert angle into a usable, positive value
	 * @param angle 	a <code>double</code> angle to be converted/fixed
	 * @return 			a <code>double</code> fixed angle
	 */
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}
	
	/**
	 * This method ensures the robot turns the minimum angle when going to a new heading
	 * @param a 	the <code>double</code> first angle
	 * @param b 	the <code>double</code> second angle
	 * @return 		The minimum <code>double</code> angle to turn to
	 */
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
}
