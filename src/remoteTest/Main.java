package remoteTest;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.internal.ev3.EV3MotorPort;
import lejos.robotics.RegulatedMotor;

public class Main {

    static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
    static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.B);
    
	public static void main(String[] args)
	{
		while ((Button.readButtons() & Button.ID_ESCAPE) == 0)
		{
			leftMotor.rotate(360);
			rightMotor.rotate(360);
		}
	}

}
