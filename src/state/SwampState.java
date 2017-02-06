package state;

import robot.Robot;

public class SwampState implements ParcourState {

	private Robot robot;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(int elapsedTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean changeOnBarcode() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
