package lightVariant;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class MoveForward extends Thread{

	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int speed, distance;
	private double WHEEL_RADIUS = 2.13;
	
	
	MoveForward(int speed, int distance, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor){
		this.speed = speed;
		this.distance = distance;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		
	}
	
	
	@Override
	public void run(){
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		leftMotor.rotateTo(distance2Degree(distance), true);
		rightMotor.rotateTo(distance2Degree(distance),false);
	}
	
	public int distance2Degree(int dist){
		double oneTurn = 2*Math.PI*this.WHEEL_RADIUS;
		return (int) (360*dist/oneTurn);
	}
}
