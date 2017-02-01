package state;

import lcdGui.LCDGui;
import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.GyroSensorMode;
import util.Util;

public class LineSlowMode implements ParcourState {

    private Robot robot;
    private LCDGui gui;
    
    public LineSlowMode(Robot robot, LCDGui gui) {
        this.robot = robot;
        this.gui = gui;
    }

    @Override
    public void init() {
        robot.setSpeed(1f);
        RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
    }

    private float param_colorThresh = 0.3f;
    
    boolean isLeftToLine = true;
    int state = 0;
    
    @Override
    public void update() {

    	switch (state)
    	{
    	case 0://Is left to line
    		RobotComponents.inst().getLeftMotor().forward();
    		RobotComponents.inst().getRightMotor().stop();
    		state = 2;
    		break;
    	case 1://Is right to line
    		RobotComponents.inst().getRightMotor().forward();
    		RobotComponents.inst().getLeftMotor().stop();
    		state = 3;
    		break;
    	case 2://WaitToCrossLineFromLeft
    		if (RobotComponents.inst().getColorSensor().sample()[0] > param_colorThresh)
    		{
    			state = 4;
    		}
    		break;
    	case 3:
    		
    		break;
    	case 4://WaitToSeeBlackOnRightSide
    		if (RobotComponents.inst().getColorSensor().sample()[0] <= param_colorThresh)
    		{
    			state = 6;
    		}
    		break;
    	}
    	
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
