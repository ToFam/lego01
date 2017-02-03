package state;

import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import sensor.modes.GyroSensorMode;
import sensor.modes.UVSensorMode;
import util.Util;
import util.lcdGui.LCDChooseList;
import util.lcdGui.LCDGui;

public class TestGUI implements ParcourState {
	private Robot robot;
    private LCDGui gui;
    
    public TestGUI(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(4,2);
    }
    
    private LCDChooseList chooseList;

    public String getName() {
        return "DataCollector";
    }
    
    @Override
    public void init() {
    	//chooseList = new LCDChooseList(new String[] {"Number 1", "Second", "Dr3i", "Vier", "5", "6", "7", "8", "9"});
    	RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RED.getIdf());
    	RobotComponents.inst().getTouchSensorB().setMode(0);
    	RobotComponents.inst().getUV().setMode(UVSensorMode.DISTANCE.getIdf());
    	RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
    }

    public void reset() {
        
    }
    
    int sens = 0;

    @Override
    public void update(int elapsedTime) {

    	float color = RobotComponents.inst().getColorSensor().sample()[0];
    	float gyro = RobotComponents.inst().getGyroSensor().sample()[0];
    	float uv = RobotComponents.inst().getUV().sample()[0];
    	float touch = RobotComponents.inst().getTouchSensorB().sample()[0];
    	
    	
    	switch (sens)
    	{
    	case 0:
    		System.out.println("Case 0: " + String.valueOf(uv));
    		break;
    	case 1:
    		System.out.println("Case 1: " + String.valueOf(color));
    		break;
    	case 2:
    		System.out.println("Case 2: " + String.valueOf(gyro));
    		break;
    	case 3:
    		System.out.println("Case 3: " + String.valueOf(touch));
    		break;
    	}
    	
    	
        if (Util.isPressed(Button.ID_DOWN))
        {
            sens = 0;
        }

        if (Util.isPressed(Button.ID_UP))
        {
            sens = 1;
        }
        if (Util.isPressed(Button.ID_LEFT))
        {
            sens = 2;
        }

        if (Util.isPressed(Button.ID_RIGHT))
        {
            sens = 3;
        }

        if (Util.isPressed(Button.ID_ENTER))
        {
        	
        }
        
    }
}
