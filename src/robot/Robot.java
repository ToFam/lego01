package robot;

import sensor.modes.ColorSensorMode;
import sensor.modes.GyroSensorMode;

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
	    this.setSpeed(speed, speed);
	}
	
    public void setSpeed(float speedLeft, float speedRight) {
        float max = Math.min(RobotComponents.inst().getLeftMotor().getMaxSpeed(), RobotComponents.inst().getRightMotor().getMaxSpeed());
        RobotComponents.inst().getLeftMotor().setSpeed(max * speedLeft);
        RobotComponents.inst().getRightMotor().setSpeed(max * speedRight);
        
        //this.forward();
    }
    
    /**
     * 
     * @param direction 1 is drive full left, -1 is drive full right. 0 is straight
     */
    public void setSpeedInDriveDirection(float direction)
    {
    	float left = (-1f * direction) >= 0f? (-1f * direction) : 0f;
    	float right = (1f * direction) >= 0f? (1f * direction) : 0f;
    	
    	setSpeed(left, right);
    }


    public void forward() {
        RobotComponents.inst().getLeftMotor().backward();
        RobotComponents.inst().getRightMotor().backward();
    }
    
    public void backward() {
        RobotComponents.inst().getLeftMotor().forward();
        RobotComponents.inst().getRightMotor().forward();
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

	
	private float turnOnSpot_angleToSlowDown = 50f;
	private float turnOnSpot_slowDownSpeedFactor = 0.4f;
	private float turnOnSpot_stopBeforeAngle = 4f;
	/**
	 * Turn the Robot around its axis.
	 * 
	 * @param degree Makes the Robot turn by the given amount.
	 */
	public void turnOnSpot(float degree)
	{
		float oldSpeedL = RobotComponents.inst().getLeftMotor().getSpeed();
		float oldSpeedR = RobotComponents.inst().getRightMotor().getSpeed();
		//RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
		float gyroValue = RobotComponents.inst().getGyroSensor().sample()[0];
		float goalValue = gyroValue + degree;
		boolean mustBeGreater = gyroValue < goalValue;
		
		float addition = mustBeGreater ? -turnOnSpot_stopBeforeAngle : turnOnSpot_stopBeforeAngle;
		goalValue += addition;
		
		
		if (mustBeGreater)
		{
	        RobotComponents.inst().getLeftMotor().forward();
			RobotComponents.inst().getRightMotor().backward();
		}
		else
		{
	        RobotComponents.inst().getLeftMotor().backward();
			RobotComponents.inst().getRightMotor().forward();
		}

		boolean setSpeed = false;
		while (RobotComponents.inst().getGyroSensor().sample()[0] < goalValue == mustBeGreater)
		{
			if (setSpeed == false && Math.abs(RobotComponents.inst().getGyroSensor().sample()[0] - goalValue) < turnOnSpot_angleToSlowDown)
			{
				setSpeed = true;
		        RobotComponents.inst().getLeftMotor().stop(true);
				RobotComponents.inst().getRightMotor().stop(true);
				RobotComponents.inst().getLeftMotor().setSpeed(oldSpeedL * turnOnSpot_slowDownSpeedFactor);
				RobotComponents.inst().getRightMotor().setSpeed(oldSpeedR * turnOnSpot_slowDownSpeedFactor);
				
				if (mustBeGreater)
				{
			        RobotComponents.inst().getLeftMotor().forward();
					RobotComponents.inst().getRightMotor().backward();
				}
				else
				{
			        RobotComponents.inst().getLeftMotor().backward();
					RobotComponents.inst().getRightMotor().forward();
				}
			}
		}
		
        RobotComponents.inst().getLeftMotor().stop(true);
		RobotComponents.inst().getRightMotor().stop(true);

		RobotComponents.inst().getLeftMotor().setSpeed(oldSpeedL);
		RobotComponents.inst().getRightMotor().setSpeed(oldSpeedR);
		
	}
	
	
	
	
	
	
	
	
	/**
	 * Sensors
	 */
	
	
	public void setColorMode(ColorSensorMode mode)
	{
		RobotComponents.inst().getColorSensor().setMode(mode.getIdf());
	}
	
	
	
	
}
