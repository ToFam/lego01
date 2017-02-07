package state;

import robot.Robot;
import robot.RobotComponents;
import util.lcdGui.LCDGui;

public class SwampState implements ParcourState {

	private Robot robot;
	private LCDGui gui;
	private SwampSegment swampSegment;
	private float color;
	private float distance;
	private float correction;
	private int time;
	private boolean finished;
	
	private enum SwampSegment {
		PRE_BARCODE,
		BARCODE,
		POST_BARCODE,
		DO_NOTHING,
	}
	
	public SwampState(Robot robot) {
		this.robot = robot;
		this.gui = new LCDGui(2, 1);
	}

	@Override
	public String getName() {
		return "Swamp";
	}

	@Override
	public void init() {

		this.swampSegment = SwampSegment.BARCODE;
		this.time = 0;
		this.finished = false;
		
		this.gui.setVarValue(0, this.swampSegment.toString());
		
		this.robot.setSpeed(1.f);
		this.robot.forward();
	}

	@Override
	public void update(int elapsedTime) {
		
		if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) {
			this.finished = true;
			this.robot.stop();
			return;
		}
		
		do {
			this.distance = RobotComponents.inst().getUS().sample()[0];
		} while (this.distance == Float.POSITIVE_INFINITY && this.distance >= 1.f);
		
		this.correction = (this.distance - 0.08f) * 8f;
        robot.steer(Math.max(-0.8f, Math.min(0.8f, correction)));
        robot.forward();
		
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean changeOnBarcode() {
		return false;
	}

	@Override
	public boolean changeImmediately() {
		return this.finished;
	}
	
}
