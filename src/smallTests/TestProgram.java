package smallTests;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;

/**
 * 
 * @author	Team AndreasBot: Simon
 *
 */
public class TestProgram {

	static EV3LargeRegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.A);
	static EV3LargeRegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.B);
	
	static EV3TouchSensor touch = new EV3TouchSensor(SensorPort.S4);
	
	public static void main(String[] args)
	{
		while (buttonPressed(Button.ID_ESCAPE) == false)
		{
			float[] samples = new float[touch.sampleSize()];
			touch.fetchSample(samples, 0);
			float mid = 0f;
			for (int i = 0; i < samples.length; i++)
			{
				mid += samples[i];
			}
			mid /= samples.length;
			
			LCD.drawString(String.valueOf(mid), 0, 0);
		}
	}

	private static boolean buttonPressed(int button)
	{
		return (Button.readButtons() & button) != 0;
	}
}
