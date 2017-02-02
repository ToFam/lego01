package state;

import robot.Robot;
import robot.RobotComponents;
import sensor.modes.GyroSensorMode;
import sensor.modes.UVSensorMode;

public class LabyrinthState implements ParcourState 
{
    private static final int TIME_UNTIL_FREE = 1000;
    private static final float DISTANCE_SHOULD = 0.05f;
    private static final float TURN_FACTOR = 10.f;

    private int free = 0;
    private Robot robot;
    
    private enum Path
    {
        STRAIGHT,
        LEFT,
        RIGHT
    }
    
    private Path choice = Path.STRAIGHT;
    
    public LabyrinthState(Robot robo) 
    {
        robot = robo;
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
        RobotComponents.inst().getUV().setMedianFilter(1000);
        robot.setSpeed(1.f);
        
        robot.rotateMiddle(-(Robot.SENSOR_ANGLE / 2 - 5));
        robot.forward();
    }
    
    public void reset() 
    {
        robot.stop();
        robot.rotateMiddle((Robot.SENSOR_ANGLE / 2 - 5));
    }

    @Override
    public void update(int elapsedTime) 
    {
        if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) 
        {
            robot.stop();
            robot.move(360);
            free = 0;
            
            switch (choice) {
            case STRAIGHT:
                robot.turnOnSpot(-90);
                choice = Path.RIGHT;
                break;
            case RIGHT:
                robot.turnOnSpot(-90);
                choice = Path.STRAIGHT;
                break;
            }
            robot.forward();
        }
        else
        {
            free++;
            
            float turn = (RobotComponents.inst().getUV().sample()[0] - DISTANCE_SHOULD) * TURN_FACTOR;
            robot.steer(turn);
        }
        
        if (free > TIME_UNTIL_FREE) 
        {
            // we made it out (hopefully)
            choice = Path.STRAIGHT;
        }
    }
}
