package team6.finalproject;

import java.util.ArrayList;
import java.util.List;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Class that scans the field for objects and determines if objects are to be avoided or if they are to be collected.
 * 
 * @author Andrei Ungur, Erick Zhao
 * @version 0.1
 */

public class ObjectSearch {
	
	private Odometer odo;
	private final EV3LargeRegulatedMotor clawMotor;
	private Navigation nav;
	private float speed = 150;
	private UltrasonicPoller lowerpoll;
	private List<Float> obstacles = new ArrayList<Float>();
	private double THRESHOLD = 60;
	//If boardsize is 1 : It moves down one square at a time
	//If boardsize is 2 : It moves down 2 squares at a time (etc.)
	//For the final, since we're splitting 12 blocks in 3, boardsize = 4.
	//For the beta demo, we can split 8 blocks in 3 with boardsize = 8/3.
	private double BOARDSIZE = 1;
	private double initX,initY,initTheta,endzoneX,endzoneY; //We take info for the endzone through wi-fi
	private double sweepAng=90;
	private double distToObject; //Distance that the robot travels to inspect object
	private boolean sameObject=false; //Determines if the robot is looking at the same object
	private ObjectAvoidance oa;
	private static final int CLAW_SPEED = 200;
	private static final int CLAW_ACCELERATION = 3000;
	/**
	 * Constructor for Object Search.
	 */
	public ObjectSearch(Odometer odo, Navigation nav,UltrasonicPoller uspoll, ObjectAvoidance oa,EV3LargeRegulatedMotor claw) {
		this.odo = odo;
		this.nav = nav;
		this.lowerpoll = uspoll;
		this.oa = oa;
		this.clawMotor = claw;
		this.clawMotor.setSpeed(CLAW_SPEED);
		this.clawMotor.setAcceleration(CLAW_ACCELERATION);
	}
	/**
	 * Implements all the elements of the search into an algorithm.
	 */
	public void doSearch(){
		//The amount of time for which the search runs is five minutes.
		//After five minutes, this infinite loop would be interrupted and the robot
		//will return to (0,0).
		endzoneX = 60.96;
		endzoneY = 60.96;
		int wp=1;
		while(true){
			//Scan the next neighbourhood
			sweep();
			Sound.beep();
			travelToWaypoint(wp);
			nav.turnTo(0, true);
			wp++;
		}
	}
	
	/**
	 * Brings the robot to the end zone and back to the neighborhood it left from.
	 * It takes as input the end zones coordinates.
	 */
	public void bringToEndzone(double x, double y){
		initX=odo.getX();
		initY=odo.getY();
		initTheta=odo.getAng();
		//Bring block to endzone
		oa.travel(endzoneX, endzoneY);
		handleBlock(false);
		//Bring robot back to last neighborhood
		oa.travel(endzoneX, endzoneY);
		nav.turnTo(initTheta, true);
	}
	
	/**
	 * Sweeps the current neighborhood by rotating about a point, 
	 * keeping track of if objects are detected in the neighborhood
	 */
	public void sweep(){
		initX=odo.getX();
		initY=odo.getY();
		double currAng = odo.getAng();
		//Start sweeping counter-clockwise (I think it increases the angle)
		nav.setSpeeds(-speed,speed);
		while(currAng<sweepAng || currAng>(sweepAng+180)){
			//An object is seen
			if(lowerpoll.getDistance()<=THRESHOLD){
				Sound.beep();
				//Add to object list
				obstacles.add(new Float(currAng));
				//Inspect object
				inspectBlock();
				nav.setSpeeds(-speed,speed);
				//Continue turning as long as it sees the same object
				while(lowerpoll.getDistance()<=THRESHOLD){
					//If it turned for too big of an angle, check that it still sees the same object
					if((odo.getAng()-currAng)>Math.atan(22.5/distToObject)){
						//Next time the loop runs, it'll check if the object is the same
						break;
					} else {
						continue;
					}
				}
				//Stop, to show that the object isn't seen anymore
				nav.setSpeeds(0,0);
				try{Thread.sleep(100);}catch(Exception e){};
				//Continue turning
				nav.setSpeeds(-speed, speed);
			}
			currAng=odo.getAng();
		}
		nav.setSpeeds(0, 0);
	}
	
	/**
	 * Uses light sensor to determine if block is wooden or if block is blue styrofoam
	 */
	private void inspectBlock(/*double heading*/) {
		//The commented out code is to alternate between...
		//... either inspecting blocks directly, or after having stored them
		//nav.turnTo(heading,true);
		
		nav.goForward();
		
		while (!ColorPoller.isObject()) {
			continue;
		}
		nav.setSpeeds(0,0);
		
		if (ColorPoller.isBlock()) {
			Sound.beep();
			handleBlock(true);
			bringToEndzone(endzoneX,endzoneY);
		} else {
			saveObstacleToMap(odo.getX(), odo.getY());
			nav.goBackward();
			while(Math.hypot(Math.abs(initX-odo.getX()), Math.abs(initY-odo.getY()))>0.5){
				continue;
			}
			distToObject = Math.hypot(Math.abs(initX-odo.getX()),Math.abs(initY-odo.getY()));
			nav.setSpeeds(0,0);
		}
	}
	
	/**
	 * Moves the robot to the starting point for the next neighborhood to scan
	 * @param wayPoint	the <code>int</code> coordinate for start of next neighborhood to scan
	 */
	public void travelToWaypoint(int wayPoint) {
		switch (wayPoint){
		case 1:
		case 2:
			oa.travel(odo.getX()+BOARDSIZE*30.48, odo.getY());
			break;
		case 3:
		case 6:
			oa.travel(odo.getX(), odo.getY()+30.48);
			break;
		case 4: 
		case 5:
			oa.travel(odo.getX()-BOARDSIZE*30.48,odo.getY());
			break;
		case 7: 
		case 8:
			oa.travel(odo.getX()+BOARDSIZE*30.48,odo.getY());
			break;
		case 9:
			oa.travel(0, 0);
			nav.turnTo(0,true);
			break;
		}
			
	}
	
	/**
	 * Grasps a styrofoam block with the claw if "pickUp" is true.
	 * Releasses styrofoam block if "pickUp" is false.
	 */
	public void handleBlock(boolean pickUp) {
		if (pickUp){
			clawMotor.rotateTo(-180);
		} else {
			clawMotor.rotateTo(0);
		}
	}
	
	/**
	 * 
	 * @param x	the x coordinate currently read by the odometer
	 * @param y	the y coordinate currently read by the odometer
	 */
	private void saveObstacleToMap(double x, double y) {
		boolean grid[][] = new boolean[12][12];
		
		int gridX = (x>=0) ? ((int) x/30+1):0;
		int gridY = (y>=0) ? ((int) y/30+1):0;
		
		grid[gridX][gridY] = true;
	}
}
