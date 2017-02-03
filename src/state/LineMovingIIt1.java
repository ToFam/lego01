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

public class LineMovingIIt1  implements ParcourState {

    private Robot robot;
    private LCDGui gui;
    
    
    private float param_robotMaxSpeed = 0.8f;
    private int param_colorFilterSize = 4;
    private int param_gyroFilterSize = 4;
    private float param_redThreshhold = 0.5f;
    private float[] param_searchAngles = new float[] {6f, 10f, 16f, 24f, 32f, 64f, 80f, 100f, 120f, 140f};
    //private float[] param_searchAngles = new float[] {30f, 60f, 100f, 140f};
    private float param_angleWhenToTurnWithMaxSpeed = 50f;
    private float param_minTurnSpeed = 0.5f;
    private float param_maxTurnSpeed = 1f;
    private boolean param_debugWaits = false;
    
    public LineMovingIIt1(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(4,2);
    }
    
    @Override
    public void init() {
        gui = new LCDGui(4, 1);
        
        robot.setSpeed(param_robotMaxSpeed, param_robotMaxSpeed);
    	
        RobotComponents.inst().getMediumMotor().setSpeed(RobotComponents.inst().getMediumMotor().getMaxSpeed() * 1f);
        RobotComponents.inst().getMediumMotor().resetTachoCount();
        
        RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RED.getIdf());
        RobotComponents.inst().getColorSensor().setMedianFilter(param_colorFilterSize);
        
        RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
        RobotComponents.inst().getGyroSensor().setMedianFilter(param_gyroFilterSize);
        
        colorSensor = RobotComponents.inst().getColorSensor();
        gyroSensor = RobotComponents.inst().getGyroSensor();
    }

    
    public String getName() {
        return "Static Line";
    }
    
    public void reset() {
        RobotComponents.inst().getMediumMotor().rotateTo(0, true);
    }
    
    private ColorSensor colorSensor;
    private GyroSensor gyroSensor;
    
    private LMState curStat = LMState.SEARCH_LEFT;
    private float lostAngle = 0f;
    private int searchIteration = 0;
    private boolean startTurnLeft = true;
    private float preLastAngle = 0f;
    private float lastAngle = 0f;
    
    @Override
    public void update(int elapsedTime) {

    	//System.out.println("Color=" + String.valueOf(RobotComponents.inst().getColorSensor().sample()[0]));
    	
    	if (curStat == LMState.STRAIGHT_LEFT || curStat == LMState.STRAIGHT_RIGHT)
    	{
    		float colorVal = RobotComponents.inst().getColorSensor().sample()[0];
    		
    		if (colorVal < param_redThreshhold)
    		{
    	    	System.out.println("LostLine");
    			gui.writeLine("Lost line");
    			robot.stop();
    			
    			lostAngle = gyroSensor.sample()[0];
    			searchIteration = 0;
    			curStat = (curStat == LMState.STRAIGHT_LEFT ? LMState.SEARCH_LEFT : LMState.SEARCH_RIGHT);
    			
    			if (curStat == LMState.SEARCH_LEFT)
    			{
    				startTurnLeft = true;
        			//robot.turnOnSpot(param_searchAngles[searchIteration]);
        			turnRobotDegrees(param_searchAngles[searchIteration]);
    			}
    			else 
    			{
    				startTurnLeft = false;
        			//robot.turnOnSpot(-param_searchAngles[searchIteration]);
        			turnRobotDegrees(-param_searchAngles[searchIteration]);
    			}
    			
    			if (param_debugWaits)
    			{
        			gui.writeLine("Wait for DOWN");
        			while(Util.isPressed(Button.ID_DOWN) == false) {}
    			}
    		}
    	}
    	
    	if (curStat == LMState.SEARCH_LEFT || curStat == LMState.SEARCH_RIGHT)
    	{
    		float colorNow = colorSensor.sample()[0];
    		
    		if (colorNow > param_redThreshhold)
    		{
    			curStat = (curStat == LMState.SEARCH_RIGHT ? LMState.STRAIGHT_RIGHT : LMState.STRAIGHT_LEFT);
    			

    			gui.writeLine("Found line");
    			if (param_debugWaits)
    			{
        			gui.writeLine("Wait for DOWN");
        			while(Util.isPressed(Button.ID_DOWN) == false) {}
    			}

    			lastAngle = gyroSensor.sample()[0];
    			float estimatedDiff = lastAngle - preLastAngle;
    			estimatedDiff *= 0.002f;
    			estimatedDiff = estimatedDiff < -1f ? -1f : (estimatedDiff > 1f ? 1f : estimatedDiff);

    			preLastAngle = lastAngle;
    			
    	    	System.out.println("FoundLine. EstDiff=" + String.valueOf(estimatedDiff));
    	    	
    	        //robot.setSpeed(param_robotMaxSpeed, param_robotMaxSpeed);
    	        //robot.setSpeed(1f, 1f);

    	        robot.steerFacSimonTest(estimatedDiff);//estimatedDiff);
    			robot.forward();
    		}
    		else if (RobotComponents.inst().getLeftMotor().isMoving() == false && RobotComponents.inst().getRightMotor().isMoving() == false)
    		{
    			curStat = (curStat == LMState.SEARCH_RIGHT ? LMState.SEARCH_LEFT : LMState.SEARCH_RIGHT);
    			
    			if (curStat == LMState.SEARCH_RIGHT && startTurnLeft == false
    				|| curStat == LMState.SEARCH_LEFT && startTurnLeft)
    			{
    				searchIteration++;
    			}
    			
    			float curGyro = gyroSensor.sample()[0];
    			float turnDegree = param_searchAngles[searchIteration];
    			
    			

    			gui.writeLine("Gonna turn");
    			if (param_debugWaits)
    			{
        			gui.writeLine("Wait for DOWN");
        			while(Util.isPressed(Button.ID_DOWN) == false) {}
    			}
    			
    			if (curStat == LMState.SEARCH_LEFT)
    			{
    				turnDegree = lostAngle - curGyro + param_searchAngles[searchIteration];
    			}
    			else
    			{
    				turnDegree = lostAngle - curGyro - param_searchAngles[searchIteration];
    			}

    			//robot.turnOnSpot(turnDegree);
    			turnRobotDegrees(turnDegree);
    			
    		}
    	}
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
		gui.setVarValue(0,  RobotComponents.inst().getColorSensor().sample()[0], 4);
		gui.setVarValue(1,  RobotComponents.inst().getGyroSensor().sample()[0], 5);
    	
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
            //robot.backward();
        }
        
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
}
