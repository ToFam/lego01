package robot.action;

import robot.RobotComponents;

public class Forward implements RobotAction {
    
    public void start() {
        RobotComponents.inst().getLeftMotor().backward();
        RobotComponents.inst().getRightMotor().backward();
    }
    
    public void update() {
        
    }

    public boolean finished() {
        return false;
    }
}
