package team6.finalproject;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.Sound;

/* 
 * OdometryCorrection.java
 */

/*	POSSIBLE APPROACH:
 * 	1) Get X,Y values when it first hits a line, and when it hits a second line
 * 	2) Use that as deltaX and deltaY
 * 	3) If deltaY is 0, deltaX should be 30.48 and vice versa. If this isn't the
 * 	case, we know by how much the robot is off and we can correct both his trajectory
 * 	as well as the odometer readings.
 * 
 */
public class OdometryCorrection extends Thread {

	//variables
	private static final long CORRECTION_PERIOD = 10;
	private static final double SENSORDIST = 4.5; 			//distance from the sensor to the centre of rotation
	private static final double MINIMUMWHITEVALUE = 0.20; 	//the minimum value of RGB that could be called white.
	private Odometer odometer;

	//first and current brightnesses.
	private double firstBrightnessLevel;
	private double currBrightnessLevel;


	private double significantPercentThreshold = 20; //the percent difference in our reading to consider it a different color (used for reading black)

	//array to store the measured RGB values
	private float[] RGBValues = new float[3];

	private boolean reachedBlackLine = false;

	//variables to see if it is the first X or Y correction. It always starts in same first square (-15,-15)-->(15,15)
	private boolean isFirstXCorrection = true;
	private boolean isFirstYCorrection = true;

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		Sound.setVolume(100);
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		firstBrightnessLevel = -1; //start the first brightness at -1 (to know it is first reading)\
		
		while (true) {
			correctionStart = System.currentTimeMillis();
			//			
			//			//gets the RGB data we measure
			//			colorSensor.getRGBMode().fetchSample(RGBValues, 0); 
			//			//we define the brightness as the average of the magnitudes of R,G,B (really "Whiteness")
			//			currBrightnessLevel = (RGBValues[0] + RGBValues[1] + RGBValues[2]);
			//			
			//			/*
			//			 * If it is our first brightness level, we just set it to our measured
			//			 * Else, it is not our FIRST measurement, so we check to see if we hit a black line. 
			//			 */
			//			if (firstBrightnessLevel == -1){
			//				firstBrightnessLevel = currBrightnessLevel;
			//			}else{ 
			//				/*
			//				 * We have one condition for a black line:
			//				 * If the percent difference between our first measurement and now is greater
			//				 * than a set threshold.
			//				 */
			//				if(100*Math.abs(currBrightnessLevel - firstBrightnessLevel)/firstBrightnessLevel > significantPercentThreshold){
			//					//we have a significant change
			//					if(currBrightnessLevel < firstBrightnessLevel){
			//						//we've reached a black line!!!
			//						reachedBlackLine = true;
			//						Sound.beep();
			//					}
			//				}else{
			//					reachedBlackLine = false;
			//				}
			//				
			//if we've reached a black line, correct the position of the robot.
			if(LightPoller.blackLine() == true){
				correctOdometerPosition();
			}
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}



		// this ensure the odometry correction occurs only once every period

	}

	/*
	 * Corrects the odometer's position (is triggered on hitting a black line).
	 */
	private void correctOdometerPosition(){
		//variables
		double currX = odometer.getX();
		double currY = odometer.getY();
		int currT = (int) odometer.getAng();
		double correctedX = 0;
		double correctedY = 0;

		//we find the nearest right theta (call it 0 (0,180,360) or 90 (90,270)) to see whether
		//we're moving in X or Y.
		int robotDirection = findRightAngleRobotDirection(currT);

		/*
		 * If our direction is 0 (or 180,360), we're moving in X, correct X.
		 * If our direction is 90 (or 270), we're moving in Y, correct Y.
		 */
		if(robotDirection == 0){ 
			//get corrected X
			correctedX = findCorrectedX(currX);
			correctedY = currY;
			//subtract or add the sensor distance to the corrected value (depending on how we're moving)
			if (currX < correctedX){
				correctedX -= SENSORDIST;
			}else{
				correctedX += SENSORDIST;
			}

		}else if(robotDirection == 90){ 
			//get corrected Y
			correctedY = findCorrectedY(currY);
			correctedX = currX;
			//subtract or add the sensor distance to the corrected value (depending on how we're moving)
			if (currY < correctedY){
				correctedY -= SENSORDIST;
			}else{
				correctedY += SENSORDIST;
			}
		}
		//create an array for the position of our robot and set the values
		double[] position = new double[3];
		position[0] = correctedX;
		position[1] = correctedY;
		position[2] = 0;
		//create an array for what fields to update for our robot and set values
		boolean[] update = new boolean[3];
		update[0] = true;
		update[1] = true;
		update[2] = false;
		//now use those two arrays to set the position of the odometer (it is now corrected).
		odometer.setPosition(position,update);

	}
	/*
	 * Finds the "corrected" X value, assuming (0,0) is in the middle of the first square,
	 * and blocks are 30x30. 
	 */
	private double findCorrectedX(double x){
		double result = x;
		/*
		 * if it's the first correction, we know that X should be 15. otherwise find the nearest line value
		 * to where we are. If none are close enough, keep it the same (false reading).
		 */
		if(isFirstXCorrection){
			result = 15;
			isFirstXCorrection = false;
		}else{
			for(int i = 0; i < 4; i++){
				if(Math.abs(x - (15-SENSORDIST + 30*i)) < 12){
					result = 15 + 30*i;
					break;
				}
			}	
		}
		return result;
	}
	/*
	 * Finds the "corrected" Y value, assuming (0,0) is in the middle of the first square,
	 * and blocks are 30x30.
	 */
	private double findCorrectedY(double y){
		double result = y;
		/*
		 * if it's the first correction, we know that Y should be -15. otherwise find the nearest line value
		 * to where we are. If none are close enough, keep it the same (false reading).
		 */
		if(isFirstYCorrection){
			result = -15;
			isFirstYCorrection = false;
		}else{
			for(int i = 0; i < 4; i++){
				if(Math.abs(y + (15-SENSORDIST + 30*i)) < 12){
					result = (-1)*(15 + 30*i);
					break;
				}
			}
		}
		return result;
	}
	/*
	 * Finds the nearest right angle (0,90,180,270,etc) to the robots direction
	 * to determine if we are moving in the X or the Y. We call X 0 and Y 90.
	 */
	private int findRightAngleRobotDirection(int t){
		int result = 0;
		int allowedError = 5;
		/*
		 * checks to see if we are in the allowed range for any of the right angles.
		 */
		if (Math.abs(t) < allowedError || Math.abs(t-360) < allowedError || Math.abs(t+360) < allowedError || Math.abs(t-180) < allowedError || Math.abs(t+180) < allowedError){
			result = 0;
		}else if (Math.abs(t-90) < allowedError || Math.abs(t+90) < allowedError || Math.abs(t-270) < allowedError || Math.abs(t+270) < allowedError){
			result = 90;
		}

		return result;
	}

	//accessors used for displaying text on LCD.
	public boolean isReadingBlack(){
		return reachedBlackLine;
	}
	public double getR(){
		return RGBValues[0];
	}
	public double getG(){
		return RGBValues[1];
	}
	public double getB(){
		return RGBValues[2];
	}
	public double getBrightness(){
		return currBrightnessLevel;
	}
}