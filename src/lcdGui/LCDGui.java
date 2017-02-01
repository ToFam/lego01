package lcdGui;

import lejos.hardware.lcd.LCD;

public class LCDGui
{
	public LCDGui()
	{
		LCD.clear();
	}
	
	public void writeLine(String line)
	{
		LCD.drawString(line, 0, 0);
	}
	
	public void logData(float x, float y)
	{
		
	}
}
