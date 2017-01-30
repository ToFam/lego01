package robot;

import robot.behavior.RobotBehavior;

/**
 * 
 * This class serves as an interface to control the Robot.
 * The class's core is the <code>RobotBehavior</code>,
 * which is a Strategy Design Pattern.
 * All methods in <code>Robot</code> call the corresponding
 * <code>RobotBehavior</code> method.
 * 
 * @author	Team AndreasBot: Adrian
 *
 */
public class Robot {

	/**
	 * Strategy Design Pattern
	 */
	private RobotBehavior behavior;
	
	/**
	 * 
	 * Constructor directly assigns the value.
	 * 
	 * @param behavior
	 */
	public Robot(RobotBehavior behavior) {
		this.behavior = behavior;
	}
	
	/**
	 * The Robot moves in a straight line.
	 */
	public void moveStraight() {
		behavior.moveStraight();
	}
	
	/**
	 * The Robot stops in place.
	 */
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
	
	/**
	 * Returns the Robots current <code>RobotBehavior</code>.
	 * @return The Robots current <code>RobotBehavior</code>.
	 */
	public RobotBehavior getBehavior() {
		return this.behavior;
	}
	
	/**
	 * Changes the Robots <code>RobotBehavior</code>.
	 * @param behavior The new <code>RobotBehavior</code>.
	 */
	public void setBehavior(RobotBehavior behavior) {
		this.behavior = behavior;
	}
	
	
}
