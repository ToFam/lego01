package state;

import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import sensor.modes.GyroSensorMode;
import sensor.modes.UVSensorMode;
import state.LabyrinthState.State;
import util.lcdGui.LCDGui;

public class HumpbackBridgeState implements ParcourState {

	private final Robot robot;
	private final LCDGui gui;
	private final float SPEED_MAX;
	private final float SPEED_MIN_LEFT;
	private final float SPEED_MIN_RIGHT;
	private float SPEED_LEFT;
	private float SPEED_RIGHT;
	private final float MINUS_LEFT_DELTA;
	private final float MINUS_RIGHT_DELTA;
	private final float TURN_INCREASE;
	private float turn_delta;
	private final float THRESHOLD_NO_GROUND;
	
	private final int GYRO_ALARM;
	private float angle_no_cliff;
	private float[] angles;
	private int angle_fresh;
	private int angle_old;
	private boolean turning;
	
	private BridgeSegment bridgeSegment;
	
	private float distance;
	private float correction;

	private float color;
	
	private enum BridgeSegment {
		PRE_COLLISION,
		COLLISION,
		POST_COLLISION,
		FOLLOW_PLANK,
		ENTIRE_BRIDGE,
		TEST_SENSOR,
	}
	
	public HumpbackBridgeState(Robot robot) {
		this.robot = robot;
		this.gui = new LCDGui(3, 1);
		this.SPEED_MAX = 1f;
		this.SPEED_MIN_LEFT = .6f;
		this.SPEED_MIN_RIGHT = .5f;
		this.MINUS_LEFT_DELTA = .3f;
		this.MINUS_RIGHT_DELTA = .5f;
		this.TURN_INCREASE = 0.05f;
//		this.TURN_INCREASE = 0.00f;
		this.turn_delta = 0.f;
		this.THRESHOLD_NO_GROUND = .04f;
		
		this.GYRO_ALARM = 3;
		this.angles = new float[50];
		this.turning = false;
		this.correction = 0.f;
		
		this.color = 0.f;
	}
//	timer, der cliff sieht und in liste zur ausgabe/manuelle Abstimmung ausgibt.
//	Schärfere Linkskurve zulassen.
	@Override
	public boolean changeOnBarcode()
	{
		return true;
	}
	
	@Override
	public boolean changeImmediately()
	{
	    return false;
	}
	
	@Override
	public String getName() {
		return "HumpbackBridge";
	}
	@Override
	public void init() {
		
        RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
		RobotComponents.inst().getUV().setMode(UVSensorMode.DISTANCE.getIdf());
		RobotComponents.inst().getUV().setMedianFilter(1);
		RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RED.getIdf());
		
		this.angle_no_cliff = RobotComponents.inst().getGyroSensor().sample()[0];
		for (int i = 0; i < this.angles.length; i++) {
			this.angles[i] = this.angle_no_cliff;
		}
		this.angle_fresh = 0;
		this.angle_old = this.angles.length - 1;

		this.SPEED_LEFT = this.SPEED_MAX;
		this.SPEED_RIGHT = this.SPEED_MAX;
		this.turn_delta = -this.TURN_INCREASE;
		
//		this.bridgeSegment = BridgeSegment.PRE_COLLISION;
		this.bridgeSegment = BridgeSegment.ENTIRE_BRIDGE;
		this.robot.lowerUV();
//		this.bridgeSegment = BridgeSegment.TEST_SENSOR;
		
