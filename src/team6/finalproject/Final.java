package team6.finalproject;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

/* TODO:
 *  1)	Get Odometry && Correction to work well
 *  2)	Get Navigation to work well
 *  3)	Get Search to work well
 */


public class Final {

	/*
	 * Resources :
	 * Motors
	 * > Port A:	Right
	 * > Port B:	Light
	 * > Port C:	Arm
	 * > Port D:	Left
	 * Sensors
	 * > Port S1:	UltraSonic
	 * > Port S2:	None
	 * > Port S3:	None
	 * > Port S4:	Color
	 */
	
	 private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	 private static final EV3LargeRegulatedMotor lightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	 private static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	 private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	 private static final Port usPort = LocalEV3.get().getPort("S1");  
	 private static final Port colorPort = LocalEV3.get().getPort("S4"); 

	
	public static void main(String[] args) {
		System.out.println("Hello world!");
	}

}
