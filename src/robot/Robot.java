package robot;

import lcdGui.LCDGui;
import lejos.hardware.lcd.LCD;
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
public class Robot 
{
    public static final int SENSOR_ANGLE = 130;
    
    private float speedLeft = 0.f;
    private float speedRight = 0.f;
	
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
        this.speedLeft = max * speedLeft;
        this.speedRight = max * speedRight;
        RobotComponents.inst().getLeftMotor().setSpeed(this.speedLeft);
        RobotComponents.inst().getRightMotor().setSpeed(this.speedRight);
    }
    
    /**
     * 
     * @param direction 1 is drive full left, -1 is drive full right. 0 is straight
     */
    public void steer(float direction)
    {   
    	float left = Math.min(speedLeft, speedLeft * (direction - 1) * (-1));
    	float right = Math.min(speedRight, speedRight * (direction + 1));

        RobotComponents.inst().getLeftMotor().setSpeed(left);
        RobotComponents.inst().getRightMotor().setSpeed(right);
    }
    
    /**
     * Additional factor to make the whole thing slower
     * @param direction 1 is drive full left, -1 is drive full right. 0 is straight
     */
    public void steerFacSimonTest(float direction)
    {
    	float right = (direction * -1 + 1f) > 1f ? 1f : (direction * -1 + 1f);
    	float left = (direction + 1f) > 1f ? 1f : (direction + 1f);
    	
    	setSpeed(left * speedLeft, right * speedRight);
    }
    
    public void steerFacSimonSpot(float direction, float speed)
    {
    	float right = (direction * -1 + 1f) > 1f ? 1f : (direction * -1 + 1f);
    	float left = (direction + 1f) > 1f ? 1f : (direction + 1f);
    	
    	setSpeed((left >= 0f ? left : -left) * speed, (right >= 0f ? right : -right) * speed);
    	
    	if (right >= 0f)
    	{
            RobotComponents.inst().getRightMotor().backward();
    	}
    	else
    	{
            RobotComponents.inst().getRightMotor().forward();
    	}
    	if (left >= 0f)
    	{
            RobotComponents.inst().getLeftMotor().backward();
    	}
    	else
    	{
            RobotComponents.inst().getLeftMotor().forward();
    	}
    }
    
    public void rotateMiddle(int deg) {
        RobotComponents.inst().getMediumMotor().rotate(deg);
    }
    
    public void move(int deg) 
    {
        //RobotComponents.inst().getLeftMotor().synchronizeWith(new RegulatedMotor[]{RobotComponents.inst().getRightMotor()});
        //RobotComponents.inst().getLeftMotor().startSynchronization();
        RobotComponents.inst().getLeftMotor().rotate(deg, true);
        RobotComponents.inst().getRightMotor().rotate(deg, true);
        //RobotComponents.inst().getLeftMotor().endSynchronization();

        //RobotComponents.inst().getLeftMotor().startSynchronization();
        RobotComponents.inst().getLeftMotor().waitComplete();
        RobotComponents.inst().getRightMotor().waitComplete();
        //RobotComponents.inst().getLeftMotor().endSynchronization();*/
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
		RobotComponents.inst().getLeftMotor().stop(true);
		RobotComponents.inst().getRightMotor().stop(true);
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
	
	public void startTurnOnSpot(boolean left) 
	{
	    if (!left) 
	    {
            RobotComponents.inst().getLeftMotor().backward();
            RobotComponents.inst().getRightMotor().forward();
	    }
	    else
	    {
            RobotComponents.inst().getLeftMotor().forward();
            RobotComponents.inst().getRightMotor().backward();
	    }
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
	    // DEBUG
	    // RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
	    // LCDGui.clearLCD();
	    
        RobotComponents.inst().getLeftMotor().setSpeed(speedLeft);
        RobotComponents.inst().getRightMotor().setSpeed(speedRight);
		float gyroValue = RobotComponents.inst().getGyroSensor().sample()[0];
		float goalValue = gyroValue + degree;
		boolean goalGreater = gyroValue < goalValue;
		
		float addition = goalGreater ? -turnOnSpot_stopBeforeAngle : turnOnSpot_stopBeforeAngle;
		goalValue += addition;
		
		if (goalGreater)
		{
		    startTurnOnSpot(true);
		}
		else
		{
            startTurnOnSpot(false);
		}

		boolean setSpeed = false;
		while ((goalGreater && gyroValue < goalValue) || (!goalGreater && gyroValue > goalValue))
		{
		    // DEBUG
		    // LCD.drawString(String.valueOf(gyroValue), 4, 2);
		    // LCD.drawString(String.valueOf(goalValue), 4, 4);
		    
			if (setSpeed == false && Math.abs(gyroValue - goalValue) < turnOnSpot_angleToSlowDown)
			{
				setSpeed = true;
		        RobotComponents.inst().getLeftMotor().stop(true);
				RobotComponents.inst().getRightMotor().stop(true);
				RobotComponents.inst().getLeftMotor().setSpeed(speedLeft * turnOnSpot_slowDownSpeedFactor);
				RobotComponents.inst().getRightMotor().setSpeed(speedRight * turnOnSpot_slowDownSpeedFactor);

		        if (goalGreater)
		        {
		            startTurnOnSpot(true);
		        }
		        else
		        {
		            startTurnOnSpot(false);
		        }
			}
			
			gyroValue = RobotComponents.inst().getGyroSensor().sample()[0];
		}
		
        RobotComponents.inst().getLeftMotor().stop(true);
		RobotComponents.inst().getRightMotor().stop(true);

		RobotComponents.inst().getLeftMotor().setSpeed(speedLeft);
		RobotComponents.inst().getRightMotor().setSpeed(speedRight);
		
	}
	
	
	
	
	
	
	
	
	/**
	 * Sensors
	 */
	
	
	public void setColorMode(ColorSensorMode mode)
	{
		RobotComponents.inst().getColorSensor().setMode(mode.getIdf());
	}
	
	
	
	
}
