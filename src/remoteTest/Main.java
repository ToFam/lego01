package remoteTest;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;
import robot.Robot;
import robot.RobotComponents;
import robot.behavior.DefaultBehavior;
import robot.behavior.HumpbackBridgeBehavior;
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
        LCD.drawString(String.valueOf(mid), 2, y);
    }
    
    public static void gyrosTest()
    {
        float sample[] = {0, 0};
        RobotComponents.inst().getGyroSensor().getAngleAndRateMode().fetchSample(sample, 0);
        LCD.drawString(String.valueOf(sample[1]), 7, 2);
        LCD.drawString(String.valueOf(sample[0]), 7, 3);
    }
    
	public static void main(String[] args)
	{
		robot = new Robot(new HumpbackBridgeBehavior(0.5f));
		
		RobotComponents.inst().getGyroSensor().reset();
		
		while (Util.isRunning())
		{
			SampleProvider sP;
            
			sP = RobotComponents.inst().getColorSensor();
            sampelus(sP, 0);
            
            sP = RobotComponents.inst().getUV();
            sampelus(sP, 1);
            
            gyrosTest();
		    
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
				robot.curveLeft();
			}
			
			if (Util.isPressed(Button.ID_RIGHT))
			{
				robot.curveRight();
			}
			
			if (Util.isPressed(Button.ID_DOWN))
			{
				RobotComponents.inst().getLeftMotor().forward();
				RobotComponents.inst().getRightMotor().forward();
			}
			
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
		}
	}

}
