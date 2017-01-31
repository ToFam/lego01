package robot;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
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

	private EV3LargeRegulatedMotor left = null;
	private EV3LargeRegulatedMotor right = null;
	private EV3MediumRegulatedMotor medium = null;
	
	private EV3UltrasonicSensor uv = null;
	private EV3ColorSensor color = null;
	private EV3TouchSensor touchA = null;
	private EV3TouchSensor touchB = null;
	
	private static RobotComponents instance;
	
	/**
	 * Default private constructor.
	 */
	private RobotComponents() {	}
	
	public static RobotComponents inst() {
	    if (instance == null)
	        instance = new RobotComponents();
	    return instance;
	}
	
	/**
	 * Returns the left motor.
	 * @return The left motor.
	 */
	public EV3LargeRegulatedMotor getLeftMotor() {
		if (left == null)
		    left = new EV3LargeRegulatedMotor(MotorPort.A); 
	    return left;
	}

	/**
	 * Returns the right motor.
	 * @return The right motor.
	 */
	public EV3LargeRegulatedMotor getRightMotor() {
	    if (right == null)
	        right = new EV3LargeRegulatedMotor(MotorPort.B);
		return right;
	}

	/**
	 * Returns the medium Motor.
	 * @return The medium Motor.
	 */
	public EV3MediumRegulatedMotor getMediumMotor() {
	    if (medium == null)
	        medium = new EV3MediumRegulatedMotor(MotorPort.C);
		return medium;
	}

	/**
	 * Returns the ultrasonic sensor.
	 * @return the ultrasonic sensor.
	 */
	public EV3UltrasonicSensor getUV() {
	    if (uv == null)
	        uv = new EV3UltrasonicSensor(SensorPort.S1);
		return uv;
	}
	
	public EV3ColorSensor getColorSensor() {
	    if (color == null)
	        color = new EV3ColorSensor(SensorPort.S2);
	    return color;
	}
	
	public EV3TouchSensor getTouchSensorA() {
	    if (touchA == null)
	        touchA = new EV3TouchSensor(SensorPort.S3);
	    return touchA;
	}
	
    public EV3TouchSensor getTouchSensorB() {
        if (touchB == null)
            touchB = new EV3TouchSensor(SensorPort.S4);
        return touchB;
    }
}
