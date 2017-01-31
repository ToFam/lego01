package main;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;

/**
 * 
 * @author	Team AndreasBot: Simon
 *
 */
public class ColorTest {

	private static EV3ColorSensor colorSens = new EV3ColorSensor(SensorPort.S1);
	
	public static void main(String[] args)
	{
		LCD.drawString("ColorID: ", 0,0);
		
		while (cancelProgram() == false)
		{
			LCD.drawString(colorSens.getColorID() + " ", 9, 0);
		}
	}
	
	private static boolean cancelProgram()
	{
		return (Button.readButtons() & Button.ID_ESCAPE) != 0;
	}

}
