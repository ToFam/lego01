package state;

import robot.Robot;
import robot.RobotComponents;

public class HumpbackBridgeState implements ParcourState {

	private Robot robot;
	
	public HumpbackBridgeState(Robot robot) {
		this.robot = robot;
	}
	@Override
	public String getName() {
		return "HumpbackBridge";
	}

	@Override
	public void init() {
		RobotComponents.inst().getMediumMotor().rotate(70, false);
		RobotComponents.inst().getMediumMotor().rotate(-70, false);
		
	}

	@Override
	public void update(int elapsedTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
