package state;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.GyroSensorMode;
import sensor.modes.UVSensorMode;
import util.Util;
import util.lcdGui.LCDGui;

public class LabyrinthState implements ParcourState 
{
    private static final float DISTANCE_SHOULD = 0.1f;
    private static final float TOO_NEAR = 0.04f;
    private static final float TOO_FAR = 0.2f;
    private static final float CRITICAL_JUMP = 0.01f;
    private static final float TURN_FACTOR = 8.f;
    private static final float STANDARD_SPEED = 1.f;
    private static final int TIME_STRAIGHT = 2000;

    private Robot robot;
    private LCDGui gui;
    
    private float turn = 0.f;
    private float last_samp = DISTANCE_SHOULD;
    private float turnAngle = 0.f;
    private int elapsedInterval = 0;
    
    public enum State {
        RETREAT,
        ROTATE,
        FOLLOW,
        RETREAT2,
        SHARP_TURN,
        STRAIGHT,
        DEBUG
    }
    private State state;
    
    public LabyrinthState(Robot robo) 
    {
        robot = robo;
    }
    
	@Override
	public boolean changeOnBarcode()
	{
		return true;
	}
    
    @Override
    public boolean changeImmediately()
    {
        return false;
    }
    
    public String getName() 
    {
        return "Labyrinth";
    }

    @Override
    public void init() 
    {
        RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
        RobotComponents.inst().getTouchSensorB();
        RobotComponents.inst().getUV().setMode(UVSensorMode.DISTANCE.getIdf());
        RobotComponents.inst().getUV().setMedianFilter(100);
        robot.setSpeed(STANDARD_SPEED);
        
        if (!robot.UVisUp())
            robot.raiseUV();
        robot.forward();
        gui = new LCDGui(2, 1);
        state = State.FOLLOW;
    }
    
    public void stateChangeDebug() 
    {
        LCD.drawString("State Change: " + state.toString(), 0, 2);
        while (!Util.isPressed(Button.ID_LEFT))
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public void reset() 
    {
        robot.stop();
    }

    @Override
    public void update(int elapsedTime) 
    {
        switch (state) 
        {
        case FOLLOW:
            if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) 
            {
                robot.stop();
                state = State.RETREAT;
                stateChangeDebug();
                robot.setSpeed(STANDARD_SPEED);
                robot.move(360);
            }
            else
            {
                float samp = RobotComponents.inst().getUV().sample()[0];
                
                float max_turn = 0.8f;
                
                
                
                if (last_samp < TOO_NEAR)
                {
                    if (samp > last_samp + CRITICAL_JUMP)
                    {
                        // got too near to wall, back off and try again
                        state = State.RETREAT2;
                        robot.stop();
                        stateChangeDebug();
                        robot.setSpeed(STANDARD_SPEED);
                        robot.move(360);
                        return;
                    }
                }
                
                if (samp > TOO_FAR)
                {
                    robot.stop();
                    state = State.SHARP_TURN;
                    stateChangeDebug();
                    robot.forward();
                    max_turn = 0.5f;
                    turnAngle = RobotComponents.inst().getGyroSensor().sample()[0];
                    return;
                }
                
                last_samp = samp;
                turn = (samp - DISTANCE_SHOULD) * TURN_FACTOR;
                robot.steer(Math.max(-max_turn, Math.min(max_turn, turn)));
                robot.forward();
                
                gui.setVarValue(0, samp);
                gui.setVarValue(1, turn);
            }
            break;
        case RETREAT:
            if (robot.finished())
            {
                state = State.ROTATE;
                stateChangeDebug();
                robot.turnOnSpot(-90);
            }
            break;
        case RETREAT2:
            if (robot.finished())
            {
                state = State.ROTATE;
                stateChangeDebug();
                robot.turnOnSpot(-20);
            }
            break;
        case ROTATE:
            if (robot.finished())
            {
                state = State.FOLLOW;
                stateChangeDebug();
                robot.forward();
            }
            break;
        case SHARP_TURN:
            if (RobotComponents.inst().getGyroSensor().sample()[0] < turnAngle + 90)
            {
                robot.steer(0.6f);
                robot.forward();
            }
            else
            {
                robot.stop();
                robot.setSpeed(STANDARD_SPEED);
                state = State.STRAIGHT;
                stateChangeDebug();
            }
            break;
        case STRAIGHT:
            elapsedInterval += elapsedTime;
            if (elapsedInterval < TIME_STRAIGHT)
            {
                robot.forward();
            }
            else
            {
                state = State.FOLLOW;
                robot.stop();
                stateChangeDebug();
                robot.forward();
            }
            break;
        default:
        }
    }
}
