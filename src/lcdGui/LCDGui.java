package lcdGui;

import lejos.hardware.lcd.LCD;

public class LCDGui
{
	public static int LCDAmountOfRows = 8;
	
	private int varsAmount = 0;
	private int amountOfCols = 1;
	private int rowToDrawSeperator = 0;
	private int[] colPoss;
	private Object[] vars;
    
    public static void clearLCD()
    {
        LCD.clear();
        
        for (int i = 0; i < LCDGui.LCDAmountOfRows; i++) {
            LCD.clear(i);
        }
    }
	
	public LCDGui(int amountOfVariables, int amountOfColumns)
	{
		varsAmount = amountOfVariables;
		amountOfCols = amountOfColumns;
		if (amountOfCols >= 1 && amountOfCols <= 3)
		{
			rowToDrawSeperator = (varsAmount / amountOfCols);
		}
		else
		{
			amountOfCols = 3;
		}
		colPoss = new int[amountOfCols];
		colPoss[0] = 0;
		switch (amountOfCols)
		{
		case 2:
			colPoss[1] = 9;
			break;
		case 3:
			colPoss[1] = 6;
			colPoss[2] = 12;
		}
		vars = new Object[varsAmount];
		
		clearLCD();
	}
	
	private void drawLCD()
	{
		
	}
	
	private void drawVars()
	{
		for (int i = 0; i < vars.length; i++)
		{
			LCD.drawString(String.valueOf(vars[i]), colPoss[i % colPoss.length], i / amountOfCols);
		}
	}
	
	public void setVarValue(int index, Object var)
	{
		if (index >= 0 && index < vars.length)
		{
			vars[index] = var;
			LCD.drawString(String.valueOf(vars[index]) + "  ", colPoss[index % colPoss.length], index / amountOfCols);
		}
	}
	
	public void writeLine(String line)
	{
		LCD.scroll();
		LCD.drawString(line, 0, LCDGui.LCDAmountOfRows - 1);
		drawVars();
		drawLCD();
	}
}
