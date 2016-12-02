package team6.finalproject;
/**
 * CountdownTimer Class
 * <p>
 * Class that acts as a timer throughout the allowed operational timeframe of the robot. 
 * Signals when it is time to begin heading back to the starting point to avoid disqualification
 * @author Kael Du
 * @version 1.0
 */
public class CountdownTimer extends PausableTimerListener {
	
	private boolean TimeUp = false;
	private int numLoops = 0;
	
	private static final int LOOPS_PER_SECOND = 20;
	private static final int MAX_TRAVEL_TIME = 240; //4 minutes of travel time
	
	/**
	 * measures the time spent and adjusts the boolean value to determine whether or not time is up
	 */
	@Override
	public void timedOut() {
		numLoops++;
		if (numLoops>=LOOPS_PER_SECOND*MAX_TRAVEL_TIME) {
			TimeUp = true;
		}
		this.stop();
	}
	/**
	 * @return a <code>boolean</code> value indicating whether time is up
	 */
	public boolean isTimeUp() {
		return TimeUp;
	}
	
}
