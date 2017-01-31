package state;

import lcdGui.LCDGui;
import lejos.hardware.Button;
import robot.Robot;
import util.Util;

public class TestState implements ParcourState {
    
    private Robot robot;
    private LCDGui gui;
    
    public TestState(Robot robot, LCDGui gui) {
        this.robot = robot;
        this.gui = gui;
    }

    @Override
    public void init() {
        robot.setSpeed(0.5f);
    }

    @Override
    public void update() {
        
        if (Util.isPressed(Button.ID_UP))
        {
            robot.forward();
        }
        
        if (Util.isPressed(Button.ID_ENTER))
        {
            robot.stop();
        }
        
        if (Util.isPressed(Button.ID_LEFT))
        {
            robot.setSpeed(0.5f, 1f);
            robot.forward();
        }
        
        if (Util.isPressed(Button.ID_RIGHT))
        {
            robot.setSpeed(1f, 0.5f);
            robot.forward();
        }
        
        if (Util.isPressed(Button.ID_DOWN))
        {
            robot.backward();
        }
        
    }
}
