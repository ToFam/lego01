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
	private final float MINUS_LEFT_DELTA;
	private final float MINUS_RIGHT_DELTA;
	private final float PLUS_LEFT_DELTA;
	private final float PLUS_RIGHT_DELTA;
	private final float THRESHOLD_NO_GROUND;
	private final float THRESHOLD_CLIFF;
	private final int PAST;
	private int SEGMENT_COUNT;
	
	private BridgeSegment bridgeSegment;
	
	private float[] heights;
	private int current;
	
	private enum BridgeSegment {
		SEGMENTS_NOT_USED,
		RAMP_UP,
		PLANK,
		RAMP_DOWN,
	}
	
	public HumpbackBridgeState(Robot robot) {
		this.robot = robot;
		this.gui = new LCDGui(2, 1);
		this.SPEED_MAX = 1f;
		this.SPEED_LEFT = this.SPEED_MAX;
		this.SPEED_RIGHT = this.SPEED_MAX;
		this.MINUS_LEFT_DELTA = 0.02f;
		this.MINUS_RIGHT_DELTA = 0.02f;
		this.PLUS_LEFT_DELTA = 0.05f;
		this.PLUS_RIGHT_DELTA = 0.05f;
		this.THRESHOLD_NO_GROUND = .04f;
		this.THRESHOLD_CLIFF = 0.39f;
		this.PAST = 10;
		this.SEGMENT_COUNT = 0;
		
		this.bridgeSegment = BridgeSegment.RAMP_UP;
		
		this.heights = new float[1000];
	}
	@Override
	public boolean changeOnBarcode()
	{
		return true;
	}
	
	@Override
	public String getName() {
		return "HumpbackBridge";
	}

	@Override
	public void init() {
		this.robot.lowerUV();
		RobotComponents.inst().getUV().setMode(UVSensorMode.DISTANCE.getIdf());
		RobotComponents.inst().getUV().setMedianFilter(1);
		
		for (int i = 0; i < this.PAST + 1; i++) {
			this.heights[0] = RobotComponents.inst().getUV().sample()[0];
		}
		
		this.current = this.PAST;
		this.bridgeSegment = BridgeSegment.SEGMENTS_NOT_USED;
		
		while (!this.robot.UVisDown()) {}
	}

	@Override
	public void update(int elapsedTime) {
		do {
		this.heights[this.current] = RobotComponents.inst().getUV().sample()[0];
		} while (this.heights[this.current] == Float.POSITIVE_INFINITY);
//		this.current++;
		gui.setVarValue(0, this.bridgeSegment.toString());
//		gui.setVarValue(0, String.valueOf(current));
		gui.setVarValue(1, String.valueOf(this.heights[this.current]), 5);
		
		switch (this.bridgeSegment) {
		case SEGMENTS_NOT_USED:
			
			if (this.heights[this.current] > this.THRESHOLD_NO_GROUND) {
				
				this.SPEED_LEFT = this.SPEED_MAX;
				this.slowDownRightMotor();
			
			} else {
				
				this.SPEED_RIGHT = this.SPEED_MAX;
				this.slowDownLeftMotor();
				
			}
		
			break;
			
		case RAMP_UP:
			
			if (this.heights[(this.current + this.heights.length - this.PAST) % this.heights.length] > this.THRESHOLD_CLIFF
					&& this.heights[(this.current + this.heights.length - this.PAST) % this.heights.length] == this.heights[this.current]) {
				
				if (this.SEGMENT_COUNT == 10) {
					
					this.bridgeSegment = BridgeSegment.PLANK;
					this.SEGMENT_COUNT = 0;
					
				} else {
					
					this.SEGMENT_COUNT++;
					
				}
				
			} else if (this.heights[(this.current + this.heights.length - this.PAST) % this.heights.length] > this.heights[this.current]) {
					
				if (this.SEGMENT_COUNT == 10) {
					
					this.bridgeSegment = BridgeSegment.RAMP_DOWN;
					this.SEGMENT_COUNT = 0;
					
				} else {
					
					this.SEGMENT_COUNT++;
				}
					
			} else {
				this.SEGMENT_COUNT = 0;
			}
			
			if (this.heights[this.current] > this.THRESHOLD_NO_GROUND) {
				
				this.SPEED_LEFT = this.SPEED_MAX;
				this.slowDownRightMotor();
			
			} else {
				
				this.SPEED_RIGHT = this.SPEED_MAX;
				this.slowDownLeftMotor();
				
			}
		
			break;
			
		case PLANK:
			
			if (this.heights[(this.current + this.heights.length - this.PAST) % this.heights.length] < this.THRESHOLD_CLIFF
//					&& this.heights[(this.current + this.heights.length - this.PAST) % this.heights.length] == this.heights[this.current]
							) {
				
				if (this.SEGMENT_COUNT == 10) {
					
					this.bridgeSegment = BridgeSegment.RAMP_DOWN;
					this.SEGMENT_COUNT = 0;
					
				} else {
					
					this.SEGMENT_COUNT++;
				}
				
			} else {
				this.SEGMENT_COUNT = 0;
			}
			
			if (this.heights[this.current] > this.THRESHOLD_NO_GROUND) {
					
				this.speedUpLeftMotor();
				this.slowDownRightMotor();
			
			} else {
				
				this.SPEED_RIGHT = this.SPEED_MAX;
				this.slowDownLeftMotor();
				
			}
			
			
			break;
		case RAMP_DOWN:
			
			if (this.heights[(this.current + this.heights.length - this.PAST) % this.heights.length] == this.heights[this.current]) {
				
				if (this.SEGMENT_COUNT == 10) {
					
					this.SPEED_RIGHT = this.SPEED_MAX;
					this.robot.setSpeed(this.SPEED_MAX);
					this.robot.forward();
					
				} else {
					
					this.SEGMENT_COUNT++;
				
				}
				
			}
			
			if (this.heights[this.current] > this.THRESHOLD_NO_GROUND) {
				
				this.SPEED_LEFT = this.SPEED_MAX;
				this.slowDownRightMotor();
			
			} else {
				
				this.SPEED_RIGHT = this.SPEED_MAX;
				this.slowDownLeftMotor();
				
			}
		
			break;
		}
		
		this.current = (this.current + 1) % this.heights.length;
	}

	@Override
	public void reset() {
		this.robot.raiseUV();
		this.robot.stop();
		
		while (!this.robot.UVisUp()) {}
	}
	
	private void speedUpLeftMotor() {
		this.SPEED_LEFT = Math.min(this.SPEED_LEFT + this.PLUS_LEFT_DELTA, this.SPEED_MAX);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}
	
	private void speedUpRightMotor() {
		this.SPEED_RIGHT = Math.min(this.SPEED_RIGHT + this.PLUS_RIGHT_DELTA, this.SPEED_MAX);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}
	
	private void slowDownLeftMotor() {
		this.SPEED_LEFT = Math.max(this.SPEED_LEFT - this.MINUS_LEFT_DELTA, this.SPEED_MAX * 0.6f);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}
	
	private void slowDownRightMotor() {
		this.SPEED_RIGHT = Math.max(this.SPEED_RIGHT - this.MINUS_RIGHT_DELTA, this.SPEED_MAX * 0.6f);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}

}
