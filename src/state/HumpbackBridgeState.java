package state;

import lcdGui.LCDGui;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.UVSensorMode;

public class HumpbackBridgeState implements ParcourState {

	private final Robot robot;
	private final LCDGui gui;
	private final float SPEED_MAX;
	private float SPEED_LEFT;
	private float SPEED_RIGHT;
	private final float THRESHOLD;
	private final int PAST;
	private int SEGMENT_COUNT;
	
	private BridgeSegment bridgeSegment;
	
	private float[] heights;
	private int current;
	
	private enum BridgeSegment {
		RAMP_UP,
		PLANK,
		RAMP_DOWN,
	}
	
	public HumpbackBridgeState(Robot robot) {
		this.robot = robot;
		this.gui = new LCDGui(0, 0);
		this.SPEED_MAX = 1.f;
		this.SPEED_LEFT = this.SPEED_MAX;
		this.SPEED_RIGHT = this.SPEED_MAX;
		this.THRESHOLD = 0.1f;
		this.PAST = 1;
		this.SEGMENT_COUNT = 0;
		
		this.bridgeSegment = BridgeSegment.RAMP_UP;
		
		this.heights = new float[10000];
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
		this.robot.stop();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.robot.forward();
		this.heights[this.current] = RobotComponents.inst().getUV().sample()[0];
		gui.writeLine(this.bridgeSegment.toString() + ": " + this.heights[this.current]);
		this.current++;
		
		switch (this.bridgeSegment) {
		case RAMP_UP:
			
			if (this.heights[(this.current - this.PAST) % this.heights.length] == this.heights[0]) {
				
				if (this.SEGMENT_COUNT == 3 ) {
					
					this.bridgeSegment = BridgeSegment.PLANK;
					this.SEGMENT_COUNT = 0;
					this.SPEED_RIGHT = this.SPEED_MAX;
					this.robot.setSpeed(this.SPEED_MAX);
					this.robot.forward();
					
				} else {
					
					this.SEGMENT_COUNT++;
				
				}
				
			}
			
			if (this.heights[0] > this.THRESHOLD) {
				
				this.slowDownRightMotor();
			
			} else {
				
				this.speedUpRightMotor();
				
			}
		
			break;
			
		case PLANK:
			
			if (this.heights[0] < this.heights[(this.current - this.PAST) % this.heights.length]) {
				
				if (this.SEGMENT_COUNT == 3) {
					
					this.bridgeSegment = BridgeSegment.RAMP_DOWN;
					this.SEGMENT_COUNT = 0;
					this.SPEED_LEFT = this.SPEED_MAX;
					this.robot.setSpeed(this.SPEED_MAX);
					this.robot.forward();
					
				} else {
					
					this.SEGMENT_COUNT++;
				}
				
			}
			
			if (this.heights[0] > this.THRESHOLD) {
					
				this.speedUpLeftMotor();
			
			} else {
				
				this.slowDownLeftMotor();
				
			}
			
			
			break;
		case RAMP_DOWN:
			
			if (this.heights[(this.current - this.PAST) % this.heights.length] == this.heights[0]) {
				
				if (this.SEGMENT_COUNT == 3 ) {
					
					this.SPEED_RIGHT = this.SPEED_MAX;
					this.robot.setSpeed(this.SPEED_MAX);
					this.robot.forward();
					
				} else {
					
					this.SEGMENT_COUNT++;
				
				}
				
			}
			
			if (this.heights[0] > this.THRESHOLD) {
				
				this.slowDownRightMotor();
			
			} else {
				
				this.speedUpRightMotor();
				
			}
		
			break;
		}
	}

	@Override
	public void reset() {
		this.robot.stop();
	}
	
	private void speedUpLeftMotor() {
		this.SPEED_LEFT = Math.min(this.SPEED_LEFT + 1, this.SPEED_MAX);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}
	
	private void speedUpRightMotor() {
		this.SPEED_RIGHT = Math.min(this.SPEED_RIGHT + 1, this.SPEED_MAX);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}
	
	private void slowDownLeftMotor() {
		this.SPEED_LEFT = Math.max(this.SPEED_LEFT - 1, 0.f);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_MAX);
		robot.forward();
	}
	
	private void slowDownRightMotor() {
		this.SPEED_RIGHT = Math.max(this.SPEED_RIGHT - 1, 0.f);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_MAX);
		robot.forward();
	}

}
