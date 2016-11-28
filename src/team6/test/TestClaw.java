package team6.test;

import team6.finalproject.Navigation;
import team6.finalproject.ObjectSearch;
import team6.finalproject.Odometer;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class TestClaw {
	
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor clawMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	private static final int CLAW_SPEED = 200;
	private static final int CLAW_ACCELERATION = 3000;
	public static final double WHEEL_RADIUS = 2.15;
	public static final double TRACK = 15.6;
	
	public static void main(String args[]) {
		Odometer odo = new Odometer(leftMotor, rightMotor, 30, true, WHEEL_RADIUS, TRACK);
		Navigation nav = new Navigation(odo);
		ObjectSearch clawHandle = new ObjectSearch(odo,nav,null,null,clawMotor,null);

		clawMotor.setSpeed(CLAW_SPEED);
		clawMotor.setAcceleration(CLAW_ACCELERATION);
		
		clawHandle.handleBlock(true);
	
		(new Thread(){
			public void run(){
				SquareDriver.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
			}
		}).start();
		

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		clawHandle.handleBlock(false);
		System.exit(0);
	}

}
