package robot.action;

import robot.RobotComponents;

public class Stop implements RobotAction {
    
    public void start() {
        RobotComponents.inst().getLeftMotor().stop(true);
        RobotComponents.inst().getRightMotor().stop(true);
    }
    
    public void update() {
        
    }

    public boolean finished() {
        return (!RobotComponents.inst().getLeftMotor().isMoving() && !RobotComponents.inst().getRightMotor().isMoving());
    }
}
