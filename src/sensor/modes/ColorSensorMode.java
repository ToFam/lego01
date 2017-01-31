package sensor.modes;

/**
 * An enum for all modes of the color sensor.
 * Available samples for the different modes:
 * 
 *  COLOR_ID:
 *    samples[0]: A color id
 *      The ids are defined as followed:
 *      -1:  None
 *       0:  Red
 *       1:  Green
 *       2:  Blue
 *       3:  Yellow
 *       6:  White
 *       7:  Black
 *       13: Brown
 *  RED:
 *    samples[0]: The amount of reflected red light. Value is between 0 and 1
 *  RGB:
 *    samlpes[0]: The red part. Value is between 0 and 1
 *    samples[1]: The green part. Value is between 0 and 1
 *    samples[2]: The blue part. Value is between 0 and 1
 *  AMBIENT:
 *    samples[0]: Amount of light. Value is between 0 and 1
 * 
 * @author Simon
 *
 */
public enum ColorSensorMode {
	COLOR_ID(0), RED(1), RGB(2), AMBIENT(3);
	
	private int identifier;
	private ColorSensorMode(int c0)
	{
		this.identifier = c0;
	}

	public int getIdf(){
		return this.identifier;
	}
}
