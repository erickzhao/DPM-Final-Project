package team6.test;

import team6.finalproject.ObjectSearch;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class TestClaw {
	
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor clawMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3MediumRegulatedMotor usMotor = new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	private static final int CLAW_SPEED = 300;
	private static final int CLAW_ACCELERATION = 2000;
	
	public static void main(String args[]) {
		ObjectSearch clawHandle = new ObjectSearch(null,null,null,null,clawMotor);

		clawMotor.setSpeed(CLAW_SPEED);
		clawMotor.setAcceleration(CLAW_ACCELERATION);
		
		clawHandle.pickUpBlock();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		clawHandle.releaseBlock();
		
	}

}
