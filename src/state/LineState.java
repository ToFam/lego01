package state;

import lcdGui.LCDGui;
import robot.Robot;
import robot.RobotComponents;
import sensor.WriteBackStorage;
import sensor.modes.ColorSensorMode;

public class LineState implements ParcourState {
	
    private Robot robot;
    private LCDGui gui;
    private WriteBackStorage storage;
    
    public LineState(Robot robot, LCDGui gui, WriteBackStorage storage) {
        this.robot = robot;
        this.gui = gui;
        this.storage = storage;
    }

	@Override
	public void init() {
        robot.setSpeed(1f);
		
	}
	
	boolean firstCall = true;

	@Override
	public void update() {
		
		gui.writeLine(String.valueOf(storage.getColor()));
		
		if (firstCall)
		{
			firstCall = false;
	        RobotComponents.inst().getColorSensor().setCurrentMode("RGB");
		}
	}

}
