package robot;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public final class RobotComponents {

	private EV3LargeRegulatedMotor left;
	private EV3LargeRegulatedMotor right;
	private EV3MediumRegulatedMotor medium;
	private EV3UltrasonicSensor uv;
	
	public RobotComponents() {
		left = new EV3LargeRegulatedMotor(MotorPort.A);
		right = new EV3LargeRegulatedMotor(MotorPort.B);
		medium = new EV3MediumRegulatedMotor(MotorPort.C);
		uv = new EV3UltrasonicSensor(SensorPort.S1);
	}
	
	public EV3LargeRegulatedMotor getLeftMotor() {
		return left;
	}

	public EV3LargeRegulatedMotor getRightMotor() {
		return right;
	}

	public EV3MediumRegulatedMotor getMediumMotor() {
		return medium;
	}

	public EV3UltrasonicSensor getUV() {
		return uv;
	}
	
	
}
