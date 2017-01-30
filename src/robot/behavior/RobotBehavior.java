package robot.behavior;

import robot.RobotComponents;

public class RobotBehavior {
	
	private RobotComponents components;
	
	protected RobotBehavior(RobotComponents components, float speedScale) {
		
		this.components = components;
		this.setSpeedScale(speedScale);
		
	}
	
	protected RobotBehavior(RobotComponents components) {
		this(components, 1.f);
	}
	
	/**
	 * This method is capable of re-scaling the Robot's speed.
	 * If the Robot is moving forward, changing the scale will update its current speed.
	 * 
	 * @param speedScale Scales the Robot's maximum Speed.
	 */
	public void setSpeedScale(float speedScale) {
		
		float speed = Math.min(
				components.getLeftMotor().getMaxSpeed(),
				components.getRightMotor().getMaxSpeed()
				) * speedScale;
		components.getLeftMotor().setSpeed(speed);
		components.getRightMotor().setSpeed(speed);
	}
	
	public void moveStraight() {
		
		components.getLeftMotor().backward();
		components.getRightMotor().backward();
		
	};
	
	public void stop() {
		
		components.getLeftMotor().stop();
		components.getRightMotor().stop();
		
	};
	
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
