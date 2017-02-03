package util.lcdGui;

import lejos.hardware.lcd.LCD;

public class LCDChooseList {

	private String[] elements = null;
	private int currentSelected = 0;
	private int currentOffset = 0;
	private int oldSelected = -1;
	private int oldOffset = -1;
	
	public LCDChooseList(String[] elements)
	{
		for (int i = 0; i < elements.length; i++)
		{
			if (elements[i].length() > 17)
			{
				elements[i] = elements[i].substring(0, 17);
			}
		}
		
		currentSelected = 0;
		currentOffset = 0;
		this.elements = elements;

        repaint();
	}
	
	public void repaint() {
	    LCDGui.clearLCD();

        for (int i = 0; i < LCDGui.LCDAmountOfRows; i++)
        {
            if (i + currentOffset < elements.length)
            {
                LCD.drawString(elements[i + currentOffset], 1, i);
            }
            else
            {
                LCD.clear(i);
            }
        }

        LCD.drawString(">", 0, currentSelected - currentOffset);
	}
	
	private void drawLCD()
	{
		if (oldOffset != currentOffset)
		{
			for (int i = 0; i < LCDGui.LCDAmountOfRows; i++)
			{
				if (i + currentOffset < elements.length)
				{
					LCD.drawString(elements[i + currentOffset], 1, i);
				}
				else
				{
					LCD.clear(i);
				}
			}
		}
		
		
		if (oldOffset != currentOffset || oldSelected != currentSelected)
		{
			//LCD.clear(0, oldSelected - oldOffset, 1);
			LCD.drawString(" ", 0, oldSelected - oldOffset);
		}
		
		LCD.drawString(">", 0, currentSelected - currentOffset);
		
		oldOffset = currentOffset;
		oldSelected = currentSelected;
	}
	
	public int getCurrentSelected()
	{
		return currentSelected;
	}
	
	public void moveOneUp()
	{
		currentSelected--;
		currentSelected = currentSelected < 0 ? 0 : currentSelected;
		
		if (currentSelected < currentOffset)
		{
			currentOffset = currentSelected;
		}
		
		drawLCD();
	}
	
	public void moveOneDown()
	{
		currentSelected++;
		currentSelected = currentSelected >= elements.length ? elements.length - 1 : currentSelected;
		
		if (currentSelected >= currentOffset + LCDGui.LCDAmountOfRows)
		{
			currentOffset = currentSelected - LCDGui.LCDAmountOfRows + 1;
		}
		
		drawLCD();
	}
	
	public String getCurrentElement()
	{
		return elements[currentSelected];
	}
}
