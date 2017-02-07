package state;

import robot.Robot;
import robot.RobotComponents;
import util.Util;
import util.lcdGui.LCDGui;

public class HumpbackBridgeState implements ParcourState {

	private final Robot robot;
	private LCDGui gui;
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
	private int time;
	private boolean onBridge;
	
	private int littleCounter = 0;
	private float rampUpAngle = Float.MAX_VALUE;
	private float[] rampUpAngles = new float[100000];
	private int rampUpCount = 0;
	
	private enum BridgeSegment {
		PRE_COLLISION,
		COLLISION,
		POST_COLLISION,
		FOLLOW_PLANK,
		ON_BARCODE,
		ENTIRE_BRIDGE,
		BRIDGE_TRAVERSED,
		
		WAIT_SHORT_TO_TURN
	}
	
	public HumpbackBridgeState(Robot robot) {
		this.robot = robot;
		this.gui = new LCDGui(1, 1);

		this.SPEED_MAX = 1f;
		this.SPEED_MIN_LEFT = .5f;
		this.SPEED_MIN_RIGHT = .2f;
		this.MINUS_LEFT_DELTA = .6f;
		this.MINUS_RIGHT_DELTA = .8f;
		this.TURN_INCREASE = 0.2f;
//		this.TURN_INCREASE = 0.00f;
		this.turn_delta = 0.f;
		this.THRESHOLD_NO_GROUND = .04f;
		
		this.GYRO_ALARM = 7;
		this.angles = new float[200];
		this.turning = false;
		this.correction = 0.f;
		
		this.color = 0.f;
	}
	
	@Override
	public boolean changeOnBarcode()
	{
		return this.onBridge;
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
		
		this.angle_no_cliff = RobotComponents.inst().getGyroSensor().sample()[0];
		for (int i = 0; i < this.angles.length; i++) {
			this.angles[i] = this.angle_no_cliff;
		}
		this.angle_fresh = 0;
		this.angle_old = this.angles.length - 1;

		this.SPEED_LEFT = this.SPEED_MAX;
		this.SPEED_RIGHT = this.SPEED_MAX;
		this.turn_delta = -this.TURN_INCREASE;
		
		this.onBridge = false;
		this.time = 0;
		
//		this.bridgeSegment = BridgeSegment.PRE_COLLISION;
		//this.bridgeSegment = BridgeSegment.ON_BARCODE;
		this.bridgeSegment = BridgeSegment.ENTIRE_BRIDGE;
		this.robot.lowerUS();
		
		this.robot.setSpeed(this.SPEED_MAX);
		this.robot.stop();
		
	}

	@Override
	public void update(int elapsedTime) {
		
		littleCounter++;
		if (littleCounter * elapsedTime > 5000)
		{
			this.onBridge = true;
		}
		
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
		case WAIT_SHORT_TO_TURN:
			if (robot.finished())
			{
				bridgeSegment = BridgeSegment.ENTIRE_BRIDGE;
			}
			break;
		case ENTIRE_BRIDGE:
			
			if (rampUpCount * elapsedTime > 4000)
			{
				rampUpAngle = Util.average(rampUpAngles, rampUpCount);
				gui.writeLine("Set Average!");
			}
			else
			{
				if (rampUpCount < rampUpAngles.length)
				{
					rampUpAngles[rampUpCount] = RobotComponents.inst().getGyroSensor().sample()[0];
					rampUpCount++;
				}
			}
			
			
			this.color = RobotComponents.inst().getColorSensor().sample()[0];
			
			if (this.color > 0.8f) {
				this.bridgeSegment = BridgeSegment.BRIDGE_TRAVERSED;
				return;
			}
			
			/*if (Math.abs(this.angles[this.angle_old] - this.angles[this.angle_fresh]) > this.GYRO_ALARM) {
				
				this.gui.setVarValue(0, "turning");
				
				this.turning = true;
				this.robot.turnOnSpot(this.angles[this.angle_old] - this.angles[this.angle_fresh]);
				return;
				
			}*/
			if (rampUpAngle != Float.MAX_VALUE && Math.abs(RobotComponents.inst().getGyroSensor().sample()[0] - rampUpAngle) > this.GYRO_ALARM)
			{
				robot.turnOnSpotExact(rampUpAngle);
				bridgeSegment = BridgeSegment.WAIT_SHORT_TO_TURN;
			}
			
			this.gui.setVarValue(0, "ENTIRE_BRIDGE");

			this.distance = RobotComponents.inst().getUS().sample()[0];

			if (this.distance >= 1.f) {
				this.robot.move(50);
				this.distance = RobotComponents.inst().getUS().sample()[0];
			}
			
			if (this.distance > this.THRESHOLD_NO_GROUND) {
								
				this.SPEED_LEFT = this.SPEED_MAX;
				this.slowDownRightMotor();
			
			} else {
				
				this.SPEED_RIGHT = this.SPEED_MAX;
				this.slowDownLeftMotor();
				
			}
		
			break;
			
		case ON_BARCODE:
			
			this.gui.setVarValue(0, "ON_BARCODE");
			
			if (!this.robot.USisDown()) {
				return;
			}
			
			this.robot.forward();
			
			this.color = RobotComponents.inst().getColorSensor().sample()[0];
			this.time += elapsedTime;
		
			if (this.color < 0.8f && this.time > 500) {
				this.onBridge = true;
				this.bridgeSegment = BridgeSegment.ENTIRE_BRIDGE;
			}
			
			this.distance = RobotComponents.inst().getUS().sample()[0];

			if (this.distance >= 1.f) {
				this.robot.move(50);
				this.distance = RobotComponents.inst().getUS().sample()[0];
			}
			
			if (this.distance > this.THRESHOLD_NO_GROUND) {
								
				this.SPEED_LEFT = this.SPEED_MAX;
				this.slowDownRightMotor();
			
			} else {
				
				this.SPEED_RIGHT = this.SPEED_MAX;
				this.slowDownLeftMotor();
				
			}
			
			break;
			
		case BRIDGE_TRAVERSED:
			
			this.gui.setVarValue(0, "BRIDGE_TRAVERSED");
			
			break;
		
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
				this.distance = RobotComponents.inst().getUS().sample()[0];
			} while (this.distance == Float.POSITIVE_INFINITY);
			
			if (this.distance > .5f)
			{
				this.robot.lowerUS();
				this.robot.setSpeed(this.SPEED_MAX);
				this.robot.forward();
				while (!this.robot.USisDown()) {}
				this.bridgeSegment = BridgeSegment.ENTIRE_BRIDGE;
			}
			else
			{
				this.correction = (this.distance - 0.08f) * 8f;
                this.robot.steer(Math.max(-0.8f, Math.min(0.8f, correction)));
	            this.robot.forward();
			}
			
			break;
			
		}
	}

	@Override
	public void reset() {
		this.robot.raiseUS();
		this.robot.stop();
		this.gui.clearLCD();
		
//		while (!this.robot.USisUp()) {}
	}
	
	private void slowDownLeftMotor() {
		this.turn_delta = 0.f;
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
