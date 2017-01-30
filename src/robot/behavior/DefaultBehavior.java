package robot.behavior;

import robot.RobotComponents;

/**
 * 
 * The <code>DefaultBehavior</code> class makes use of a single scale
 * to dynamically adjust the movement property of the Robot.
 * 
 * @author	Team AndreasBot: Adrian
 *
 */
public class DefaultBehavior implements RobotBehavior {
	
	/**
	 * The constructor calls the method <code>setSpeedScale</code>
	 * to initialize the scale. Setting or changing the scale has immediate effect.
	 * 
	 * @param speedScale
	 */
	public DefaultBehavior(float speedScale) {
		this.setSpeedScale(speedScale);
	}
	
	/**
	 * This method is capable of re-scaling the Robot's speed.
	 * If the Robot is moving forward, changing the scale will update its current speed.
	 * 
	 * @param speedScale Scales the Robot's maximum Speed.
	 */
	public void setSpeedScale(float speedScale) {
		
		float speed = Math.min(
				RobotComponents.getLeftMotor().getMaxSpeed(),
				RobotComponents.getRightMotor().getMaxSpeed()
				) * speedScale;
		RobotComponents.getLeftMotor().setSpeed(speed);
		RobotComponents.getRightMotor().setSpeed(speed);
	}
	
	/**
	 * The Robot moves in a straight line.
	 */
	public void moveStraight() {
		
		RobotComponents.getLeftMotor().backward();
		RobotComponents.getRightMotor().backward();
		
	};
	
	/**
	 * The Robot stops in place.
	 */
	public void stop() {
		
		RobotComponents.getLeftMotor().stop();
		RobotComponents.getRightMotor().stop();
		
	};
	
	/**
	 * The Robot follows the Line.
	 */
	public void followLine() {
		
	};

	/**
	 * Turn the Robot around its axis.
	 * 
	 * @param degree Makes the Robot turn by the given amount.
	 */
	public void turnOnSpot(float degree) {
		
	};
	
}
