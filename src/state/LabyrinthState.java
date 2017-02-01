package state;

import robot.Robot;
import robot.RobotComponents;

public class LabyrinthState implements ParcourState {
    private Robot robot;
    
    private int retreat = 0;
    private int free = 0;
    
    private enum Path {
        STRAIGHT,
        LEFT,
        RIGHT
    }
    
    private Path choice = Path.STRAIGHT;
    
    public LabyrinthState(Robot robo) {
        robot = robo;
    }

    @Override
    public void init() {
    }

    @Override
    public void update() {
        if (retreat > 0) {
            retreat--;
            
            if (retreat == 0)
            {
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
        }
        else {
            if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) {
                robot.stop();
                retreat = 10;
                free = 0;
            }
            else
            {
                free++;
            }
            
            if (free > retreat * 1.5) {
                // we made it out (hopefully)
                robot.forward();
                choice = Path.STRAIGHT;
            }
        }
    }
}
