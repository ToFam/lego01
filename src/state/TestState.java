package state;

import lcdGui.LCDGui;
import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import sensor.modes.GyroSensorMode;
import util.Util;

public class TestState implements ParcourState {
    
    private Robot robot;
    private LCDGui gui;
    
    public TestState(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(4,2);
    }
    
    public String getName() {
        return "Test";
    }

    @Override
    public void init() {
        robot.setSpeed(1f);
        RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
        RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RGB.getIdf());
        RobotComponents.inst().getUV().setMode(0);
    }

    public void reset() {
        
    }

    @Override
    public void update(int elapsedTime) {

    	if (RobotComponents.inst().getColorSensor().sampleSize() >= 3)
    	{
    		//gui.writeLine(String.valueOf(RobotComponents.inst().getColorSensor().sample()[0]), 0);
    		//gui.writeLine(String.valueOf(RobotComponents.inst().getColorSensor().sample()[1]), 1);
    		//gui.writeLine(String.valueOf(RobotComponents.inst().getColorSensor().sample()[2]), 2);
    		//gui.writeLine(String.valueOf(Util.howMuchOnLine(new float[] {RobotComponents.inst().getColorSensor().sample()[0], RobotComponents.inst().getColorSensor().sample()[1], RobotComponents.inst().getColorSensor().sample()[2]})), 0);
    		gui.setVarValue(0, Util.howMuchOnLine(new float[] {RobotComponents.inst().getColorSensor().sample()[0], RobotComponents.inst().getColorSensor().sample()[1], RobotComponents.inst().getColorSensor().sample()[2]}));
    	}
    	
    	gui.setVarValue(1, RobotComponents.inst().getUV().sample()[0]);
    	
    	
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
