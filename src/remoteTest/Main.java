package remoteTest;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class Main {

    static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
    static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);
    //static EV3MediumRegulatedMotor mediumMotor = new EV3MediumRegulatedMotor(MotorPort.C);
    //static EV3UltrasonicSensor ultraSens = new EV3UltrasonicSensor(SensorPort.S1);
    
    static boolean ultraLooksDown = false;
    
	public static void main(String[] args)
	{
		leftMotor.setSpeed(leftMotor.getMaxSpeed() * 0.5f);
		rightMotor.setSpeed(rightMotor.getMaxSpeed() * 0.5f);
		//mediumMotor.setSpeed(mediumMotor.getMaxSpeed() * 1.0f);

		LCD.drawString("Running!", 0,0);
		
		while (cancelProgram() == false)
		{
			/*SampleProvider sP = ultraSens.getDistanceMode();
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
				leftMotor.backward();
				rightMotor.backward();
			}
			
			if (buttonEnterPressed())
			{
				leftMotor.stop();
				rightMotor.stop();
			}
			
			if (buttonPressed(Button.ID_LEFT))
			{
				leftMotor.forward();
				rightMotor.backward();
			}
			
			if (buttonPressed(Button.ID_RIGHT))
			{
				leftMotor.backward();
				rightMotor.forward();
			}
			
			if (buttonPressed(Button.ID_DOWN))
			{
				leftMotor.forward();
				rightMotor.forward();
			}
		}
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
