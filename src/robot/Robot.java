package robot;

import sensor.modes.ColorSensorMode;

/**
 * 
 * This class serves as an interface to control the Robot.
 * The class's core is the <code>RobotBehavior</code>,
 * which is a Strategy Design Pattern.
 * All methods in <code>Robot</code> call the corresponding
 * <code>RobotBehavior</code> method.
 * 
 * @author	Team AndreasBot: Adrian
 *
 */
public class Robot {

	
	/**
	 * 
	 * Constructor directly assigns the value.
	 * 
	 * @param behavior
	 */
	public Robot() {
	}
	
	public void setSpeed(float speed) {
	    float max = Math.min(RobotComponents.inst().getLeftMotor().getMaxSpeed(), RobotComponents.inst().getRightMotor().getMaxSpeed());
	    RobotComponents.inst().getLeftMotor().setSpeed(max * speed);
        RobotComponents.inst().getRightMotor().setSpeed(max * speed);
	}
	
    public void setSpeed(float speedLeft, float speedRight) {
        float max = Math.min(RobotComponents.inst().getLeftMotor().getMaxSpeed(), RobotComponents.inst().getRightMotor().getMaxSpeed());
        RobotComponents.inst().getLeftMotor().setSpeed(max * speedLeft);
        RobotComponents.inst().getRightMotor().setSpeed(max * speedRight);
    }


    public void forward() {
        RobotComponents.inst().getLeftMotor().forward();
        RobotComponents.inst().getRightMotor().forward();
    }
    
    public void backward() {
        RobotComponents.inst().getLeftMotor().backward();
        RobotComponents.inst().getRightMotor().backward();
    }
	
	/**
	 * The Robot stops in place.
	 */
	public void stop() {
		RobotComponents.inst().getLeftMotor().stop();
		RobotComponents.inst().getRightMotor().stop();
	}

	/**
	 * The Robot moves in a curved line.
	 * @param direction Direction of the curve. 
	 */
	public void curveLeft() {
	}

	/**
	 * The Robot moves in a curved line.
	 * @param direction Direction of the curve. 
	 */
	public void curveRight() {
	}

	/**
	 * Turn the Robot around its axis.
	 * 
	 * @param degree Makes the Robot turn by the given amount.
	 */
	public void turnOnSpot(float degree)
	{
		RobotComponents.inst().getGyroSensor().setMode(0);
		float gyroValue = RobotComponents.inst().getGyroSensor().sample()[0];
		float goalValue = gyroValue + degree;
        RobotComponents.inst().getLeftMotor().forward();
		RobotComponents.inst().getRightMotor().stop();
	}
	
	
	
	
	
	
	
	
	/**
	 * Sensors
	 */
	
	
	public void setColorMode(ColorSensorMode mode)
	{
		RobotComponents.inst().getColorSensor().setMode(mode.getIdf());
	}
	
	
	
	
}
