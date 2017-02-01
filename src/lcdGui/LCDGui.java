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
		String spaces = "";
		for (int i = 0; i < 18 - line.length(); i++)
		{
			spaces += " ";
		}
		LCD.drawString(line + spaces, 0, 0);
	}
	
	public void writeLine(String line, int row)
	{
		String spaces = "";
		for (int i = 0; i < 18 - line.length(); i++)
		{
			spaces += " ";
		}
		LCD.drawString(line + spaces, 0, row);
	}
	
	public void logData(float x, float y)
	{
		
	}
}
