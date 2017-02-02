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
public class Robot 
{
    public static final int SENSOR_ANGLE = 130;
	
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
    public void steer(float direction)
    {
        float lMax = RobotComponents.inst().getLeftMotor().getSpeed();
        float rMax = RobotComponents.inst().getRightMotor().getSpeed();
        
    	float left = Math.min(lMax, lMax * (direction - 1) * (-1));
    	float right = Math.min(rMax, rMax * (direction + 1));
    	
    	setSpeed(left, right);
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
        RobotComponents.inst().getLeftMotor().waitComplete();
        RobotComponents.inst().getRightMotor().waitComplete();
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
	    if (left) 
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
	    
		float oldSpeedL = RobotComponents.inst().getLeftMotor().getSpeed();
		float oldSpeedR = RobotComponents.inst().getRightMotor().getSpeed();
		float gyroValue = RobotComponents.inst().getGyroSensor().sample()[0];
		float goalValue = gyroValue + degree;
		boolean mustBeGreater = gyroValue < goalValue;
		
		float addition = mustBeGreater ? -turnOnSpot_stopBeforeAngle : turnOnSpot_stopBeforeAngle;
		goalValue += addition;
		
		if (mustBeGreater)
		{
		    startTurnOnSpot(true);
		}
		else
		{
            startTurnOnSpot(false);
		}

		boolean setSpeed = false;
		while (gyroValue < goalValue == mustBeGreater)
		{
		    // DEBUG
		    // LCD.drawString(String.valueOf(gyroValue), 4, 2);
		    // LCD.drawString(String.valueOf(goalValue), 4, 7);
		    
			if (setSpeed == false && Math.abs(gyroValue - goalValue) < turnOnSpot_angleToSlowDown)
			{
				setSpeed = true;
		        RobotComponents.inst().getLeftMotor().stop(true);
				RobotComponents.inst().getRightMotor().stop(true);
				RobotComponents.inst().getLeftMotor().setSpeed(oldSpeedL * turnOnSpot_slowDownSpeedFactor);
				RobotComponents.inst().getRightMotor().setSpeed(oldSpeedR * turnOnSpot_slowDownSpeedFactor);

		        if (mustBeGreater)
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