		this.robot.setSpeed(this.SPEED_MAX);
		this.robot.forward();
		
	}

	@Override
	public void update(int elapsedTime) {
		
		this.angle_fresh = (this.angle_fresh + 1) % this.angles.length;
		this.angle_old = (this.angle_old + 1) % this.angles.length;
		this.angles[angle_fresh] = RobotComponents.inst().getGyroSensor().sample()[0];
		
		if (turning) {
			
			if (!this.robot.finished()) {
				return;
			}
			
			float temp = RobotComponents.inst().getGyroSensor().sample()[0];
			for (int i = 0; i < this.angles.length; i++) {
				this.angles[i] = temp;
			}
			
			turning = false;
			
		}
		
		switch (this.bridgeSegment) {
		case PRE_COLLISION:
			
			if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) 
            {
                this.robot.stop();
                this.robot.setSpeed(this.SPEED_MAX);
                this.robot.move(300);
                this.bridgeSegment = BridgeSegment.COLLISION;
            }
			
			break;
			
		case COLLISION:
			
			if (this.robot.finished())
            {
                this.robot.turnOnSpot(-80);
                this.bridgeSegment = BridgeSegment.POST_COLLISION;
            }
			
			break;
			
		case POST_COLLISION:
			
			if (this.robot.finished())
			{
				this.bridgeSegment = BridgeSegment.FOLLOW_PLANK;
			}
			
			break;
			
		case FOLLOW_PLANK:
			
			do {
				this.distance = RobotComponents.inst().getUV().sample()[0];
			} while (this.distance == Float.POSITIVE_INFINITY);
			
			if (this.distance > .5f)
			{
				this.robot.lowerUV();
				this.robot.setSpeed(this.SPEED_MAX);
				this.robot.forward();
				while (!this.robot.UVisDown()) {}
				this.bridgeSegment = BridgeSegment.ENTIRE_BRIDGE;
			}
			else
			{
				this.correction = (this.distance - 0.08f) * 8f;
                robot.steer(Math.max(-0.8f, Math.min(0.8f, correction)));
	            robot.forward();
			}
			
			break;
			
		case ENTIRE_BRIDGE:
			
			this.color = RobotComponents.inst().getColorSensor().sample()[0];
			
			if (this.color > 0.8f) {
				this.robot.stop();
			}
			
			do {
				this.distance = RobotComponents.inst().getUV().sample()[0];
			} while (this.distance == Float.POSITIVE_INFINITY);
			
			if (Math.abs(this.angles[this.angle_old] - this.angles[this.angle_fresh]) > this.GYRO_ALARM) {
				
				this.turning = true;
				this.gui.setVarValue(0, this.angles[this.angle_old] - this.angles[this.angle_fresh]);
				this.robot.turnOnSpot(this.angles[this.angle_old] - this.angles[this.angle_fresh]);
				return;
				
			}

			if (this.distance > this.THRESHOLD_NO_GROUND) {
				
//				if (!this.cliff) {
//					
////					if ((this.angle_no_cliff - this.angles[this.angle_fresh]) > 20) {
////						RobotComponents.inst().getRightMotor().rotate(100, false);
////					}
//					
//					this.turn_delta = -this.TURN_INCREASE;
//					this.SPEED_LEFT = this.SPEED_MAX;
//					this.cliff = true;
//				}
				
				this.SPEED_LEFT = this.SPEED_MAX;
				this.slowDownRightMotor();
			
			} else {
				
//				if (this.cliff) {
//
//					this.turn_delta = -this.TURN_INCREASE;
//					this.angle_no_cliff = this.angles[this.angle_fresh];
//					this.SPEED_RIGHT = this.SPEED_MAX;
//					this.cliff = false;
//					
//				}
				
				this.SPEED_RIGHT = this.SPEED_MAX;
				this.slowDownLeftMotor();
				
			}
		
			break;
			
		case TEST_SENSOR:
			
			this.robot.stop();
			gui.setVarValue(0, color);
			
			break;
		}
	}

	@Override
	public void reset() {
		this.robot.raiseUV();
		this.robot.stop();
		
		while (!this.robot.UVisUp()) {}
	}
	
	private void slowDownLeftMotor() {
//		this.turn_delta += this.TURN_INCREASE;
		this.SPEED_LEFT = Math.max(this.SPEED_LEFT - this.MINUS_LEFT_DELTA - this.turn_delta, this.SPEED_MIN_LEFT);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}
	
	private void slowDownRightMotor() {
		this.turn_delta += this.TURN_INCREASE;
		this.SPEED_RIGHT = Math.max(this.SPEED_RIGHT - this.MINUS_RIGHT_DELTA - this.turn_delta, this.SPEED_MIN_RIGHT);
		robot.setSpeed(this.SPEED_LEFT, this.SPEED_RIGHT);
		robot.forward();
	}

}
