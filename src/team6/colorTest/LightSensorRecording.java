package team6.colorTet;



	import lejos.hardware.Button;
	import lejos.hardware.ev3.LocalEV3;
	import lejos.hardware.lcd.TextLCD;
	import lejos.hardware.motor.EV3LargeRegulatedMotor;
	import lejos.hardware.port.Port;
	import lejos.hardware.sensor.EV3ColorSensor;
	import lejos.hardware.sensor.SensorModes;
	import lejos.robotics.SampleProvider;

	public class LightSensorRecording {

		static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
		private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
		private static final Port lightPort = LocalEV3.get().getPort("S2");
		private static final int WHEEL_SPEED = 150;
		private static final int DISTANCE = 90;
		
		public static void main(String[] args) {
			
			final TextLCD t = LocalEV3.get().getTextLCD();
			
			// Light Sensor setting
			@SuppressWarnings("resource")    
			SensorModes lightSensor = new EV3ColorSensor(lightPort);
			SampleProvider lightValue = lightSensor.getMode("Red");
			float[] lightData = new float[lightValue.sampleSize()];
			
			
			MoveForward mf = new MoveForward(WHEEL_SPEED, DISTANCE, leftMotor, rightMotor);
			DataLogger dl = new DataLogger(lightValue, lightData, leftMotor);
			
			t.drawString("Press ENTER!!!!", 0, 0);
			while(Button.waitForAnyPress() != Button.ID_ENTER);
			mf.start();
			dl.start();
					
			
			while(Button.waitForAnyPress() != Button.ID_ESCAPE);
			System.exit(0);
	

	}
}
