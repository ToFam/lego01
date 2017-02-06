package state;

import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;

public class SwampState implements ParcourState {

	private Robot robot;
	private SwampSegment swampSegment;
	private float color;
	private float distance;
	private float correction;
	private int time;
	private boolean finished;
	
	private enum SwampSegment {
		PRE_BARCODE,
		BARCODE,
		POST_BARCODE
	}
	
	public SwampState(Robot robot) {
		this.robot = robot;
	}

	@Override
	public String getName() {
		return "Swamp";
	}

	@Override
	public void init() {
		RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RED.getIdf());
		this.swampSegment = SwampSegment.PRE_BARCODE;
		this.time = 0;
		this.finished = false;
	}

	@Override
	public void update(int elapsedTime) {
		
		this.color = RobotComponents.inst().getColorSensor().sample()[0];
		
		switch (this.swampSegment) {
			case PRE_BARCODE:
				
				if (this.color > 0.8f) {
					this.swampSegment = SwampSegment.BARCODE;
					return;
				}
	            
				break;
				
			case BARCODE:
				
				if (this.color < 0.8f) {
					this.swampSegment = SwampSegment.POST_BARCODE;
					return;
				}
				
				this.correction = (this.distance - 0.08f) * 8f;
                robot.steer(Math.max(-0.8f, Math.min(0.8f, correction)));
	            robot.forward();
				
				break;
				
			case POST_BARCODE:
				
				if (this.color > 0.8f) {
					this.finished = true;
					
					this.correction = (this.distance - 0.08f) * 8f;
	                robot.steer(Math.max(-0.8f, Math.min(0.8f, correction)));
		            robot.forward();
		            
		            return;
				}
				
				if (this.time < 200) {
					this.time += elapsedTime;
					return;
				}
				
				this.correction = (this.distance - 0.05f) * 8f;
                robot.steer(Math.max(-0.8f, Math.min(0.8f, correction)));
	            robot.forward();
				
				break;
		}
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean changeOnBarcode() {
		return finished;
	}

	@Override
	public boolean changeImmediately() {
		return finished;
	}
	
}
