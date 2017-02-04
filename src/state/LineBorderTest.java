package state;

import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import util.MediumMotorTuple;
import util.Util;
import util.lcdGui.LCDGui;

public class LineBorderTest implements ParcourState {

    private Robot robot;
    private LCDGui gui;
    
    public LineBorderTest(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(4,2);
    }

    private int param_mediumMotorOpenRangeAngleToOneDirection = 60;
    private float param_mediumMotorSpeed = 0.7f;
    private float param_colorThresh = 0.17f;
    private int param_bufferSize = 100000;
    private float param_kp = 1.2f;
    
    private int param_TicksUntilFullSteer = 15;
    
    @Override
    public void init() {
    	maxAngl = param_mediumMotorOpenRangeAngleToOneDirection;
    	buffer = new MediumMotorTuple[param_bufferSize];

        gui = new LCDGui(4, 1);
    	
        robot.setSpeed(param_mediumMotorSpeed);
        RobotComponents.inst().getMediumMotor().setSpeed(RobotComponents.inst().getMediumMotor().getMaxSpeed() * 0.5f);
        robot.setSpeed(0.4f, 0.4f);
        RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RGB.getIdf());
        RobotComponents.inst().getColorSensor().setMedianFilter(2);
        RobotComponents.inst().getMediumMotor().resetTachoCount();
    }
    
	@Override
	public boolean changeOnBarcode()
	{
		return true;
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
    
    int timeCounter = 1;
    boolean currOnBlack = true;
    
    public String getName() {
        return "Line Simon";
    }
    
    public void reset() {
        RobotComponents.inst().getMediumMotor().rotateTo(0, true);
        robot.stop();
    }
    
    @Override
    public void update(int elapsedTime)
    {
    	float curr = Util.howMuchOnLine(RobotComponents.inst().getColorSensor().instSample());
    	if (curr < param_colorThresh)
    	{
    		curr = 0.0f;
    		if (currOnBlack == false)
    		{
        		//timeCounter = 1;
    		}
    		currOnBlack = true;
    	}
    	else
    	{
    		curr = 1.0f;
    		if (currOnBlack)
    		{
        		//timeCounter = 1;
    		}
    		currOnBlack = false;
    	}
    	
    	//float err = ((curr * 2 - 1f) * (timeCounter) >= 0 ? timeCounter : -timeCounter) / ((float)param_TicksUntilFullSteer);
    	float err = (curr * 2 - 1f);
    	
    	float steerY = err * param_kp;
		gui.setVarValue(0,  curr);
		gui.setVarValue(1,  steerY);
    	robot.steerFacSimonSpot(steerY, 0.5f);
    	
    	//steerY = steerY > 1f ? 1f : (steerY < -1f ? -1f : steerY);
    	
    	
    	//robot.forward();
    	

		
		if (currOnBlack)
		{
	    	timeCounter++;
	    	timeCounter = timeCounter > param_TicksUntilFullSteer ? param_TicksUntilFullSteer : timeCounter;
		}
		else
		{
			timeCounter--;
	    	timeCounter = timeCounter < -param_TicksUntilFullSteer ? -param_TicksUntilFullSteer : timeCounter;
		}
    	
    	
    	
        if (Util.isPressed(Button.ID_UP))
        {
            robot.forward();
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
