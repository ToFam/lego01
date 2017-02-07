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
import util.MediumMotorTuple;
import util.Util;
import util.lcdGui.LCDGui;

public class Endboss implements ParcourState {
	
	
	public enum EndbossState
	{
		START
	}

	
    private Robot robot;
    private LCDGui gui;
    
    
    private float param_robotMaxSpeed = 1f;
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
    
    
    
    @Override
    public void update(int elapsedTime) {

    	switch (state)
    	{
    	case START:
    		
    		break;
    	}
    }
}
