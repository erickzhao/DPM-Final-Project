package team6.finalproject;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import lejos.utility.TimerListener;
import lejos.utility.Timer;

//UltrasonicPoller gets data from the US and can output it through
//getDistance. It runs every 50 seconds as it implements TimerListener.

public class LightPoller implements TimerListener{
	private Timer timer;
	private final int refreshRate = 50;
	
	private SampleProvider light;
	private float[] lightData;
	private static float lightValue;
	private static float currentLight;
	private static float previousLight = -1;
	private static double significantPercentThreshold = 20;
	
	public LightPoller(SampleProvider light, float[] lightData) {
		this.light = light;
		this.lightData = lightData;
		this.timer = new Timer(refreshRate, this);
		timer.start();
	}
	
	// start the timer
	public void timedOut() {
		light.fetchSample(lightData,0);						// acquire data
		lightValue=lightData[0]*100;						// extract from buffer, cast to int
	}
	
	//"distance" is sometimes accessed in a static way from outside this class
	public static boolean blackLine(){
		currentLight=lightValue;	
		if(100*Math.abs(currentLight - previousLight)/previousLight > significantPercentThreshold){
			if (currentLight < previousLight){
				Sound.beep();
				return true;
			}
		}
		previousLight = currentLight;
		return false;
	}
	public static float getReading(){
		return lightValue;
	}
}
