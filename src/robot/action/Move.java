package robot.action;

import robot.RobotComponents;

public class Move  implements RobotAction {
    
    public int deg;
    
    public Move(int degree) {
        this.deg = degree;
    }
    
    public void start() {
        RobotComponents.inst().getLeftMotor().rotate(deg, true);
        RobotComponents.inst().getRightMotor().rotate(deg, true);
    }
    
    public void update() {
        
    }

    public boolean finished() {
        return (!RobotComponents.inst().getLeftMotor().isMoving() && !RobotComponents.inst().getRightMotor().isMoving());
    }
}
