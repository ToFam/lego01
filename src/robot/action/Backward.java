package robot.action;

import robot.RobotComponents;

public class Backward implements RobotAction {
    
    public void start() {
        RobotComponents.inst().getLeftMotor().forward();
        RobotComponents.inst().getRightMotor().forward();
    }
    
    public void update() {
        
    }

    public boolean finished() {
        return false;
    }
}
