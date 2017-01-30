package robot;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * 
 * The <code>RobotComponents</code> class serves as an Interface
 * for all components currently being used. The components are
 * changed manually.
 * 
 * @author	Team AndreasBot: Adrian
 *
 */
public final class RobotComponents {

	private static EV3LargeRegulatedMotor left;
	private static EV3LargeRegulatedMotor right;
	private static EV3MediumRegulatedMotor medium;
	private static EV3UltrasonicSensor uv;
	
	/**
	 * Default private constructor.
	 */
	private RobotComponents() {	}
	
	/**
	 * Instantiates all members with a new value.
	 * This method has to be called initially or
	 * else all getters will return <code>null</code>!
	 */
	public static void initialize() {
		left = new EV3LargeRegulatedMotor(MotorPort.A);
		right = new EV3LargeRegulatedMotor(MotorPort.B);
		medium = new EV3MediumRegulatedMotor(MotorPort.C);
		uv = new EV3UltrasonicSensor(SensorPort.S1);
	}
	
	/**
	 * Returns the left motor.
	 * @return The left motor.
	 */
	public static EV3LargeRegulatedMotor getLeftMotor() {
		return left;
	}

	/**
	 * Returns the right motor.
	 * @return The right motor.
	 */
	public static EV3LargeRegulatedMotor getRightMotor() {
		return right;
	}

	/**
	 * Returns the medium Motor.
	 * @return The medium Motor.
	 */
	public static EV3MediumRegulatedMotor getMediumMotor() {
		return medium;
	}

	/**
	 * Returns the ultrasonic sensor.
	 * @return the ultrasonic sensor.
	 */
	public static EV3UltrasonicSensor getUV() {
		return uv;
	}
	
	
}
