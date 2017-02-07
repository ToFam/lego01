package state;

import robot.Robot;
import robot.RobotComponents;
import util.lcdGui.LCDGui;

public class SwampState implements ParcourState {

	private Robot robot;
	private LCDGui gui;
	private float[] distances;
	private int distance_fresh;
	private int distance_old;
	private float recommended;
	private float correction;
	
	private int time;
	private boolean finished;
	
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
			this.finished = true;
			this.robot.stop();
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
		
		do {
			this.distances[this.distance_fresh] = RobotComponents.inst().getUS().sample()[0];
		} while (this.distances[this.distance_fresh] >= 1.f);
		
		if (this.distances[this.distance_fresh] < 0.05f &&
				this.distances[this.distance_old] < 0.05f) {
			this.recommended = 0.04f;
		}
		
		if (this.distances[this.distance_fresh] > 0.05f &&
				this.distances[this.distance_old] > 0.05f) {
			this.recommended = 0.06f;
		}
		
		this.gui.setVarValue(0, this.distances[this.distance_fresh]);
		
		this.correction = (this.distances[this.distance_fresh] - this.recommended) * 8f;
        robot.steer(Math.max(-0.8f, Math.min(0.5f, correction)));
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
