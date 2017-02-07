package state;

import robot.Robot;
import robot.RobotComponents;
import sensor.GyroSensor;
import sensor.TouchSensorBThread;
import sensor.USSensor;
import util.lcdGui.LCDGui;

public class Endboss implements ParcourState {
	
	
	public enum EndbossState
	{
		START, DRIVE_LEFTWALL_TOBOSS, DRIVETOBOSS_RETREAT, DRIVETOBOSS_TURN, LOWER_SHOOT, SHOOT, UPPER_SHOOT,
		LURE, LURE_RETREAT, LURE_TURN, PUSH, PUSH_RETREAT, PUSH_TURN, DRIVE_LTW, DRIVE_RETREAT, DRIVE_TURN
	}

	
    private Robot robot;
    private LCDGui gui;
    
    
    private static final float param_robotMaxSpeed = 1f;
    private static final float param_goalDistance_toboss = 0.1f;
    private static final float param_robotRetreatSpeed = 0.8f;
    private static final int param_timeLoweringTheCancnon = 4000;
    private static final int param_timePush = 10000;
    private static final int param_mediumRotateAngle = 280;
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
    }

    
    public String getName() {
        return "Endboss";
    }
    
    public void reset() {
        
    }
    
    private GyroSensor gyros;
    private USSensor us;
    private TouchSensorBThread touch;
    
    private EndbossState state;

    private float turn = 0;
    private int mediumStart = 0;
    
    private float waitCounter;
    
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
    			RobotComponents.inst().getMediumMotor().rotateTo(param_mediumRotateAngle, true);
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
                RobotComponents.inst().getMediumMotor().stop();
                RobotComponents.inst().getMediumMotor().rotateTo(0, true);
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
    	        robot.move(330);
    	        state = EndbossState.PUSH_RETREAT;
    	    }
    	    break;
    	case PUSH_RETREAT:
    	    if (robot.finished())
    	    {
    	        robot.stop();
    	        robot.turnOnSpot(180);
                robot.setSpeed(param_robotMaxSpeed);
    	        state = EndbossState.DRIVE_LTW;
    	    }
    	    break;
    	case DRIVE_LTW:
            if (touch.sample()[0] == 1.0f)
            {
                robot.stop();
                robot.setSpeed(param_robotRetreatSpeed);
                robot.move(330);
                state = EndbossState.DRIVE_RETREAT;
            }
            else
            {
                float samp = us.sample()[0];
                
                turn = (samp - param_goalDistance_toboss) * 25;
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
    	        state = EndbossState.DRIVE_LTW;
    	    }
    	    break;
    	}   
    }
}
