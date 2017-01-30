package remoteTest;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.internal.ev3.EV3MotorPort;
import lejos.robotics.RegulatedMotor;

public class Main {

    static EV3LargeRegulatedMotor leftMotor = Motor.B;
    static EV3LargeRegulatedMotor rightMotor = Motor.C;
    
	public static void main(String[] args)
	{
		while ((Button.readButtons() & Button.ID_ESCAPE) == 0)
		{
			leftMotor.rotate(360);
			rightMotor.rotate(360);
		}
	}

}
