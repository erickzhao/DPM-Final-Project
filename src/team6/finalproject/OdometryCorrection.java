package team6.finalproject;

import lejos.hardware.Sound;

/**
 * Class that corrects odometer values while the robot is traveling to waypoints (i.e. moving in a straight line).
 * <o>
 * Works by snapping the odometer's position to a grid line's X or Y position when passing over it.
 * 
 * @author Erick Zhao
 * @version 0.1
 * @see Odometer
 */

public class OdometryCorrection extends PausableTimerListener {
	
	//variables
	private Odometer odometer;
	
	private static final double SENSOR_TO_CENTRE = 9.5;
	private static final double GRID_WIDTH = 30.48;
	private static final double ODOMETER_ERROR_THRESHOLD = 1;

	/**
	 * Constructor for Odometer correction.
	 * @param odometer	the <code>Odometer</code> object running on a separate </code>Timer</code> that is to be corrected
	 */
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	/**
	 * Checks if the odometer reading can be corrected every <code>Timer</code> loop and executes the correction
	 * whenever it is needed.
	 */
	@Override
	public void timedOut() {
		if(LightPoller.blackLine()){
			correctOdometerPosition();
		}
	}

	/**
	 * Corrects the odometer's reading whenever a black line is detected.
	 * 
	 * This works by snapping the odometer to a value of the grid when it passes over a black line.
	 * The odometer reading snaps to the value of the nearest black grid line.
	 */
	private void correctOdometerPosition(){
		
		double positionX = odometer.getX()+getHorizontalSensorToCentreDistance();
		double positionY = odometer.getY()+getVerticalSensorToCentreDistance();
		
		if (isRobotNearGridLine(positionX)) {
			double actualPosition = getNearestGridLine(positionX)-getHorizontalSensorToCentreDistance();
			double[] position = {actualPosition,0,0};
			boolean[] update =  {true,false,false};
			odometer.setPosition(position,update);
			Sound.beepSequenceUp();
		}
		
		if (isRobotNearGridLine(positionY)) {
			double actualPosition = getNearestGridLine(positionY)-getVerticalSensorToCentreDistance();
			double[] position = {0,actualPosition,0};
			boolean[] update = {false,true,false};
			
			odometer.setPosition(position,update);
			Sound.beepSequence();
		}
	}
	
	/**
	 * Determines if the odometer reading one one of the two axes is close enough to a grid line
	 * to snap to it.
	 * @param position	the x or y position of the robot according to the odometer
	 * @return			the boolean value indicating if the odometer should snap to this grid line
	 */
	private boolean isRobotNearGridLine(double position) {
		
		double distanceFromLine = GRID_WIDTH-position%GRID_WIDTH; 
		if (position%GRID_WIDTH < 15){
			distanceFromLine = position%GRID_WIDTH;
		}
		
		if (Math.abs(distanceFromLine) <= ODOMETER_ERROR_THRESHOLD) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the position of the nearest grid line in either the x or y direction (depending on which axis'
	 * position is passed as a parameter).
	 * 
	 * This works by dividing the current position by the grid width to see which grid line it's closest to,
	 * and then multiplying that number by the grid width again to get that line's distance from the origin.
	 * 
	 * @param position	the x or y position of the robot according to the odometer
	 * @return			the corresponding <code>double</code> position of the nearest grid line
	 */
	private double getNearestGridLine(double position) {
		return (int)((position+ODOMETER_ERROR_THRESHOLD)/GRID_WIDTH)*GRID_WIDTH;
	}
	
	/**
	 * Gets the horizontal distance between the sensor and the centre of rotation.
	 * @return		a <code>double</code> representing the horizontal distance in cm
	 */
	private double getHorizontalSensorToCentreDistance() {
		return Math.cos(angleToRadians(odometer.getAng()))*SENSOR_TO_CENTRE;
	}
	
	/**
	 * Gets the vertical distance between the sensor and the centre of rotation.
	 * @return		a <code>double</code> representing the vertical distance in cm
	 */
	private double getVerticalSensorToCentreDistance() {
		return Math.sin(angleToRadians(odometer.getAng()))*SENSOR_TO_CENTRE;
	}
	
	/**
	 * Converts an angle from degrees to radians.
	 * @param angle	a <code>double</code> representing an angle in degrees
	 * @return		the equivalent angle in radians
	 */
	private double angleToRadians(double angle) {
		return 2*Math.PI*angle/360;
	}
	
}