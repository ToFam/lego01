package state;

import robot.Robot;
import robot.RobotComponents;
import sensor.ColorSensor;
import sensor.GyroSensor;
import sensor.TouchSensorBThread;
import sensor.USSensor;
import util.lcdGui.LCDGui;

public class Endboss implements ParcourState {
	
	
	public enum EndbossState
	{
		START, 
		    DRIVE_LEFTWALL_TOBOSS, 
	        DRIVETOBOSS_RETREAT, 
	        DRIVETOBOSS_TURN, 
	        LOWER_SHOOT, 
	        SHOOT, 
	        UPPER_SHOOT,
		LURE, 
		    LURE_RETREAT, 
		    LURE_TURN, 
		PUSH, 
		    PUSH_RETREAT, 
		    PUSH_TURN, 
	    DRIVE_LTW, 
		    DRIVE_RETREAT, 
		    DRIVE_TURN, 
	    DRIVE_LTW_ENTRY, 
		    DRIVE_LTW_ENTRY_RETREAT, 
		    DRIVE_LTW_ENTRY_TURN,
		DRIVE_LTW_EXIT,
		    DRIVE_LTW_EXIT_RETREAT,
		    DRIVE_LTW_EXIT_TURN,
		    
		DRIVE_STRAIGHT_TILL_DOTS,
		
		WAIT_TO_TURN_BECAUSE_LINE,
		    
		    
		    
		    TURN_BLABLABLA
	}

	
    private Robot robot;
    private LCDGui gui;
    
    
    private static final float param_robotMaxSpeed = 1f;
    private static final float param_goalDistance_toboss = 0.1f + 0.04f;
    private static final float param_robotRetreatSpeed = 0.8f;
    private static final int param_timeLoweringTheCancnon = 4000;
    private static final int param_timePush = 10000;
    private static final int param_mediumRotateAngle = 280;
    private static final float param_usDistanceToDriveStraight = 0.3f;
    //private static final boolean param_debugWaits = false;
    
    public Endboss(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(2, 1);
    }
    
	@Override
	public boolean changeOnBarcode()
	{
		return false;
	}
    
    @Override
    public boolean changeImmediately()
    {
        return false;
    }
    
    @Override
    public void init() {
    	state = EndbossState.START;
    	
        robot.setSpeed(param_robotMaxSpeed, param_robotMaxSpeed);
        
        gyros = RobotComponents.inst().getGyroSensor();
        us = RobotComponents.inst().getUS();
        touch = RobotComponents.inst().getTouchSensorB();
        color = RobotComponents.inst().getColorSensor();
    }

    
    public String getName() {
        return "Endboss";
    }
    
    public void reset() {
        robot.stop();
    }
    
    private GyroSensor gyros;
    private USSensor us;
    private TouchSensorBThread touch;
    private ColorSensor color;
    
    private EndbossState state;

    private float turn = 0;
    private int mediumStart = 0;
    
    private float waitCounter;
    
    private float gyroAtPush = Float.MAX_VALUE;
    
    private float gyroOnInfinity = 0f;
    
    private void startWait(float time)
    {
        waitCounter = time;
    }
    private boolean waiting(float timeElapsed)
    {
        waitCounter -= timeElapsed;
        if (waitCounter >= 0)
            return true;
        return false;
    }
    
