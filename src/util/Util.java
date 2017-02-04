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
	
	private final static float transmission = 20.f / 12.f;
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
	
	public static int getEffectiveAngle(float angle) {
		return (int) (angle * transmission);
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
	
	public static float average(float[] samples, int elementsCount) {
		float sum = 0.f;
		int weight = 0;
		
		for (int i = 0; i < Math.min(samples.length, elementsCount); i++) {
			if (samples[i] != Float.NEGATIVE_INFINITY || samples[i] != Float.POSITIVE_INFINITY) {
				sum += samples[i];
				weight++;
			}
		}
		
		return sum / weight;
	}
	
	private static float howMuchOnLine_RMin = 0.1f;
	private static float howMuchOnLine_RMax = 0.29f;
	private static float howMuchOnLine_GMin = 0.1f;
	private static float howMuchOnLine_GMax = 0.29f;
	private static float howMuchOnLine_BMin = 0.1f;
	private static float howMuchOnLine_BMax = 0.29f;
	/**
	 * 
	 * @param rgbValues
	 * @return A value between 0 and 1. Where 0 is black and 1 is line
	 */
	public static float howMuchOnLine(float[] rgbValues)
	{
		float facR = (1.0f / (howMuchOnLine_RMax - howMuchOnLine_RMin)) * (rgbValues[0] - howMuchOnLine_RMin);
		float facG = (1.0f / (howMuchOnLine_GMax - howMuchOnLine_GMin)) * (rgbValues[1] - howMuchOnLine_GMin);
		float facB = (1.0f / (howMuchOnLine_BMax - howMuchOnLine_BMin)) * (rgbValues[2] - howMuchOnLine_BMin);
		
		float midVal = (facR + facG + facB) / 3.0f;
		
		midVal = midVal < 0f ? 0f : (midVal > 1f ? 1f : midVal);
		
		return midVal;
	}
}
