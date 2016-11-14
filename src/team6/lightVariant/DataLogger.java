package lightVariant;

import java.io.*;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;



public class DataLogger extends Thread{

	
	private SampleProvider lightSensor;
	private float[] lightData;
	private EV3LargeRegulatedMotor leftMotor;
	float value;
	int COUNT_MIN = 99;
	
	DataLogger (SampleProvider lightSensor, float[] lightData, EV3LargeRegulatedMotor leftMotor){
		this.lightSensor = lightSensor;
		this.lightData = lightData;
		this.leftMotor = leftMotor;
	}
	
    String theWord="";

	@Override
	public void run(){
		try{
		    PrintWriter writer = new PrintWriter("BrightLightSensorDataCollection.txt", "UTF-8");
		    int count = 0;
		    
			while (leftMotor.isMoving() || count <= COUNT_MIN) {
						
				value= getData();
				theWord = String.valueOf(value);
				writer.print(theWord + "\r\n");
				count++;
			}
		    writer.println();
		    writer.close();
		    Sound.beepSequence();
		} catch (Exception e) {
		   // do something
		}
	}
	
	public float getData() {
		lightSensor.fetchSample(lightData, 0);
		float res = lightData[0];
		try { Thread.sleep(32); } catch(Exception e){}		// Poor man's timed sampling
		return res;
	}
}
