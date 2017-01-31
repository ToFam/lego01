package state;

import lcdGui.LCDGui;
import robot.Robot;

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
		
	}

	@Override
	public void update() {
		
		gui.writeLine("");
	}

}
