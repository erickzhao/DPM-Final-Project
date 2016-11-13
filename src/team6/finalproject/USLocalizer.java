package team6.finalproject;

import team6.test.TestLocalize;
import lejos.hardware.Sound;

/**
 * Ultrasonic localizer class
 * Localizes the robot to the 0 degree angle heading using falling edge localization
 * and updates the Odometer heading accordingly
 * 
 * @author Andrei Ungur
 * @version 0.1
 */
public class USLocalizer {
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private UltrasonicPoller uspoll;
	private Navigation navigator;
	private double THRESHOLD = 30.0;
	private float speed = 350;

	/**
	 * Constructor for the UltraSonic Localizer
	 * @param odo		the <code>Odometer</code> object that will be used to determine the robot's position
	 */
	public USLocalizer(Odometer odo,UltrasonicPoller uspoll) {
		this.odo = odo;
		this.uspoll=uspoll;
	}

	/**
	 * Localizes the robot by first rotating until it is facing a wall,
	 * then rotating again and latching the angle at which it sees a wall,
	 * then rotating the other direction and latching the angle at which it again sees a wall. 
	 * These 2 angles are then used to determine the robot's true heading,
	 * and then turn it to true 0 degrees
	 */
	public void doLocalization() {
		double angleA, angleB;	//Latched on angles
		
		//Setup navigation
		navigator=new Navigation(odo);
		
		//Falling Edge navigation
		//For falling edge, the robot should be facing a wall first
		while(!seeWall()){
			navigator.setSpeeds(-speed, speed);
		}
		
		Sound.beep();
		//Robot saw a wall
		
		//Stop movement, set starting position to the position when it faces the wall
		navigator.setSpeeds(0,0);
		odo.setPosition(new double [] {0.0, 0.0,90.0}, new boolean [] {true, true, true});
		Sound.beep();
		//Robot sees a wall : Rotate until it doesn't
		while (seeWall()){
			navigator.setSpeeds(speed, -speed);
		}
		
		//Stop and wait to show that it finished this part
		navigator.setSpeeds(0,0);
		navigator.setSpeeds(speed,-speed);
		try{Thread.sleep(200);}catch (Exception e){};
		Sound.beep();
		//Keep rotating until the robot sees a wall again, then latch the angle
		while (!seeWall()){
			navigator.setSpeeds(speed,-speed);
		}
		
		//Stop the motors again
		
		navigator.setSpeeds(0,0);
		try{Thread.sleep(200);}catch (Exception e){};
		//Get AngleA
		angleA=odo.getAng();
		navigator.setSpeeds(-speed,speed);
		Sound.beep();
		
		//Switch direction and wait until it sees no wall
		while (seeWall()){
			navigator.setSpeeds(-speed, speed);
		}
		Sound.beep();
		
		//Stop and wait to show this part ended
		navigator.setSpeeds(0,0);
		navigator.setSpeeds(-speed,speed);
		try{Thread.sleep(200);}catch (Exception e){};
		
		//Keep rotating until the robot sees a wall, then latch the angle
		while (!seeWall()){
			navigator.setSpeeds(-speed,speed);
		}
		
		navigator.setSpeeds(0,0);
		try{Thread.sleep(200);}catch (Exception e){};
		
		//Get AngleB
		angleB=odo.getAng();
		
		//Compute original angle. Formula used is that from slides
		double finalAng = getEndAngle(angleA,angleB);

		finalAng=wrapAngle(finalAng);
		
		//Rotate robot to 90, which was its "initial" position
		navigator.turnTo(90,true);
		navigator.setSpeeds(0,0);
		try{Thread.sleep(100);}catch (Exception e){};
		
		//Turn to 0 degrees, and set the position
		navigator.turnTo(finalAng, true);
		navigator.setSpeeds(0,0);
		try{Thread.sleep(100);}catch (Exception e){};
		odo.setPosition(new double [] {0.0, 0.0,0.0}, new boolean [] {true, true, true});
		Sound.beepSequenceUp();
	}
	
	/**
	 * Filtered Data
	 * @return filtered distance
	 */
	private float getFilteredData() {
		//Filter out faulty 255 data
		int filter=0;

		//Fetch data
		float distance = uspoll.getDistance();			
		
		//Check five times if the data is accurate
		while (filter<5 && distance==255){
			filter++;
			distance=uspoll.getDistance();
		}
		filter=0;
		
		//Clip if distance is greater than hypotenuse of one square
		if (distance>THRESHOLD){
			return 255;
		}
		return distance;
	}
	
	/**
	 * Wraps angle around
	 * Angle of 361 becomes 1
	 * Angle of -1 becomes 359
	 * @param angle
	 * @return
	 */
	private double wrapAngle(double angle){
		if (angle<0){
			return 360+angle;
		} else if (angle>360){
			return angle-360;
		}
		return angle;
	}
	
	/**
	 * Calculates the angle heading offset of the robot using the angles at which the robot detected a wall
	 * @param a		the first angle that the robot has latched
	 * @param b		the second angle that the robot has latched
	 * @return		the angle that the odometer is off by
	 */
	private double getEndAngle(double a, double b) {
		if (a > b) {
			return ((a+b)/2 - 225);
		}
		return ((a+b)/2 - 45);
	}
	
	/**
	 * Uses ultrasonic sensor readings to determine whether or not a wall has been detected
	 * @return boolean true if wall is detected, false if wall is not detected
	 */
	private boolean seeWall(){
		if (getFilteredData()==255){
			return false;
		}
		return true;
	}
}
