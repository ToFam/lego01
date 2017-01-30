package robot.behavior;

/**
 * 
 * Interface for the Strategy Design Pattern handling
 * the Robots Behavior under certain circumstances.
 * 
 * @author	Team AndreasBot: Adrian
 *
 */
public interface RobotBehavior {
	
	/**
	 * The Robot moves in a straight line.
	 */
	public void moveStraight();
	
	/**
	 * The Robot stop in place.
	 */
	public void stop();
	
	/**
	 * The Robot follows the line, until it ends.
	 * The end is to be defined!
	 */
	public void followLine();

	/**
	 * Turn the Robot around its axis.
	 * 
	 * @param degree Makes the Robot turn by the given amount.
	 */
	public void turnOnSpot(float degree);

}
