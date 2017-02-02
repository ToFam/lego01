package state;

import robot.Robot;
import robot.RobotComponents;
import sensor.modes.GyroSensorMode;

public class LabyrinthState implements ParcourState 
{
    private static final int TIME_UNTIL_FREE = 1000;

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
        RobotComponents.inst().getTouchSensorB().setMode(0);
        robot.setSpeed(1.f);
        robot.forward();
    }
    
    public void reset() 
    {
        
    }

    @Override
    public void update(int elapsedTime) 
    {
        if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) 
        {
            robot.stop();
            robot.move(540);
            free = 0;
            
            switch (choice) {
            case STRAIGHT:
                robot.turnOnSpot(90);
                choice = Path.LEFT;
                break;
            case LEFT:
                robot.turnOnSpot(180);
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
        }
        
        if (free > TIME_UNTIL_FREE) 
        {
            // we made it out (hopefully)
            choice = Path.STRAIGHT;
        }
    }
}
