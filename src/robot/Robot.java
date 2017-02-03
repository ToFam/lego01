package robot;

import robot.action.Backward;
import robot.action.Forward;
import robot.action.Move;
import robot.action.RobotAction;
import robot.action.Stop;
import robot.action.TurnOnSpot;
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
    
    private float speedLeft = 0.f;
    private float speedRight = 0.f;
    
    private RobotAction action;
    
    private RobotAction forw, backw, stop;
	
	public Robot() {
	    forw = new Forward();
	    backw = new Backward();
	    stop = new Stop();
	    action = stop;
	    action.start();
	    RobotComponents.inst().getMediumMotor().resetTachoCount();
	}
	
	public boolean finished() 
	{
	    return action.finished();
	}
	
	public void update()
	{
	    action.update();
	}
	
	public void raiseUV() {
		if (Math.abs(RobotComponents.inst().getMediumMotor().getTachoCount()) < 2) {
			RobotComponents.inst().getMediumMotor().rotate(-210, true);
		}
	}
	
	public boolean UVisUp() {
		return Math.abs(RobotComponents.inst().getMediumMotor().getTachoCount()) < 2;
	}
	
	public void lowerUV() {
		if (Math.abs(RobotComponents.inst().getMediumMotor().getTachoCount()) > 208) {
			RobotComponents.inst().getMediumMotor().rotate(210, true);
		}
	}
	
	public boolean UVisDown() {
		return Math.abs(RobotComponents.inst().getMediumMotor().getTachoCount()) > 208;
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
        
        //System.out.println("Right=" + String.valueOf(right) + " Left=" + String.valueOf(left) + " Direction=" + String.valueOf(direction));
        
        
        //setSpeed(left * speedLeft, right * speedRight);
        setSpeed(left, right);
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
        action = new Move(deg);
        action.start();
    }

    public void forward() 
    {
        action = forw;
        action.start();
    }
    
    public void backward() 
    {
        action = backw;
        action.start();
    }
	
	public void stop() 
	{
	    action = stop;
        action.start();
	}
	

	
	/**
	 * Turn the Robot around its axis.
	 * 
	 * @param degree Makes the Robot turn by the given amount.
	 */
	public void turnOnSpot(float degree)
	{
		action = new TurnOnSpot(degree, speedLeft, speedRight);
        action.start();
	}
	
	
	
	
	
	
	
	
	/**
	 * Sensors
	 */
	
	
	public void setColorMode(ColorSensorMode mode)
	{
		RobotComponents.inst().getColorSensor().setMode(mode.getIdf());
	}
	
	
	
	
}
