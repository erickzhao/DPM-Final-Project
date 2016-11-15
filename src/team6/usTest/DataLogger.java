
package team6.usTest;

import java.io.*;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;



public class DataLogger extends Thread{

	
	private SampleProvider usSensor;
	private float[] usData;
	private String name;
	
	
	DataLogger (SampleProvider usSensor, float[] usData, String name){
		this.usSensor = usSensor;
		this.usData = usData;
		this.name = name;
	}
	
	
	float distance;
    String theWord="";
    int count = 0;
    float MAX_DISTANCE = 100;
	
	@Override
	public void run(){
		try{
		    PrintWriter writer = new PrintWriter("Interference"+name+".txt", "UTF-8");
		    
		    
			while (count<=6000) {
						
				distance= getFilteredData();
				theWord = String.valueOf(distance);
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
	
	public float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		int aux = (int) (usData[0]*10000.0);
		float res = (float) (aux/100.0);
		if (res > MAX_DISTANCE) res = MAX_DISTANCE;
		try { Thread.sleep(50); } catch(Exception e){}		// Poor man's timed sampling
		return res;
	}
}
