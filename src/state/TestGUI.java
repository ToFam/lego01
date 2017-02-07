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
        this.gui = new LCDGui(4,1);
    }
    
    private LCDChooseList chooseList;

    public String getName() {
        return "Data Test";
    }
    
    @Override
    public void init() {
    	//chooseList = new LCDChooseList(new String[] {"Number 1", "Second", "Dr3i", "Vier", "5", "6", "7", "8", "9"});
    	//RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RED.getIdf());
    	//RobotComponents.inst().getTouchSensorB().setMode(0);
    	//RobotComponents.inst().getUV().setMode(UVSensorMode.DISTANCE.getIdf());
    	//RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
    	
    	//RobotComponents.inst().getMediumMotor().resetTachoCount();
    	//RobotComponents.inst().getMediumMotor().setSpeed(RobotComponents.inst().getMediumMotor().getMaxSpeed() * 0.7f);
        //RobotComponents.inst().getMediumMotor().forward();
    }
    
	@Override
	public boolean changeOnBarcode()
	{
		return false;
	}
    
    @Override
    public boolean changeImmediately()
    {
        return false;
    }

    public void reset() {
        robot.stop();
    }
    
    int sens = 0;
    float[] tach = new float[10000];
    float[] cols = new float[10000];
    int cou = 0;

    @Override
    public void update(int elapsedTime) {

    	float color = 0;//RobotComponents.inst().getColorSensor().instSample()[0];
    	float gyro = 0;//RobotComponents.inst().getGyroSensor().sample()[0];
    	float uv = RobotComponents.inst().getUS().sample()[0];
    	float touch = 0;//RobotComponents.inst().getTouchSensorB().sample()[0];
    	
    	gui.setVarValue(0, String.valueOf(uv), 5);
    	
    	//tach[cou] = RobotComponents.inst().getMediumMotor().getTachoCount() % 360;
    	cols[cou] = color;
    	
    	if (cou < tach.length - 5)
    		cou++;
    	
    	if (cou == 9000)
    	{

        	//System.out.println("9000");
    	}
    	
    	switch (sens)
    	{
    	case 0:
    		//System.out.println("Case 0: " + String.valueOf(uv));
    		break;
    	case 1:
    		//System.out.println(String.valueOf(RobotComponents.inst().getMediumMotor().getTachoCount() % 360) + "=" + String.valueOf(color));
    		break;
    	case 2:
    		//System.out.println("Case 2: " + String.valueOf(gyro));
    		break;
    	case 3:
    		//System.out.println("Case 3: " + String.valueOf(touch));
    		break;
    	}
    	
    	
        if (Util.isPressed(Button.ID_DOWN))
        {
            sens = 0;
        }

        if (Util.isPressed(Button.ID_UP))
        {
            sens = 1;
            /*for (int i = 0; i < tach.length; i++)
            {
            	System.out.println(String.valueOf(tach[i]) + "=" + String.valueOf(cols[i]));
        		
            }*/
            //RobotComponents.inst().getMediumMotor().stop();
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
