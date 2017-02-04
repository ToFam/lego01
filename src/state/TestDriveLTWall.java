package state;

import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.UVSensorMode;
import util.Util;
import util.lcdGui.LCDGui;

public class TestDriveLTWall implements ParcourState {
    
    private Robot robot;
    private LCDGui gui;
    
    public TestDriveLTWall(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(4,2);
    }

    public String getName() {
        return "TestDriveLTWall";
    }

    public void reset() {
        robot.stop();
    }

    @Override
    public void init() {
        robot.setSpeed(1f);
        RobotComponents.inst().getUV().setMode(UVSensorMode.DISTANCE.getIdf());
        RobotComponents.inst().getUV().setMedianFilter(1000);
        
        gui = new LCDGui(4, 2);
    }
    
	@Override
	public boolean changeOnBarcode()
	{
		return true;
	}
    
    private float oldDistance = 0.0f;
    private float turnFactor = 5.0f;

    @Override
    public void update(int elapsedTime) {

    	if (oldDistance == 0.0f)
    		oldDistance = RobotComponents.inst().getUV().sample()[0];
    	
    	float diff = RobotComponents.inst().getUV().sample()[0] - oldDistance;
    	
    	diff *= turnFactor;
    	
    	robot.steer(diff);
    	robot.forward();
    	
    	
    	gui.setVarValue(0, RobotComponents.inst().getUV().sample()[0]);
    	gui.setVarValue(1, RobotComponents.inst().getUV().instSample()[0]);
    	gui.setVarValue(2, diff);
    	
    	
    	
    	
    }
}
