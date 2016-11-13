package team6.finalproject;

import lejos.utility.TimerListener;
import lejos.utility.Timer;

/**
 * Abstraction of a pausable leJos {@link TimerListener} interface.
 * <o>
 * Allows for any <code>TimerListener</code> to be instantiated and paused using the <code>start()</code>
 * and <code>stop()</code> methods within this class. Also instantiates every timer at a refresh rate of
 * 50 milliseconds.
 * 
 * @author	Erick Zhao
 * @version 0.1
 * @see		TimerListener
 * @see		Timer
 *
 */

public abstract class PausableTimerListener implements TimerListener {

	private Timer timer;
	private static final int REFRESH_RATE = 50;
	
	/**
	 * Constructor for Pausable timer listener.
	 * <o>
	 * Instantiates a new <code>Timer</code> with a refresh rate of 50 ms, but does not
	 * start it until the user decides to do so.
	 */
	public PausableTimerListener() {
		this.timer = new Timer(REFRESH_RATE, this);
	}
	
	/**
	 * Starts the <code>Timer</code>, which resumes the instance of this class.
	 */
	public void start() {
		if (this.timer != null)
			this.timer.start();
	}
	
	/**
	 * Stops the <code>Timer</code>, which pauses the instance of this class.
	 */
	public void stop() {
		if (this.timer != null)
			this.timer.stop();
	}
}
