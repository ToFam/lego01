package robot.behavior;

import robot.RobotComponents;

public class DefaultBehavior implements RobotBehavior {
	
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
	
	public void moveStraight() {
		
		RobotComponents.getLeftMotor().backward();
		RobotComponents.getRightMotor().backward();
		
	};
	
	public void stop() {
		
		RobotComponents.getLeftMotor().stop();
		RobotComponents.getRightMotor().stop();
		
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
