package state;

import robot.Robot;
import robot.RobotComponents;
import sensor.modes.UVSensorMode;
import util.lcdGui.LCDGui;

public class HumpbackBridgeState implements ParcourState {

	private final Robot robot;
	private final LCDGui gui;
	private final float SPEED_MAX;
	private float SPEED_LEFT;
	private float SPEED_RIGHT;
	private final float DELTA;
	private final float THRESHOLD;
	private final int PAST;
	private int SEGMENT_COUNT;
	
	private BridgeSegment bridgeSegment;
	
	private float[] heights;
	private int current;
	
	private enum BridgeSegment {
		FIND_LEFT_CLIFF,
		RAMP_UP,
		PLANK,
		RAMP_DOWN,
	}
	
	public HumpbackBridgeState(Robot robot) {
		this.robot = robot;
		this.gui = new LCDGui(2, 1);
		this.SPEED_MAX = .5f;
		this.SPEED_LEFT = this.SPEED_MAX;
		this.SPEED_RIGHT = this.SPEED_MAX;
		this.DELTA = 0.1f;
		this.THRESHOLD = 0.04f;
		this.PAST = 10;
		this.SEGMENT_COUNT = 0;
		
		this.bridgeSegment = BridgeSegment.RAMP_UP;
		
		this.heights = new float[1000];
	}
	@Override
	public String getName() {
		return "HumpbackBridge";
	}

	@Override
	public void init() {
		RobotComponents.inst().getUV().setMode(UVSensorMode.DISTANCE.getIdf());
		RobotComponents.inst().getUV().setMedianFilter(1);
		
		for (int i = 0; i < this.PAST + 1; i++) {
			this.heights[0] = RobotComponents.inst().getUV().sample()[0];
		}
		
		this.current = this.PAST;
		this.bridgeSegment = BridgeSegment.RAMP_UP;
	}

	@Override
	public void update(int elapsedTime) {
		this.heights[this.current] = RobotComponents.inst().getUV().sample()[0];
		this.current = (this.current + 1) % this.heights.length;
//		this.current++;
		gui.setVarValue(0, this.bridgeSegment.toString());
//		gui.setVarValue(0, String.valueOf(current));
		gui.setVarValue(1, String.valueOf(this.heights[this.current]), 5);
		
		switch (this.bridgeSegment) {
		case FIND_LEFT_CLIFF:
			
			
			
			break;
			
		case RAMP_UP:
			
			if (this.heights[(this.current + this.heights.length - this.PAST) % this.heights.length] == this.heights[0]) {
				
				if (this.SEGMENT_COUNT == 10) {
					
					this.bridgeSegment = BridgeSegment.PLANK;
					this.SEGMENT_COUNT = 0;
					
				} else {
					
					this.SEGMENT_COUNT++;
				
				}
				
			} else {
				this.SEGMENT_COUNT = 0;
			}
			
			if (this.heights[this.current] > this.THRESHOLD) {
				
				this.SPEED_LEFT = this.SPEED_MAX;
				this.slowDownRightMotor();
			
			} else {
				
				this.SPEED_RIGHT = this.SPEED_MAX;
				this.slowDownLeftMotor();
				
			}
		
			break;
			
		case PLANK:
			
			if (this.heights[this.current] < this.heights[(this.current +this.heights.length - this.PAST) % this.heights.length]) {
				
				if (this.SEGMENT_COUNT == 10) {
					
					this.bridgeSegment = BridgeSegment.RAMP_DOWN;
					this.SEGMENT_COUNT = 0;
					
				} else {
					
					this.SEGMENT_COUNT++;
				}
				
			} else {
				this.SEGMENT_COUNT = 0;
			}
			
			if (this.heights[this.current] > this.THRESHOLD) {
					
				this.speedUpLeftMotor();
			
			} else {
				
				this.slowDownLeftMotor();
				
			}
			
			
			break;
		case RAMP_DOWN:
			
			if (this.heights[(this.current + this.heights.length - this.PAST) % this.heights.length] == this.heights[0]) {
				
				if (this.SEGMENT_COUNT == 10) {
					
					this.SPEED_RIGHT = this.SPEED_MAX;
					this.robot.setSpeed(this.SPEED_MAX);
					this.robot.forward();
					
				} else {
					
					this.SEGMENT_COUNT++;
				
				}
				
			}
			
			if (this.heights[this.current] > this.THRESHOLD) {
				
				this.SPEED_LEFT = this.SPEED_MAX;
				this.slowDownRightMotor();
			
			} else {
				
				this.SPEED_RIGHT = this.SPEED_MAX;
				this.slowDownLeftMotor();
				
			}
		
			break;
		}
	}

	@Override
	public void reset() {
		this.robot.stop();
	}
	
	private void speedUpLeftMotor() {
		this.SPEED_LEFT = Math.min(this.SPEED_LEFT + this.DELTA, this.SPEED_MAX);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}
	
	private void speedUpRightMotor() {
		this.SPEED_RIGHT = Math.min(this.SPEED_RIGHT + this.DELTA, this.SPEED_MAX);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}
	
	private void slowDownLeftMotor() {
		this.SPEED_LEFT = Math.max(this.SPEED_LEFT - this.DELTA, this.SPEED_MAX / 2.f);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}
	
	private void slowDownRightMotor() {
		this.SPEED_RIGHT = Math.max(this.SPEED_RIGHT - this.DELTA, this.SPEED_MAX / 2.f);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}

}
