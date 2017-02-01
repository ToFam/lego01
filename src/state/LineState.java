package state;

import lcdGui.LCDGui;
import robot.Robot;
import sensor.modes.ColorSensorMode;

public class LineState implements ParcourState {
	
    private Robot robot;
    private LCDGui gui;
    
    public LineState(Robot robot, LCDGui gui) {
        this.robot = robot;
        this.gui = gui;
    }

	@Override
	public void init() {
        robot.setSpeed(1f);
        robot.setColorMode(ColorSensorMode.AMBIENT);
	}

	@Override
	public void update() {

	}

}
