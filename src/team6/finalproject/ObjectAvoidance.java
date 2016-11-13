package team6.finalproject;

import lejos.hardware.motor.EV3MediumRegulatedMotor;

public class ObjectAvoidance {
	private double waypointX, waypointY;
	private Odometer odo;
	private Navigation nav;
	private EV3MediumRegulatedMotor usMotor;
	
	public ObjectAvoidance(double waypointX, double waypointY, Odometer odo, EV3MediumRegulatedMotor usMotor){
		this.waypointX = waypointX;
		this.waypointY = waypointY;
		this.odo = odo;
		this.nav = new Navigation(odo);	
		this.usMotor = usMotor;
	}
	
	
	
	
	// travel to the set destination (x,y)
	public void travelTo(double x, double y){
		nav.travelTo(x, y);
	}
	
	// 
	
	
}
