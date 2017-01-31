package state;

import lcdGui.LCDGui;
import robot.Robot;
import sensor.WriteBackStorage;

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

	@Override
	public void update() {
		
		gui.writeLine(String.valueOf(storage.getColor()));
	}

}
