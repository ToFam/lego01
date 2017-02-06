package state;

import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.ColorSensor;
import sensor.GyroSensor;
import sensor.modes.ColorSensorMode;
import sensor.modes.GyroSensorMode;
import util.MediumMotorTuple;
import util.Util;
import util.lcdGui.LCDGui;

public class NewLineState implements ParcourState {

    private Robot robot;
    private LCDGui gui;
    
    
    private float param_robotMaxSpeed = 1f;
    private int param_colorFilterSize = 4;
    private int param_gyroFilterSize = 4;
    private float param_redThreshhold = 0.27f;
    //private float[] param_searchAngles = new float[] {6f, 10f, 16f, 24f, 32f, 64f, 80f, 100f, 120f, 140f};
    private float[] param_searchAngles = new float[] {8f, 18f, 40f, 100f, 120f};
    private float param_angleWhenToTurnWithMaxSpeed = 50f;
    private float param_minTurnSpeed = 0.5f;
    private float param_maxTurnSpeed = 1f;
    private boolean param_debugWaits = false;
    
    private float mes_left_middle = 120f;
    private float mes_right_middle = 71f;
    private float mes_left_left = 90f;
    private float mes_right_left = 285f;
    private float mes_left_right = 308f;
    private float mes_right_right = 270f;
    
    private float measure_left_left = 90;
    private float measure_left_right = 148 + 7;
    private float measure_right_left = 314 + 7;
    private float measure_right_right = 271;
    
    private boolean end_of_line = false;
    
    public NewLineState(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(2,2);
    }
    
	@Override
	public boolean changeOnBarcode()
	{
		return end_of_line;
	}
    
    @Override
    public boolean changeImmediately()
    {
        return false;
    }
    
    @Override
    public void init() {
        gui = new LCDGui(2, 2);
        
        robot.setSpeed(param_robotMaxSpeed, param_robotMaxSpeed);
    	
        RobotComponents.inst().getMediumMotor().setSpeed(RobotComponents.inst().getMediumMotor().getMaxSpeed() * 0.7f);
        RobotComponents.inst().getMediumMotor().resetTachoCount();
        RobotComponents.inst().getMediumMotor().forward();
        
        RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RED.getIdf());
        RobotComponents.inst().getColorSensor().setMedianFilter(param_colorFilterSize);
        
