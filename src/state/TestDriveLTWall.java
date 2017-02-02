package state;

import lcdGui.LCDGui;
import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.UVSensorMode;
import util.Util;

public class TestDriveLTWall implements ParcourState {
    
    private Robot robot;
    private LCDGui gui;
    
    public TestDriveLTWall(Robot robot, LCDGui gui) {
        this.robot = robot;
        this.gui = gui;
    }

    @Override
    public void init() {
        robot.setSpeed(1f);
        RobotComponents.inst().getUV().setMode(UVSensorMode.DISTANCE.getIdf());
        
        gui = new LCDGui(4, 2);
    }

    @Override
    public void update(int elapsedTime) {

    	
    	
    	gui.setVarValue(0, RobotComponents.inst().getUV().sample()[0]);
    	
    	
    }
}
