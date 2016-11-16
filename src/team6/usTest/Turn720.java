package team6.usTest;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Turn720 extends Thread{
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int leftVelocity, rightVelocity;
	

	
	public Turn720 (int turningSpeed, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor){
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftVelocity = turningSpeed;
		this.rightVelocity = turningSpeed;
	}
	
	@Override
	public void run(){
		leftMotor.setSpeed(leftVelocity);
		rightMotor.setSpeed(rightVelocity);
		leftMotor.forward();
		rightMotor.backward();
	}
	
	
	
}
