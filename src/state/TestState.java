package state;

import lcdGui.LCDGui;
import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.GyroSensorMode;
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
        robot.setSpeed(1f);
        RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
        
        RobotComponents.inst().getMediumMotor().rotate(-25);
    }

    @Override
    public void update() {

		gui.writeLine(String.valueOf(RobotComponents.inst().getGyroSensor().sample()[0]));
    	
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
            robot.turnOnSpot(90);
        }
        
        if (Util.isPressed(Button.ID_RIGHT))
        {
            robot.turnOnSpot(-90);
        }
        
        if (Util.isPressed(Button.ID_DOWN))
        {
            robot.backward();
        }
        
    }
}
