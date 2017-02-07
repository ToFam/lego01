package state;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.internal.ev3.EV3Audio;
import robot.Robot;
import robot.RobotComponents;
import sensor.ColorSensor;
import sensor.GyroSensor;
import sensor.TouchSensorBThread;
import sensor.USSensor;
import sensor.modes.ColorSensorMode;
import sensor.modes.GyroSensorMode;
import sensor.modes.UVSensorMode;
import state.SuspBridgeState.S_SuspBridgeState;
import util.MediumMotorTuple;
import util.Util;
import util.lcdGui.LCDGui;

public class Endboss implements ParcourState {
	
	
	public enum EndbossState
	{
		START,
		DRIVE_LEFTWALL_TOBOSS,
		DRIVETOBOSS_RETREAT,
		DRIVETOBOSS_TURN,
		LOWER_SHOOT,
		SHOT,
		UPPER_SHOOT
	}

	
    private Robot robot;
    private LCDGui gui;
    
    
    private float param_robotMaxSpeed = 1f;
    private float param_goalDistance_toboss = 0.1f;
    private float param_robotRetreatSpeed = 0.8f;
    private int param_timeLoweringTheCancnon = 2000;
    private int param_mediumRotateAngle = 250;
    private boolean param_debugWaits = false;

    private boolean end_of_line = false;
    
    public Endboss(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(2, 1);
    }
    
	@Override
	public boolean changeOnBarcode()
	{
		return end_of_line;
	}
    
    @Override
    public boolean changeImmediately()
    {
        return false;
    }
    
    @Override
    public void init() {
    	state = EndbossState.START;
    	
        robot.setSpeed(param_robotMaxSpeed, param_robotMaxSpeed);
        
        gyroSensor = RobotComponents.inst().getGyroSensor();
        uvSensor = RobotComponents.inst().getUS();
        touchSensor = RobotComponents.inst().getTouchSensorB();
    }

    
    public String getName() {
        return "Endboss";
    }
    
    public void reset() {
        
    }
    
    private GyroSensor gyroSensor;
    private USSensor uvSensor;
    private TouchSensorBThread touchSensor;
    
    private EndbossState state;

    private float turn = 0;
    private int mediumStart = 0;
    private int timeLower = 0;
    
    
    
    @Override
    public void update(int elapsedTime) {

    	switch (state)
    	{
    	case START:
    		
    		break;
    	case DRIVE_LEFTWALL_TOBOSS:
    		robot.setSpeed(param_robotMaxSpeed);

    		if (touchSensor.sample()[0] == 1.0f)
    		{
    			robot.stop();
                robot.setSpeed(param_robotRetreatSpeed);
                robot.move(330);
    			state = EndbossState.DRIVETOBOSS_RETREAT;
    		}
    		else
    		{
        		float samp = uvSensor.sample()[0];
                
                turn = (samp - param_goalDistance_toboss) * 25;
                robot.steer(Math.max(-0.8f, Math.min(0.8f, turn)));
                robot.forward();
    		}
    		
    		break;
    	case DRIVETOBOSS_RETREAT:
    		if (robot.finished())
    		{
                robot.turnOnSpot(-90);
                state = EndbossState.DRIVETOBOSS_TURN;
    		}
    		break;
    	case DRIVETOBOSS_TURN:
    		if (robot.finished())
    		{
    			robot.stop();
    			mediumStart = RobotComponents.inst().getMediumMotor().getTachoCount();
    			timeLower = 0;
    			RobotComponents.inst().getMediumMotor().setSpeed(RobotComponents.inst().getMediumMotor().getMaxSpeed() * 0.1f);
    			RobotComponents.inst().getMediumMotor().rotateTo(-param_mediumRotateAngle, true);
    			state = EndbossState.LOWER_SHOOT;
    		}
    		break;
    	case LOWER_SHOOT:
    		if (timeLower * elapsedTime >= param_timeLoweringTheCancnon)
    		{
    			RobotComponents.inst().getMediumMotor().stop();
    			timeLower = 0;
    			RobotComponents.inst().getMediumMotor().rotateTo(mediumStart, true);
    			state = EndbossState.UPPER_SHOOT;
    		}
    		else
    		{
    			timeLower++;
    		}
    		break;
    	case UPPER_SHOOT:
    		
    		break;
    	}
    }
}
