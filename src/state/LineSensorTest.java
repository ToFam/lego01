package state;

import lcdGui.LCDGui;
import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import sensor.modes.GyroSensorMode;
import util.MediumMotorTuple;
import util.Util;

public class LineSensorTest implements ParcourState {

    private Robot robot;
    private LCDGui gui;
    
    public LineSensorTest(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(4,2);
    }

    private int param_mediumMotorOpenRangeAngleToOneDirection = 60;
    private float param_mediumMotorSpeed = 0.7f;
    private float param_colorThresh = 0.17f;
    private float param_leftOffset = -15f;
    private float param_rightOffset = 15f;
    private int param_bufferSize = 100000;
    private float param_kp = 1.0f / 30f;
    
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
    float firstVal = 0f;
    float secondVal = 0f;
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
    		//gui.setVarValue(1,  bufPos);
    		
    		float[] errorAngles = evaluateBuffer(armMovingLeft);

    		float error = (errorAngles[0] + errorAngles[1]);
    		
    		if (armMovingLeft)
    		{
    			firstVal = error;
    			error += param_leftOffset;
    		}
    		else
    		{
    			secondVal = error;
    			error += param_rightOffset;
    			
    			/*float between = (firstVal + secondVal) * 0.5f;
    			
        		gui.setVarValue(0,  between);
        		
        		robot.steerFacSimonTest(between * param_kp, 0.2f);
        		
        		robot.forward();*/
    		}
    		
    		gui.setVarValue(0,  error * param_kp);
    		
    		//robot.steerFacSimonTest(error * param_kp, 0.3f);
    		
    		//robot.forward();
    		
    		//gui.setVarValue(1,  errorAngles[1]);
    		
    		
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
    private float[] evaluateBuffer(boolean movingToLeft)
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
    		return new float[] {0, 5000};
    	}
    	if (amountAddedBlack == 0)
    	{
    		return new float[] {-5000, 0};
    	}
    	
    	medianWhite = medianWhite / amountAddedWhite;
    	//medianWhite = movingToLeft ? medianWhite : -medianWhite;

    	medianBlack = medianBlack / amountAddedBlack;
    	//medianBlack = movingToLeft ? medianBlack : -medianBlack;
    	
    	return new float[] {medianWhite, medianBlack};
    }
}
