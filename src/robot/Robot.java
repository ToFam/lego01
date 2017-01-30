package robot;

import robot.behavior.RobotBehavior;

public class Robot {

	private RobotBehavior behavior;
	
	public Robot(RobotBehavior behavior) {
		this.behavior = behavior;
	}
	
	public void moveStraight() {
		behavior.moveStraight();
	}
	
	public void stop() {
		this.behavior.stop();
	}

	/**
	 * Turn the Robot around its axis.
	 * 
	 * @param degree Makes the Robot turn by the given amount.
	 */
	public void turnOnSpot(float degree) {
		behavior.turnOnSpot(degree);
	}
	
	public RobotBehavior getBehavior() {
		return this.behavior;
	}
	
	public void setBehavior(RobotBehavior behavior) {
		this.behavior = behavior;
	}
	
	
}
