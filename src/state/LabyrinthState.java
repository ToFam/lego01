package state;

import robot.Robot;
import robot.RobotComponents;
import sensor.modes.GyroSensorMode;
import sensor.modes.UVSensorMode;
import util.lcdGui.LCDGui;

public class LabyrinthState implements ParcourState 
{
    private static final float DISTANCE_SHOULD = 0.1f;
    private static final float TURN_FACTOR = 8.f;
    
    private static final float STANDARD_SPEED = 1.f;

    private Robot robot;
    private LCDGui gui;
    
    private float turn = 0;
    
    public enum State {
        RETREAT,
        TURN,
        FOLLOW
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
        
        //robot.rotateMiddle(-(Robot.SENSOR_ANGLE / 2 - 5));
        robot.forward();
        gui = new LCDGui(2, 1);
        state = State.FOLLOW;
    }
    
    public void reset() 
    {
        robot.stop();
        //robot.rotateMiddle((Robot.SENSOR_ANGLE / 2 - 5));
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
                robot.setSpeed(STANDARD_SPEED);
                robot.move(360);
                state = State.RETREAT;
            }
            else
            {
                float samp = RobotComponents.inst().getUV().sample()[0];
                
                turn = (samp - DISTANCE_SHOULD) * TURN_FACTOR;
                robot.steer(Math.max(-0.8f, Math.min(0.8f, turn)));
                robot.forward();
                
                gui.setVarValue(0, samp);
                gui.setVarValue(1, turn);
            }
            break;
        case RETREAT:
            if (robot.finished())
            {
                robot.turnOnSpot(-90);
                state = State.TURN;
            }
            break;
        case TURN:
            if (robot.finished())
            {
                robot.forward();
                state = State.FOLLOW;
            }
            break;
        default:
        }
    }
}
