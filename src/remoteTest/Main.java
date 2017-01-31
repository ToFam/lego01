package remoteTest;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;
import robot.Robot;
import robot.RobotComponents;
import robot.behavior.DefaultBehavior;
import util.Util;
/**
 * 
 * @author	Team AndreasBot: Simon,
 * 			Team AndreasBot: Tobias,
 * 			Team AndreasBot: Adrian
 *
 */
public class Main {

    private static Robot robot;
    
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
        LCD.drawString(String.valueOf(mid), 7, 0);
    }
    
	public static void main(String[] args)
	{
		robot = new Robot(new DefaultBehavior(0.5f));

		LCD.drawString("Running!", 0,0);
		
		while (Util.isRunning())
		{
			SampleProvider sP = RobotComponents.inst().getColorSensor();
			sampelus(sP, 0);
			
			sP = RobotComponents.inst().getUV();
			sampelus(sP, 1);
			
			if (Util.isPressed(Button.ID_UP))
			{
				robot.moveStraight();
			}
			
			if (Util.isPressed(Button.ID_ENTER))
			{
				robot.stop();
			}
			
			if (Util.isPressed(Button.ID_LEFT))
			{
				RobotComponents.inst().getLeftMotor().forward();
				RobotComponents.inst().getRightMotor().backward();
			}
			
			if (Util.isPressed(Button.ID_RIGHT))
			{
				RobotComponents.inst().getLeftMotor().backward();
				RobotComponents.inst().getRightMotor().forward();
			}
			
			if (Util.isPressed(Button.ID_DOWN))
			{
				RobotComponents.inst().getLeftMotor().forward();
				RobotComponents.inst().getRightMotor().forward();
			}
		}
	}

}