    @Override
    public void update(int elapsedTime) {

    	if (state != EndbossState.WAIT_TO_TURN_BECAUSE_LINE && color.sample()[0] >= 0.4f)
    	{
    		robot.stop();
    		robot.turnOnSpot(180);
    		state = EndbossState.WAIT_TO_TURN_BECAUSE_LINE;
    	}
    	
    	switch (state)
    	{
    	case START:
    		state = EndbossState.DRIVE_LEFTWALL_TOBOSS;
    		break;
    	case DRIVE_LEFTWALL_TOBOSS:
    		robot.setSpeed(param_robotMaxSpeed);

    		if (touch.sample()[0] == 1.0f)
    		{
    			robot.stop();
                robot.setSpeed(param_robotRetreatSpeed);
                robot.move(330);
    			state = EndbossState.DRIVETOBOSS_RETREAT;
    		}
    		else
    		{
        		float samp = us.sample()[0];
                
                turn = (samp - param_goalDistance_toboss) * 25;
                robot.steer(Math.max(-0.8f, Math.min(0.8f, turn)));
                robot.forward();
    		}
    		
    		break;
    	case DRIVETOBOSS_RETREAT:
    		if (robot.finished())
    		{
                robot.turnOnSpot(-90);
                state = EndbossState.DRIVETOBOSS_TURN;
    		}
    		break;
    	case DRIVETOBOSS_TURN:
    		if (robot.finished())
    		{
    			robot.stop();
    			mediumStart = RobotComponents.inst().getMediumMotor().getTachoCount();
    			startWait(param_timeLoweringTheCancnon);
    			RobotComponents.inst().getMediumMotor().setSpeed(RobotComponents.inst().getMediumMotor().getMaxSpeed() * 0.3f);
    			//RobotComponents.inst().getMediumMotor().rotateTo(param_mediumRotateAngle, true);
    			RobotComponents.inst().getMediumMotor().rotate(param_mediumRotateAngle, true);
    			state = EndbossState.SHOOT;
    		}
    		break;
    	/*case LOWER_SHOOT:
    	    if (!waiting(elapsedTime))
    	    {
    			RobotComponents.inst().getMediumMotor().stop();
    			RobotComponents.inst().getMediumMotor().rotateTo(mediumStart, true);
    			state = EndbossState.SHOOT;
    			startWait(500);
    		}
    		break;*/
    	case SHOOT:
    	    if (!waiting(elapsedTime))
    	    {
    	        state = EndbossState.UPPER_SHOOT;
                startWait(500);
    	    }
    	    break;
    	case UPPER_SHOOT:
            if (!waiting(elapsedTime))
            {
                //RobotComponents.inst().getMediumMotor().stop();
                //RobotComponents.inst().getMediumMotor().rotateTo(0, true);
    			RobotComponents.inst().getMediumMotor().rotate(-param_mediumRotateAngle, true);
                robot.setSpeed(param_robotMaxSpeed);
                robot.forward();
                state = EndbossState.LURE;
            }
            break;
    	case LURE:
            if (touch.sample()[0] == 1.0f)
            {
                robot.stop();
                robot.setSpeed(param_robotRetreatSpeed);
                robot.move(330);
                state = EndbossState.LURE_RETREAT;
            }
            break;
    	case LURE_RETREAT:
    	    if (robot.finished())
    	    {
    	        robot.stop();
    	        robot.turnOnSpot(90);
    	        state = EndbossState.LURE_TURN;
    	    }
    	    break;
    	case LURE_TURN:
    	    if (robot.finished())
    	    {
                robot.stop();
                
                gyroAtPush = gyros.sample()[0];
                
                robot.setSpeed(param_robotMaxSpeed);
                robot.forward();
                state = EndbossState.PUSH;
                startWait(param_timePush);
	        }
    	    break;
    	case PUSH:
    	    if (!waiting(elapsedTime))
    	    {
    	        robot.stop();
    	        robot.setSpeed(param_robotRetreatSpeed);
    	        robot.move(450);
    	        state = EndbossState.PUSH_RETREAT;
    	    }
    	    break;
    	case PUSH_RETREAT:
    	    if (robot.finished())
    	    {
    	        robot.stop();
                robot.setSpeed(param_robotMaxSpeed);
                
                float curGyro = gyros.sample()[0];
                
    	        robot.turnOnSpot(180 + gyroAtPush - curGyro);
    	        state = EndbossState.TURN_BLABLABLA;
    	    }
    	    break;
    	case TURN_BLABLABLA:
    		if (robot.finished())
    		{
    			robot.stop();
    			state = EndbossState.DRIVE_LTW_EXIT;
    			robot.forward();
    		}
    		break;
    	case DRIVE_LTW:
            if (touch.sample()[0] == 1.0f)
            {
                robot.stop();
                robot.setSpeed(param_robotRetreatSpeed);
                robot.move(240);
                state = EndbossState.DRIVE_RETREAT;
            }
            else
            {
                float samp = us.sample()[0];
                
                turn = (samp - (param_goalDistance_toboss + 0.06f)) * 25;
                robot.steer(Math.max(-0.8f, Math.min(0.8f, turn)));
                robot.forward();
            }
            
            break;
    	case DRIVE_RETREAT:
    	    if (robot.finished())
    	    {
    	        robot.stop();
    	        robot.turnOnSpot(-90);
    	        state = EndbossState.DRIVE_TURN;
    	    }
    	    break;
    	case DRIVE_TURN:
    	    if (robot.finished())
    	    {
    	        robot.stop();
    	        robot.setSpeed(param_robotMaxSpeed);
    	        robot.forward();
    	        //state = EndbossState.DRIVE_LTW_ENTRY;
    	        state = EndbossState.DRIVE_LTW_EXIT;
    	    }
    	    break;
    	case DRIVE_LTW_ENTRY:
            if (touch.sample()[0] == 1.0f)
            {
                robot.stop();
                robot.setSpeed(param_robotRetreatSpeed);
                robot.move(330);
                state = EndbossState.DRIVE_LTW_ENTRY_RETREAT;
            }
            else
            {
                float samp = us.sample()[0];
                
                turn = (samp - param_goalDistance_toboss) * 25;
                robot.steer(Math.max(-0.8f, Math.min(0.1f, turn)));
                robot.forward();
            }
            
            break;
        case DRIVE_LTW_ENTRY_RETREAT:
            if (robot.finished())
            {
                robot.stop();
                robot.turnOnSpot(-90);
                state = EndbossState.DRIVE_LTW_ENTRY_TURN;
            }
            break;
        case DRIVE_LTW_ENTRY_TURN:
            if (robot.finished())
            {
                robot.stop();
                robot.setSpeed(param_robotMaxSpeed);
                robot.forward();
                state = EndbossState.DRIVE_LTW_EXIT;
            }
            break;
        case DRIVE_LTW_EXIT:
            if (touch.sample()[0] == 1.0f)
            {
                robot.stop();
                robot.setSpeed(param_robotRetreatSpeed);
                robot.move(240);
                state = EndbossState.DRIVE_LTW_EXIT_RETREAT;
            }
            else
            {
                float samp = us.sample()[0];
                
                if (samp <= param_usDistanceToDriveStraight)
                {
                	gyroOnInfinity = gyros.sample()[0];
                    
                    turn = (samp - param_goalDistance_toboss) * 25;
                    robot.steer(Math.max(-0.8f, Math.min(0.8f, turn)));
                    robot.forward();
                }
                else
                {
                	if (Math.abs(gyros.sample()[0] - gyroOnInfinity) >= 360)
                	{
                		gyroOnInfinity = gyros.sample()[0];
                		robot.setSpeed(param_robotMaxSpeed);
                		robot.forward();
                		state = EndbossState.DRIVE_STRAIGHT_TILL_DOTS;
                	}
                	else
                	{
                        turn = (samp - param_goalDistance_toboss) * 25;
                        robot.steer(Math.max(-0.8f, Math.min(0.8f, turn)));
                        robot.forward();
                	}
                }
            }
            
            break;
        case DRIVE_LTW_EXIT_RETREAT:
            if (robot.finished())
            {
                robot.stop();
                robot.turnOnSpot(-90);
                state = EndbossState.DRIVE_LTW_EXIT_TURN;
            }
            break;
        case DRIVE_LTW_EXIT_TURN:
            if (robot.finished())
            {
                robot.stop();
                robot.setSpeed(param_robotMaxSpeed);
                robot.forward();
                state = EndbossState.DRIVE_LTW_EXIT;
            }
            break;
            
        case DRIVE_STRAIGHT_TILL_DOTS:
        	if (touch.sample()[0] == 1.0f)
            {
                robot.stop();
                robot.setSpeed(param_robotRetreatSpeed);
                robot.move(240);
                state = EndbossState.DRIVE_LTW_EXIT_RETREAT;
            }
        	break;
    	case WAIT_TO_TURN_BECAUSE_LINE:
    		if (robot.finished())
    		{
    			robot.stop();
    			state = EndbossState.DRIVE_LTW_EXIT;
    		}
    		break;
    	}
    }
}
