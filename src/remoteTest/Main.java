package remoteTest;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;
import robot.Robot;
import robot.RobotComponents;
import robot.behavior.DefaultBehavior;
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
    
	public static void main(String[] args)
	{
		robot = new Robot(new DefaultBehavior(0.5f));

		LCD.drawString("Running!", 0,0);
		
		while (cancelProgram() == false)
		{
			/*SampleProvider sP = RobotComponents.inst().getUV().getDistanceMode();
			float[] samples = new float[sP.sampleSize()];
			sP.fetchSample(samples, 0);
			float mid = 0f;
			for (int i = 0; i < samples.length; i++)
			{
				mid += samples[i];
			}
			mid /= samples.length;
			LCD.drawString(String.valueOf(mid), 7, 0);*/
			
			
			if (buttonForwardPressed())
			{
				robot.moveStraight();
			}
			
			if (buttonEnterPressed())
			{
				robot.stop();
			}
			
			if (buttonPressed(Button.ID_LEFT))
			{
				RobotComponents.inst().getLeftMotor().forward();
				RobotComponents.inst().getRightMotor().backward();
			}
			
			if (buttonPressed(Button.ID_RIGHT))
			{
				RobotComponents.inst().getLeftMotor().backward();
				RobotComponents.inst().getRightMotor().forward();
			}
			
			if (buttonPressed(Button.ID_DOWN))
			{
				RobotComponents.inst().getLeftMotor().forward();
				RobotComponents.inst().getRightMotor().forward();
			}
		}
	}
	
	/**
	 * Returns the main Robot.
	 * @return The main Robot.
	 */
	public static Robot getRobot() {
		return robot;
	}
	
	private static boolean cancelProgram()
	{
		return (Button.readButtons() & Button.ID_ESCAPE) != 0;
	}
	
	private static boolean buttonForwardPressed()
	{
		return (Button.readButtons() & Button.ID_UP) != 0;
	}
	
	private static boolean buttonEnterPressed()
	{
		return (Button.readButtons() & Button.ID_ENTER) != 0;
	}
	
	private static boolean buttonPressed(int button)
	{
		return (Button.readButtons() & button) != 0;
	}

}
