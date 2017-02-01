package robot;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import sensor.ColorSensor;
import sensor.GyroSensor;
import sensor.TouchSensorAThread;
import sensor.TouchSensorBThread;
import sensor.UVSensor;
import sensor.modes.ColorSensorMode;

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
	
	private ColorSensor color = null;
	private GyroSensor gyros = null;
	private TouchSensorAThread touchA = null;
	private TouchSensorBThread touchB = null;
	private UVSensor uv = null;
	
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
	public synchronized UVSensor getUV() {
	    if (uv == null)
	    {
	    	try
	    	{
		        uv = new UVSensor(new EV3UltrasonicSensor(SensorPort.S1));
		        uv.start();
	    	}
	    	catch (IllegalArgumentException exception)
	    	{
	    		LCD.clear();
	    		LCD.drawString("1No UV sensor found on S1", 0, 0);
	    		Sound.beep();
	    	}
	    }
		return uv;
	}
	
	public synchronized ColorSensor getColorSensor() {
	    if (color == null)
	    {
	    	try
	    	{
	    		color = new ColorSensor(new EV3ColorSensor(SensorPort.S2));
	    		color.start();
	    	}
	    	catch (IllegalArgumentException exception)
	    	{
	    		LCD.clear();
	    		LCD.drawString("2No color sensor found on S2", 0, 0);
	    		Sound.beep();
	    	}
	    }
	    return color;
	}
	
	public synchronized TouchSensorAThread getTouchSensorA() {
	    if (touchA == null)
	    {
	    	try
	    	{
		        touchA = new TouchSensorAThread(new EV3TouchSensor(SensorPort.S3));
		        touchA.start();
	    	}
	    	catch (IllegalArgumentException exception)
	    	{
	    		LCD.clear();
	    		LCD.drawString("3No touch sensor found on S3", 0, 0);
	    		Sound.beep();
	    	}
	    }
	    return touchA;
	}
	
    public synchronized TouchSensorBThread getTouchSensorB() {
        if (touchB == null)
        {
	    	try
	    	{
	            touchB = new TouchSensorBThread(new EV3TouchSensor(SensorPort.S4));
	            touchB.start();
	    	}
	    	catch (IllegalArgumentException exception)
	    	{
	    		LCD.clear();
	    		LCD.drawString("4No touch sensor found on S4", 0, 0);
	    		Sound.beep();
	    	}
        }
        return touchB;
    }
    
    public synchronized GyroSensor getGyroSensor() {
        if (gyros == null)
        {
	    	try
	    	{
	            gyros = new GyroSensor(new EV3GyroSensor(SensorPort.S3));
	            gyros.start();
	    	}
	    	catch (IllegalArgumentException exception)
	    	{
	    		LCD.clear();
	    		LCD.drawString("3No gyros sensor found on S3", 0, 0);
	    		Sound.beep();
	    	}
        }
        return gyros;
    }
}
