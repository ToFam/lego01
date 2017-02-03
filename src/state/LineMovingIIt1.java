package state;

import lcdGui.LCDGui;
import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import util.MediumMotorTuple;
import util.Util;

public class LineMovingIIt1  implements ParcourState {

    private Robot robot;
    private LCDGui gui;
    
    
    private float param_robotMaxSpeed = 0.4f;
    
    public LineMovingIIt1(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(4,2);
    }
    
    @Override
    public void init() {
        gui = new LCDGui(4, 1);
    	
        RobotComponents.inst().getMediumMotor().setSpeed(RobotComponents.inst().getMediumMotor().getMaxSpeed() * 1f);
        robot.setSpeed(param_robotMaxSpeed, param_robotMaxSpeed);
        RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RED.getIdf());
        RobotComponents.inst().getColorSensor().setMedianFilter(1);
        RobotComponents.inst().getMediumMotor().resetTachoCount();
    }

    
    public String getName() {
        return "MediTest";
    }
    
    public void reset() {
        RobotComponents.inst().getMediumMotor().rotateTo(0, true);
    }
    
    @Override
    public void update(int elapsedTime) {

    	
    	
		gui.setVarValue(0,  RobotComponents.inst().getColorSensor().sample()[0], 5);
    	
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
            RobotComponents.inst().getMediumMotor().rotateTo(-210, true);
        }
        
        if (Util.isPressed(Button.ID_RIGHT))
        {
            RobotComponents.inst().getMediumMotor().rotateTo(0, true);
        }
        
        if (Util.isPressed(Button.ID_DOWN))
        {
            robot.backward();
        }
        
    }
}
