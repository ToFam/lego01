package state;

import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import util.Util;
import util.lcdGui.LCDGui;

public class SwampState implements ParcourState {

	private Robot robot;
	private LCDGui gui;
	private float[] distances;
	private int distance_fresh;
	private int distance_old;
	private float recommended;
	private float correction;
	
	private SwampSegment swampSegment;
	private int error;
	
	private int time;
	private boolean finished;
	
	private enum SwampSegment {
		NO_SWAMP,
		SWAMP,
		ERROR,
	}
	
	public SwampState(Robot robot) {
		this.robot = robot;
		this.gui = new LCDGui(1, 1);
	}

	@Override
	public String getName() {
		return "Swamp";
	}

	@Override
	public void init() {

		this.finished = false;
		this.swampSegment = SwampSegment.NO_SWAMP;
		this.error = 0;
		
		this.distances = new float[3];
		this.distance_fresh = this.distances.length;
		this.distance_old = 0;
		this.recommended = 0.06f;
		
		for (int i = 1; i < this.distances.length; i++) {
			this.distances[i] = Float.NaN;
		}
		
		this.time = 0;
		
		this.robot.setSpeed(1.f);
		this.robot.forward();
	}

	@Override
	public void update(int elapsedTime) {
		
		if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) {
			this.swampSegment = SwampSegment.ERROR;
			
			while(!Util.isPressed(Button.DOWN.getId())) {
				this.robot.stop();
			}
			return;
		}
		
		if (this.time > 5000) {
			RobotComponents.inst().getRightMotor().rotate(150, false);
			this.time = 0;
		} else {
			this.time += elapsedTime;
		}
		
		this.distance_fresh = (this.distance_fresh + 1) % this.distances.length;
		this.distance_old = (this.distance_old + 1) % this.distances.length;
		
		this.distances[this.distance_fresh] = RobotComponents.inst().getUS().sample()[0];
		
		while (this.distances[this.distance_fresh] >= 1.f) {
			this.robot.stop();
			this.distances[this.distance_fresh] = RobotComponents.inst().getUS().sample()[0];
		}
		
		switch (this.swampSegment) {
		case NO_SWAMP:
			
			this.gui.setVarValue(0, "NO_SWAMP");
			
			if (this.distances[this.distance_fresh] < 0.05f &&
					this.distances[this.distance_old] < 0.05f) {
				this.swampSegment = SwampSegment.SWAMP;
				
				while(!Util.isPressed(Button.DOWN.getId())) {
					this.robot.stop();
				}
			}
			
			this.correction = (this.distances[this.distance_fresh] - 0.06f) * 8f;
	        robot.steer(Math.max(-0.8f, Math.min(0.5f, correction)));
	        robot.forward();
			
			break;
			
		case SWAMP:
			
			this.gui.setVarValue(0, "SWAMP");
			
			if (this.distances[this.distance_fresh] > 0.05f
					&& this.distances[this.distance_old] > 0.05f) {
				this.swampSegment = SwampSegment.NO_SWAMP;
				
				while(!Util.isPressed(Button.DOWN.getId())) {
					this.robot.stop();
				}
			}
			
			this.correction = (this.distances[this.distance_fresh] - 0.04f) * 8f;
	        this.robot.steer(Math.max(-0.8f, Math.min(0.5f, correction)));
	        this.robot.forward();
	        
	        break;
	        
		case ERROR:
			
			this.gui.setVarValue(0, "ERROR");
			
			this.robot.setSpeed(0.2f);
			this.robot.forward();
			
			if (this.error == 5) {
				this.finished = true;
				return;
			}

			if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) {
				this.error++;
			} else {
				this.swampSegment = SwampSegment.NO_SWAMP;
				
				while(!Util.isPressed(Button.DOWN.getId())) {
					this.robot.stop();
				}

				this.robot.setSpeed(1f);
				this.robot.forward();
				this.error = 0;
			}
			
			break;
	        
		}
		
	}

	@Override
	public void reset() {
		this.gui.clearLCD();
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
