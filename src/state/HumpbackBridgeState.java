package state;

import robot.Robot;
import robot.RobotComponents;

public class HumpbackBridgeState implements ParcourState {

	private Robot robot;
	private final int ANGLE;
	private boolean test;
	
	public HumpbackBridgeState(Robot robot) {
		this.robot = robot;
		this.ANGLE = 90;
		this.test = false;
	}
	@Override
	public String getName() {
		return "HumpbackBridge";
	}

	@Override
	public void init() {
		RobotComponents.inst().getMediumMotor().rotate(this.ANGLE + 5, false);
		
	}

	@Override
	public void update(int elapsedTime) {
		if (test) {
			RobotComponents.inst().getMediumMotor().rotate(this.ANGLE * 2, false);
		} else {
			RobotComponents.inst().getMediumMotor().rotate(-this.ANGLE * 2, false);
		}
		
		this.test = !this.test;
		
	}

	@Override
	public void reset() {
		if (test) {
			RobotComponents.inst().getMediumMotor().rotate(this.ANGLE, false);
		} else {
			RobotComponents.inst().getMediumMotor().rotate(-this.ANGLE - 5, false);
		}
	}

}
