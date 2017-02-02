package state;

import lcdGui.LCDGui;
import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import sensor.modes.GyroSensorMode;
import util.Util;

public class LineSensorTest implements ParcourState {

    private Robot robot;
    private LCDGui gui;
    
    public LineSensorTest(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(4,2);
    }

    private int param_mediumMotorOpenRangeAngleToOneDirection = 50;
    private float param_colorThresh = 0.17f;
    private int param_bufferSize = 100000;
    
    @Override
    public void init() {
    	maxAngl = param_mediumMotorOpenRangeAngleToOneDirection;
    	buffer = new float[param_bufferSize];

        gui = new LCDGui(4, 2);
    	
        robot.setSpeed(0.5f);
        RobotComponents.inst().getMediumMotor().setSpeed(RobotComponents.inst().getMediumMotor().getMaxSpeed() * 0.6f);
        RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RGB.getIdf());
        RobotComponents.inst().getColorSensor().setMedianFilter(5);
        RobotComponents.inst().getMediumMotor().resetTachoCount();
    }

    
    private int maxAngl = 0;
    
    boolean armMovingLeft = true;
    float[] buffer;
    int bufPos = 0;
    boolean isLeftToLine = true;
    int state = 0;
    
    public String getName() {
        return "Line SensTest";
    }
    
    public void reset() {
        RobotComponents.inst().getMediumMotor().rotateTo(0, true);
    }
    
    @Override
    public void update(int elapsedTime) {

    	// Rotation des Arms
    	if (RobotComponents.inst().getMediumMotor().isMoving() == false)
    	{
    		gui.setVarValue(0,  bufPos);
    		
    		float errorAngle = evaluateBuffer(armMovingLeft);
    		
    		
    		
    		if (RobotComponents.inst().getMediumMotor().getTachoCount() > 0)
    		{
    			armMovingLeft = false;
    		}
    		else
    		{
    			armMovingLeft = true;
    		}

        	RobotComponents.inst().getMediumMotor().rotateTo(armMovingLeft ? maxAngl : -maxAngl, true);

        	bufPos = 0;
    	}
    	
    	float currentLineVal = Util.howMuchOnLine(RobotComponents.inst().getColorSensor().sample());
    	
    	buffer[bufPos] = currentLineVal;
    	bufPos++;
    	
    	
    	
		//gui.setVarValue(2,  currentLineVal);
    	
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
    
    private float evaluateBuffer(boolean movingToLeft)
    {
    	int arrayC = movingToLeft ? 0 : bufPos - 1;
    	return 0f;
    }
}
