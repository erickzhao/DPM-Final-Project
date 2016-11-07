package team6.finalproject;
import lejos.hardware.Sound;

/**
 * Light Localizer class
 * Localizes the robot to (0,0) by using the light sensor to detect black lines 
 * and performing calculation on the angles at which the lines were detected to determine 
 * the robot's true position on the coordinate plane, updating the odometer, and then travelling to true (0,0)
 * 
 * @author Myriam
 * @version 0.1
 */
public class LightLocalizer 
{
	private Odometer odo;
	private boolean lineCrossed; //Determines if a line was crossed
	private Navigation navigate;
	private double LStoWB; //Distance from the Light Sensor to the Wheel Base
	private int lineCount = 0; //Amount of lines crossed			
	private double [] saveThetas;
	private double xDist;
	private double yDist;
	private float speed=175;

	/**
	 * Constructor for the Light Localizer
	 * @param odo		the <code>Odometer</code> object that will be used to determine the robot's position
	 * @param LStoWB	the distance from the light sensor to the wheel base 
	 */
	public LightLocalizer(Odometer odo, double LStoWB) 
	{
		this.odo = odo;
		this.lineCrossed = false;
		this.navigate = new Navigation(odo);
		this.LStoWB = LStoWB;
		this.saveThetas = new double[4];
	}

	/**
	 * Localizes the robot using the light sensor by detecting the 4 black lines, 
	 * getting the odometer angles at the point each line is detected,
	 * and uses these values to calculate the offset of the robot from (0,0), and then travel to (0,0)
	 */
	public void doLocalization() 
	{
		
		//The robot is facing 0 degrees when this code executes
		//The default position is in negative X and Y
		//We navigate the robot to, approximately, (0,0)
		
		navigate.turnTo(45, true);
		
		//Await for light sensor to touch a line
		while(!lineCrossed)
		{
			navigate.setSpeeds(speed, speed); 
			
			if(LightPoller.blackLine())
			{
				lineCrossed = true;
				Sound.beep();
				navigate.setSpeeds(0, 0);
				try{Thread.sleep(500);}catch(Exception e){};
			}
		}
		
		lineCrossed = false;
		//Advance the robot by the LStoWB distance
		//This adds precision since our light sensor has less chances of being on (0,0)
		navigate.goForward(LStoWB);
		
		//Get the angle readings of the four lines on which the robot is positioned
		navigate.setSpeeds(-speed, speed);
		while(lineCount < 4)
		{
			if(LightPoller.blackLine())
			{
				lineCrossed = true;
				Sound.beep();
				saveThetas[lineCount] = odo.getAng();
				lineCount++;
			}
			//If a line is crossed, keep recording samples until the angle is no longer crossed
			while(lineCrossed)
			{
				if(!LightPoller.blackLine())
				{
					lineCrossed = false;
				}
			}
		}
		
		//Robot got all four angles; stop its movement while the X and Y offset values are computed
		navigate.setSpeeds(0, 0);
		xDist = -this.LStoWB * Math.cos(Math.toRadians(Math.abs(saveThetas[3] - saveThetas[1]) / 2));
		yDist = -this.LStoWB * Math.cos(Math.toRadians(Math.abs(saveThetas[2] - saveThetas[0]) / 2));
		
		//Wait
		try{Thread.sleep(2000);}catch(Exception e){};
		
		// Now we need to correct the heading
		double angleCorrection;
		angleCorrection = 90 - Math.abs((saveThetas[1]-saveThetas[3])/2) - saveThetas[3];
				
		odo.setPosition(new double[]{xDist,yDist,odo.getAng()+angleCorrection},new boolean[]{true,true,true});
		
		//Travel to origin
		navigate.travelTo(0, 0);
		navigate.setSpeeds(0,0);
		navigate.turnTo(0, true);
	}
}
