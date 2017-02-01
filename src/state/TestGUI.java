package state;

import lcdGui.LCDChooseList;
import lcdGui.LCDGui;
import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import sensor.modes.GyroSensorMode;
import util.Util;

public class TestGUI implements ParcourState {
	private Robot robot;
    private LCDGui gui;
    
    public TestGUI(Robot robot, LCDGui gui) {
        this.robot = robot;
        this.gui = gui;
    }
    
    private LCDChooseList chooseList;
    
    @Override
    public void init() {
    	chooseList = new LCDChooseList(new String[] {"Number 1", "Second", "Dr3i", "Vier"});
    }

    @Override
    public void update() {

        if (Util.isPressed(Button.ID_DOWN))
        {
            chooseList.moveOneDown();
        }

        if (Util.isPressed(Button.ID_UP))
        {
            chooseList.moveOneUp();
        }

        if (Util.isPressed(Button.ID_ENTER))
        {
            gui = new LCDGui(4, 2);
            gui.writeLine("Started other gui");
            gui.writeLine(chooseList.getCurrentElement());
        }
        
    }
}
