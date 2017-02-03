package state;

import lcdGui.LCDGui;
import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.ColorSensor;
import sensor.GyroSensor;
import sensor.modes.ColorSensorMode;
import sensor.modes.GyroSensorMode;
import util.MediumMotorTuple;
import util.Util;

public class LineMovingIIt1  implements ParcourState {

    private Robot robot;
    private LCDGui gui;
    
    
    private float param_robotMaxSpeed = 0.4f;
    private int param_colorFilterSize = 4;
    private int param_gyroFilterSize = 4;
    private float param_redThreshhold = 0.5f;
    private float[] param_searchAngles = new float[] {20f, 40f, 60f, 100f, 150f, 180f};
    
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
    
    private LMState curStat = LMState.STRAIGHT_LEFT;
    private float lostAngle = 0f;
    private int searchIteration = 0;
    private boolean startTurnLeft = true;
    
    @Override
    public void update(int elapsedTime) {

    	if (curStat == LMState.STRAIGHT_LEFT || curStat == LMState.STRAIGHT_RIGHT)
    	{
    		float colorVal = RobotComponents.inst().getColorSensor().sample()[0];
    		
    		if (colorVal < param_redThreshhold)
    		{
    			gui.writeLine("Lost line");
    			robot.stop();
    			
    			lostAngle = gyroSensor.sample()[0];
    			searchIteration = 0;
    			curStat = (curStat == LMState.STRAIGHT_LEFT ? LMState.SEARCH_LEFT : LMState.SEARCH_RIGHT);
    			
    			if (curStat == LMState.SEARCH_LEFT)
    			{
    				startTurnLeft = true;
        			robot.turnOnSpot(param_searchAngles[searchIteration]);
    			}
    			else 
    			{
    				startTurnLeft = false;
        			robot.turnOnSpot(-param_searchAngles[searchIteration]);
    			}
    			
    			
    			gui.writeLine("Wait for DOWN");
    			while(Util.isPressed(Button.ID_DOWN) == false) {}
    		}
    	}
    	
    	if (curStat == LMState.SEARCH_LEFT || curStat == LMState.SEARCH_RIGHT)
    	{
    		float colorNow = colorSensor.sample()[0];
    		
    		if (colorNow < param_redThreshhold)
    		{
    			curStat = (curStat == LMState.SEARCH_RIGHT ? LMState.STRAIGHT_LEFT : LMState.STRAIGHT_RIGHT);
    		}
    		else
    		{
    			curStat = (curStat == LMState.SEARCH_RIGHT ? LMState.SEARCH_LEFT : LMState.SEARCH_RIGHT);
    			
    			if (curStat == LMState.SEARCH_RIGHT && startTurnLeft == false
    				|| curStat == LMState.SEARCH_LEFT && startTurnLeft)
    			{
    				searchIteration++;
    			}
    			
    			float curGyro = gyroSensor.sample()[0];
    			float turnDegree = param_searchAngles[searchIteration];
    			
    			

    			gui.writeLine("Wait for DOWN");
    			while(Util.isPressed(Button.ID_DOWN) == false) {}
    			
    			if (curStat == LMState.SEARCH_LEFT)
    			{
    				turnDegree = lostAngle - curGyro + param_searchAngles[searchIteration];
    			}
    			else
    			{
    				turnDegree = lostAngle - curGyro - param_searchAngles[searchIteration];
    			}

    			robot.turnOnSpot(turnDegree);
    			
    			
    		}
    	}
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
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
            //robot.backward();
        }
        
    }
}
