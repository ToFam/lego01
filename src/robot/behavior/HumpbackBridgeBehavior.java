package robot.behavior;

public class HumpbackBridgeBehavior extends DefaultBehavior {
	
	private final float turnScale = 0.8f * speedScale;

	public HumpbackBridgeBehavior(float speedScale) {
		super(speedScale);
	}
	
	@Override
	public void curveLeft() {
		this.setSpeedScale(turnScale, speedScale);
	}
	
	public void curveRight() {
		this.setSpeedScale(speedScale, turnScale);
	}

}
