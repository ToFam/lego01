package robot.behavior;

public interface RobotBehavior {
	
	public void moveStraight();
	
	public void stop();
	
	public void followLine();

	/**
	 * Turn the Robot around its axis.
	 * 
	 * @param degree Makes the Robot turn by the given amount.
	 */
	public void turnOnSpot(float degree);

}
