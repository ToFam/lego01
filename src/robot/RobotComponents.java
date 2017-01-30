package robot;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public final class RobotComponents {

	private static EV3LargeRegulatedMotor left;
	private static EV3LargeRegulatedMotor right;
	private static EV3MediumRegulatedMotor medium;
	private static EV3UltrasonicSensor uv;
	
	private RobotComponents() {	}
	
	public static void initialise() {
		left = new EV3LargeRegulatedMotor(MotorPort.A);
		right = new EV3LargeRegulatedMotor(MotorPort.B);
		medium = new EV3MediumRegulatedMotor(MotorPort.C);
		uv = new EV3UltrasonicSensor(SensorPort.S1);
	}
	
	public static EV3LargeRegulatedMotor getLeftMotor() {
		return left;
	}

	public static EV3LargeRegulatedMotor getRightMotor() {
		return right;
	}

	public static EV3MediumRegulatedMotor getMediumMotor() {
		return medium;
	}

	public static EV3UltrasonicSensor getUV() {
		return uv;
	}
	
	
}
