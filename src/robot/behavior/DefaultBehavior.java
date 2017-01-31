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
	
	protected float speedScale;
	
	/**
	 * The constructor calls the method <code>setSpeedScale</code>
	 * to initialize the scale. Setting or changing the scale has immediate effect.
	 * 
	 * @param speedScale
	 */
	public DefaultBehavior(float speedScale) {
		this.speedScale = speedScale;
		this.setSpeedScale(speedScale, speedScale);
	}
	
	/**
	 * This method is capable of re-scaling the Robot's speed.
	 * If the Robot is moving forward, changing the scale will update its current speed.
	 * 
	 * @param speedScale Scales the Robot's maximum Speed.
	 */
	public void setSpeedScale(float leftScale, float rightScale) {
		
		float speed = Math.min(
				RobotComponents.inst().getLeftMotor().getMaxSpeed(),
				RobotComponents.inst().getRightMotor().getMaxSpeed()
				);
		RobotComponents.inst().getLeftMotor().setSpeed(speed * leftScale);
		RobotComponents.inst().getRightMotor().setSpeed(speed * rightScale);
	}
	
	/**
	 * The Robot forward moves in a straight line.
	 */
	public void moveForward() {
		
		RobotComponents.inst().getLeftMotor().backward();
		RobotComponents.inst().getRightMotor().backward();
		
	}
	
	/**
	 * The Robot backward moves in a straight line.
	 */
	public void moveBackward() {
		
		RobotComponents.inst().getLeftMotor().forward();
		RobotComponents.inst().getRightMotor().forward();
		
	}
	
	/**
	 * The Robot stops in place.
	 */
	public void stop() {
		
		RobotComponents.inst().getLeftMotor().stop();
		RobotComponents.inst().getRightMotor().stop();
		
	}
	
	/**
	 * The Robot follows the Line.
	 */
	public void followLine() {
		
	}

	/**
	 * The Robot moves in a curved line.
	 * @param direction Direction of the curve. 
	 */
	public void curveLeft() {
		
	}

	/**
	 * The Robot moves in a curved line.
	 * @param direction Direction of the curve. 
	 */
	public void curveRight() {
		
	}

	/**
	 * Turn the Robot around its axis.
	 * 
	 * @param degree Makes the Robot turn by the given amount.
	 */
	public void turnOnSpot(float degree) {
		
	}
	
}
