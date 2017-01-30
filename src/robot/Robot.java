package robot;

import robot.behavior.RobotBehavior;

public class Robot {

	private RobotComponents components;
	private RobotBehavior behavior;
	
	public Robot(RobotComponents components, RobotBehavior behavior) {
		this.components = components;
		this.behavior = behavior;
	}
	
	public void moveStraight() {
		behavior.moveStraight();
	}

	/**
	 * Turn the Robot around its axis.
	 * 
	 * @param degree Makes the Robot turn by the given amount.
	 */
	public void turnOnSpot(float degree) {
		behavior.turnOnSpot(degree);
	}
	
	public RobotComponents getComponents() {
		return this.components;
	}
	
	public RobotBehavior getBehavior() {
		return this.behavior;
	}
	
	public void setBehavior(RobotBehavior behavior) {
		this.behavior = behavior;
	}
	
	
}
