package main;

import lcdGui.LCDGui;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;
import robot.Robot;
import robot.RobotComponents;
import state.ParcourState;
import state.TestState;
import util.Util;
/**
 * 
 * @author	Team AndreasBot: Simon,
 * 			Team AndreasBot: Tobias,
 * 			Team AndreasBot: Adrian
 *
 */
public class Main {

    private Robot robot;
    private ParcourState state;
    
    static boolean ultraLooksDown = false;
    
    public static void sampelus(SampleProvider sP, int y)
    {
        float[] samples = new float[sP.sampleSize()];
        sP.fetchSample(samples, 0);
        float mid = 0f;
        for (int i = 0; i < samples.length; i++)
        {
            mid += samples[i];
        }
        mid /= samples.length;
        LCD.drawString(String.valueOf(mid), 2, y);
    }
    
    public static void gyrosTest()
    {
        float sample[] = {0, 0};
        RobotComponents.inst().getGyroSensor().getAngleAndRateMode().fetchSample(sample, 0);
        LCD.drawString(String.valueOf(sample[1]), 7, 2);
        LCD.drawString(String.valueOf(sample[0]), 7, 3);
    }
    
    public void run()
    {
        robot = new Robot();
        LCDGui gui = new LCDGui();
        
        state = new TestState(robot, gui);
        state.init();
        
        while (Util.isRunning())
        {
            
            state.update();
            
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
    }
    
	public static void main(String[] args)
	{
		Button.ESCAPE.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(Key k) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void keyPressed(Key k) {
				System.exit(0);
			}
		});
		
	    Main m = new Main();
	    m.run();
	}

}
