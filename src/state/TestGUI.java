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
    
    public TestGUI(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(4,2);
    }
    
    private LCDChooseList chooseList;

    public String getName() {
        return "GUITest";
    }
    
    @Override
    public void init() {
    	chooseList = new LCDChooseList(new String[] {"Number 1", "Second", "Dr3i", "Vier", "5", "6", "7", "8", "9"});
    }

    public void reset() {
        
    }

    @Override
    public void update(int elapsedTime) {

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
