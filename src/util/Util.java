package util;

import lejos.hardware.Button;

public final class Util {
	
	private Util() {
		
	}
	
	/**
	 * Returns whether the Button is being pressed.
	 * 
	 * @param buttonID The Integer constant assigned to the Button
	 * @return Returns whether the Button is being pressed.
	 * 
	 */
	public static boolean isPressed(int buttonID) {
		return (Button.readButtons() & buttonID) != 0;
	}
	
	/**
	 * Returns the run condition for an infinite while loop.
	 * This method uses isPressed(Button.ID_ESCAPE) to determine the run condition.
	 * 
	 * @return Whether to continue iterating an infinite while loop.
	 */
	public static boolean isRunning() {
		return isPressed(Button.ID_ESCAPE);
	}
}
