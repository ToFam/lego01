package state;

import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
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
//		RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RED.getIdf());
		this.swampSegment = SwampSegment.PRE_BARCODE;
		this.time = 0;
		this.finished = false;
		
		gui.setVarValue(0, this.swampSegment.toString());
		
		this.robot.setSpeed(1.f);
		this.robot.forward();
	}

	@Override
	public void update(int elapsedTime) {
		
		this.color = RobotComponents.inst().getColorSensor().sample()[0];
		
		do {
			this.distance = RobotComponents.inst().getUS().sample()[0];
		} while (this.distance == Float.POSITIVE_INFINITY && this.distance >= 1.f);
		
		this.gui.setVarValue(1, String.valueOf(this.color));
		
		switch (this.swampSegment) {
			case PRE_BARCODE:
				
				if (this.color > 0.8f) {
					this.swampSegment = SwampSegment.BARCODE;
					this.gui.setVarValue(0, this.swampSegment.toString());
					return;
				}
				
				this.correction = (this.distance - 0.05f) * 8f;
                robot.steer(Math.max(-0.8f, Math.min(0.8f, correction)));
	            robot.forward();
	            
				break;
				
			case BARCODE:
				
				if (this.color < 0.8f) {
					this.swampSegment = SwampSegment.POST_BARCODE;
					this.gui.setVarValue(0, this.swampSegment.toString());
					return;
				}
				
				this.correction = (this.distance - 0.05f) * 8f;
				this.robot.steer(Math.max(-0.8f, Math.min(0.8f, correction)));
				this.robot.forward();
				
				break;
				
			case POST_BARCODE:
				
				if (this.color > 0.8f) {
//					this.finished = true;
					this.swampSegment = SwampSegment.DO_NOTHING;
					
					this.robot.stop();
		            
		            return;
				}
				
				this.correction = (this.distance - 0.03f) * 8f;
				this.robot.steer(Math.max(-0.8f, Math.min(0.8f, correction)));
				this.robot.forward();
				
				break;
				
			case DO_NOTHING:
				
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
