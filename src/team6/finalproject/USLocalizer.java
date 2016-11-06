package team6.finalproject;

import lejos.hardware.Sound;

public class USLocalizer {
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private Navigation navigator;
	private double THRESHOLD = 30.0;
	private float speed = 175;

	public USLocalizer(Odometer odo) {
		this.odo = odo;
	}

	public void doLocalization() {
		double angleA, angleB;	//Latched on angles
		float distance;
		
		//Setup navigation
		navigator=new Navigation(odo);
		
		//Falling Edge navigation
		//Setup and display distance
		distance=getFilteredData();
		
		//For falling edge, the robot should be facing a wall first
		while(distance==255){
			distance=getFilteredData();
			navigator.setSpeeds(speed, -speed);
		}
		Sound.beep();
		//Robot saw a wall
		//Continue rotation in the same direction for a second to ensure that it faces a wall
		//And that the reading wasn't erronous
		navigator.setSpeeds(0,0);
		navigator.setSpeeds(speed,-speed);
		try{Thread.sleep(1000);}catch(Exception e){};
		
		//Stop movement, set starting position to the position when it faces the wall
		navigator.setSpeeds(0,0);
		odo.setPosition(new double [] {0.0, 0.0,90.0}, new boolean [] {true, true, true});
		try{Thread.sleep(1000);}catch(Exception e){};
		Sound.beep();
		//Robot sees a wall : Rotate until it doesn't
		while (distance<255){
			distance=getFilteredData();
			navigator.setSpeeds(speed, -speed);
		}
		
		//Stop and wait to show that it finished this part
		navigator.setSpeeds(0,0);
		navigator.setSpeeds(speed,-speed);
		try{Thread.sleep(200);}catch (Exception e){};
		Sound.beep();
		//Keep rotating until the robot sees a wall again, then latch the angle
		while (distance==255){
			navigator.setSpeeds(speed,-speed);
			distance=getFilteredData();
		}
		
		//Stop the motors again
		//Get AngleA
		angleA=odo.getAng();
		navigator.setSpeeds(0,0);
		navigator.setSpeeds(-speed,speed);
		try{Thread.sleep(200);}catch (Exception e){};
		Sound.beep();
		
		//Switch direction and wait until it sees no wall
		while (distance<255){
			navigator.setSpeeds(-speed, speed);
			distance=getFilteredData();
		}
		Sound.beep();
		//Stop and wait to show this part ended
		navigator.setSpeeds(0,0);
		navigator.setSpeeds(-speed,speed);
		try{Thread.sleep(200);}catch (Exception e){};
		
		//Keep rotating until the robot sees a wall, then latch the angle
		while (distance==255){
			navigator.setSpeeds(-speed,speed);
			distance=getFilteredData();
		}
		
		navigator.setSpeeds(0,0);
		try{Thread.sleep(200);}catch (Exception e){};
		
		//Get AngleB
		angleB=odo.getAng();
		
		//Compute original angle. Formula used is that from slides
		double finalAng;
		
		//Bigger values than 45/225 : overturn clockwise
		//Lower values than  ' ' '  : overturn counter-clockwise
		if (angleA<angleB){
			finalAng=35-(angleA+angleB)/2;
		} else {
			finalAng=215-(angleA+angleB)/2;
		}
		finalAng=wrapAngle(finalAng);
		Sound.beep();
		//Rotate robot to 90, which was its "initial" position
		navigator.turnTo(90,true);
		
		//Rotate it back from "90" to the real (0,0)
		finalAng=360-finalAng; //"Navigation" doesn't handle negative angles, so we wrap it around
		
		//Turn to 0 degrees, and set the position
		navigator.turnTo(finalAng, true);
		odo.setPosition(new double [] {0.0, 0.0,0.0}, new boolean [] {true, true, true});
		Sound.beepSequenceUp();
	}
	
	/*
	 * Filtered Data
	 * @return filtered distance
	 */
	private float getFilteredData() {
		//Filter out faulty 255 data
		int filter=0;

		//Fetch data
		float distance = UltrasonicPoller.getDistance();			
		
		//Check five times if the data is accurate
		while (filter<5 && distance==255){
			filter++;
			distance=UltrasonicPoller.getDistance();
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
}
