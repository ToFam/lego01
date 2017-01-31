package util;

import lejos.hardware.Button;

/**
 * 
 * This class consists of a collection of useful methods.
 * 
 * @author	Team AndreasBot: Adrian
 *
 */
public final class Util {
	
	/**
	 * Default private constructor.
	 */
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
		return !isPressed(Button.ID_ESCAPE);
	}
	
	/**
	 * Calculate the average value of the float array ignoring infinite values.
	 * @param samples The array with float values.
	 * @return The arrays average value.
	 */
	public static int average(int[] samples) {
		float sum = 0.f;
		int weight = 0;
		
		for (int i = 0; i < samples.length; i++) {
			if (samples[i] != Integer.MIN_VALUE || samples[i] != Integer.MAX_VALUE) {
				sum += samples[i];
				weight++;
			}
		}
		
		return Math.round(sum / weight);
	}
	
	/**
	 * Calculate the average value of the float array ignoring infinite values.
	 * @param samples The array with float values.
	 * @return The arrays average value.
	 */
	public static float average(float[] samples) {
		float sum = 0.f;
		int weight = 0;
		
		for (int i = 0; i < samples.length; i++) {
			if (samples[i] != Float.NEGATIVE_INFINITY || samples[i] != Float.POSITIVE_INFINITY) {
				sum += samples[i];
				weight++;
			}
		}
		
		return sum / weight;
	}
}