        colorSensor = RobotComponents.inst().getColorSensor();
    }

    
    public String getName() {
        return "New Line";
    }
    
    public void reset() {
        //RobotComponents.inst().getMediumMotor().rotateTo(0, true);
    }
    
    private ColorSensor colorSensor;
    MediumMotorTuple[] measureVals = new MediumMotorTuple[10000];
    int measureCount = 0;
    
    
    MediumMotorTuple[] tups = new MediumMotorTuple[10000];
    int tupsCount = 0;
    int state = 0;
    // 0 = toRight
    // 1 = toLeft
    
    @Override
    public void update(int elapsedTime)
    {
    	int tacho = RobotComponents.inst().getMediumMotor().getTachoCount() % 360;
    	float col = colorSensor.instSample()[0];
    	
    	switch (state)
    	{
    	case 0:
    		if (tacho < 180)
    		{
    			tups[tupsCount] = new MediumMotorTuple(col, tacho);
    			tupsCount++;
    		}
    		else
    		{
    			float peak0 = getMediamOfBorders();
    			float error = getError(peak0);
    			//gui.setVarValue(0, error, 7);
    			
    			measureVals[measureCount] = new MediumMotorTuple(error, measureCount);
    			measureCount++;
    			tupsCount = 0;
    			state = 1;
    		}
    		break;
    	case 1:
    		if (tacho < 360 && tacho >= 180)
    		{
    			tups[tupsCount] = new MediumMotorTuple(col, tacho);
    			tupsCount++;
    		}
    		else
    		{
    			float peak1 = getMediamOfBorders();
    			float error = getError(peak1);
    			//gui.setVarValue(0, error, 7);
    			
    			measureVals[measureCount] = new MediumMotorTuple(error, measureCount);
    			measureCount++;
    			tupsCount = 0;
    			state = 0;
    		}
    		break;
    	case -1:
    		
    		break;
    	}
    	
    	
    	if (Util.isPressed(Button.ID_UP))
    	{
    		state = -1;
    		RobotComponents.inst().getMediumMotor().stop();
    		for (int i = 0; i < measureCount; i++)
            {
            	System.out.println(String.valueOf(measureVals[i].getF2()) + "=" + String.valueOf(measureVals[i].getF1()));
        		
            }
        	System.out.println("Measures end");
    	}
    	
        
    }
    
    private float getMediamOfBorders()
    {
    	int tachoLeft = -1;
    	int tachoRight = -1;
    	
    	for (int i = 0; i < tupsCount; i++)
    	{
    		if (tups[i].getF1() > param_redThreshhold && tachoLeft == -1)
    		{
    			tachoLeft = tups[i].getF2();
    		}
    		
    		if (tups[i].getF1() < param_redThreshhold && tachoLeft != -1)
    		{
    			tachoRight = tups[i].getF2();
    		}
    	}
    	
    	if (tachoLeft != -1 && tachoRight == -1)
    	{
    		tachoRight = tups[tupsCount - 1].getF2();
    	}
    	
    	return (tachoRight - tachoLeft) * 0.5f + tachoLeft;
    }
    
    private float getError(float median)
    {
    	float error = 0f;
    	if (median <= 180)
    	{
    		float a = measure_left_right - measure_left_left;
    		error = (median - measure_left_left) / a;
    		error = error * 2f - 1f;
    		error = error * -1f;
    	}
    	else
    	{
    		float a = measure_right_left - measure_right_right;
    		error = (median - measure_right_right) / a;
    		error = error * 2f - 1f;
    	}
    	
    	return error;
    }
    
    
    private float errorFromMedianLeftToRight(float median)
    {
    	float error = 2f;
    	if (median < mes_left_middle)
    	{
    		float a = mes_left_middle - mes_left_left;
    		error = a / (mes_left_middle - median);
    		//error = error;		// Das - ist Absicht und stimmt so
    	}
    	else
    	{
    		float a = mes_left_right - mes_left_middle;
    		error = a / (median - mes_left_middle);
    		error = -1f * error;	// Das * ist Absicht und stimmt so
    	}
    	
    	return error;
    }
    
    private float errorFromMedianRightToLeft(float median)
    {
    	float error = 2f;
    	if (median < mes_right_middle)
    	{
    		float a = mes_right_middle - mes_right_right;
    		error = a / (mes_right_middle - median);
    		error = -1f * error;		// Das - ist Absicht und stimmt so
    	}
    	else
    	{
    		float a = mes_right_left - mes_right_middle;
    		error = a / (median - mes_right_middle);
    		//error = -1f * error;	// Das * ist Absicht und stimmt so
    	}
    	
    	return error;
    }
    
    
    private float degreeFac = 360f / 45f;
    private void turnRobotDegrees(float degrees)
    {
    	float turnSpeed = degrees / param_angleWhenToTurnWithMaxSpeed;
    	
    	turnSpeed = (turnSpeed < param_minTurnSpeed ? param_minTurnSpeed : (turnSpeed > param_maxTurnSpeed ? param_maxTurnSpeed : turnSpeed));
    	
    	turnSpeed = 1f;
    	
    	robot.setSpeed(turnSpeed, turnSpeed);
    	RobotComponents.inst().getLeftMotor().rotate((int) (degrees * degreeFac), true);
    	RobotComponents.inst().getRightMotor().rotate((int) (-degrees * degreeFac), true);
    }
    
    private void turnRobotDegreesGyro(float degrees)
    {
    	robot.turnOnSpot(degrees);
    }
}
