package team6.finalproject;

public class CountdownTimer extends PausableTimerListener {
	
	private boolean TimeUp = false;
	private int numLoops = 0;
	
	private static final int LOOPS_PER_SECOND = 20;
	private static final int MAX_TRAVEL_TIME = 240; //4 minutes of travel time

	@Override
	public void timedOut() {
		numLoops++;
		if (numLoops>=LOOPS_PER_SECOND*MAX_TRAVEL_TIME) {
			TimeUp = true;
		}
		this.stop();
	}
	
	public boolean isTimeUp() {
		return TimeUp;
	}
	
}
