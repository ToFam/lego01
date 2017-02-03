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
    
    public LineMovingIIt1(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(4,2);
    }

    private int param_mediumMotorOpenRangeAngleToOneDirection = 60;
    private float param_mediumMotorSpeed = 0.7f;
    private float param_colorThresh = 0.17f;
    private int param_bufferSize = 100000;
    
    @Override
    public void init() {
    	maxAngl = param_mediumMotorOpenRangeAngleToOneDirection;
    	buffer = new MediumMotorTuple[param_bufferSize];

        gui = new LCDGui(4, 1);
    	
        robot.setSpeed(param_mediumMotorSpeed);
        RobotComponents.inst().getMediumMotor().setSpeed(RobotComponents.inst().getMediumMotor().getMaxSpeed() * 0.5f);
        robot.setSpeed(0.4f, 0.4f);
        RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RGB.getIdf());
        RobotComponents.inst().getColorSensor().setMedianFilter(1);
        RobotComponents.inst().getMediumMotor().resetTachoCount();
    }

    
    private int maxAngl = 0;
    
    boolean armMovingLeft = true;
    //float[] buffer;
    MediumMotorTuple[] buffer;
    int bufPos = 0;
    boolean isLeftToLine = true;
    
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
    		//gui.setVarValue(1,  bufPos);
    		
    		//float[] errorAngles = evaluateBuffer(armMovingLeft);

    		//float error = (errorAngles[0] + errorAngles[1]);
    		
    		
    		
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
    	
    	buffer[bufPos] = new MediumMotorTuple(currentLineVal, RobotComponents.inst().getMediumMotor().getTachoCount());
    	bufPos++;
    	
    	
    	
		//gui.setVarValue(0,  currentLineVal);
    	
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
    
    /**
     * Attention: 5000 and -5000 is a error value
     * @param movingToLeft
     * @return
     */
    private float evaluateBuffer(boolean movingToLeft)
    {
    	//int arrayC = movingToLeft ? 0 : bufPos - 1;
    	
    	float medianWhite = 0f;
    	float medianBlack = 0f;
    	int amountAddedWhite = 0;
    	int amountAddedBlack = 0;
    	
    	for (int i = 0; i < bufPos; i++)
    	{
    		if (buffer[i].getF1() >= param_colorThresh)
    		{
    			medianWhite += buffer[i].getF2();
    			amountAddedWhite++;
    		}
    		else
    		{
    			medianBlack += buffer[i].getF2();
    			amountAddedBlack++;
    		}
    	}
    	
    	if (amountAddedWhite == 0)
    	{
    		return 0;
    	}
    	if (amountAddedBlack == 0)
    	{
    		return 0;
    	}
    	
    	medianWhite = medianWhite / amountAddedWhite;
    	//medianWhite = movingToLeft ? medianWhite : -medianWhite;

    	medianBlack = medianBlack / amountAddedBlack;
    	//medianBlack = movingToLeft ? medianBlack : -medianBlack;
    	
    	return medianWhite - medianBlack;
    }
}
