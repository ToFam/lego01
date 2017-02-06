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

public class LineMovingIIt1 implements ParcourState {
	
	public enum LMState {
		STRAIGHT_LEFT, STRAIGHT_RIGHT, LOST_RIGHT, LOST_LEFT, SEARCH_LEFT, SEARCH_RIGHT,
		SEARCH_LINE_END, SEARCH_LINE_END_TURNTOLOST, SEARCH_360_LINESCOUNT, STOP,
		FIND_LINE_START, FIND_LINE_SHORT_STRAIGHT, FIND_LINE_TURNLEFT, FIND_LINE_TURNRIGHT, FIND_LINE_STRAIGTRIGHT, FIND_LINE_STRAIGHTLEFT,
		END, DRIVE_OFFSET_FORWARD
	}
	
    private Robot robot;
    private LCDGui gui;
    
    
    private float param_robotMaxSpeed = 0.85f;
    private int param_colorFilterSize = 4;
    private int param_gyroFilterSize = 4;
    private float param_redThreshhold = 0.45f;
    //private float[] param_searchAngles = new float[] {6f, 10f, 16f, 24f, 32f, 64f, 80f, 100f, 120f, 140f};
    private float[] param_searchAngles = new float[] {8f, 18f, 36f, 115f, 145f};
    //private float[] param_searchAngles = new float[] {5f, 8f, 16f, 40f, 100f, 120f};
    private float param_angleWhenToTurnWithMaxSpeed = 50f;
    private float param_minTurnSpeed = 0.5f;
    private float param_maxTurnSpeed = 1f;
    private int param_timeWhenNextGyroValueIsTaken = 50;
    
    private float param_find_angle = 30f;
    private int param_find_moveStraigt_distance = 360 * 2;
    private int param_drive_down_of_barcode_distance = 450;
    private boolean param_debugWaits = false;
    
    private boolean end_of_line = false;
    
    public LineMovingIIt1(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(4, 1);
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
        //gui = new LCDGui(4, 1);
        
        curStat = LMState.DRIVE_OFFSET_FORWARD;
        
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
    private boolean search360onWhite = false;
    private int search360Count = 0;
    private float[] lastGyros = new float[10];
    private int lastGyrosIndex = 0;
    private int timeCounter = 0;
    private float gyroStartFindValue = 0f;
    
    @Override
    public void update(int elapsedTime) {

    	gui.setVarValue(0, String.valueOf(gyroSensor.sample()[0]), 5);
    	
    	timeCounter += elapsedTime;
    	
    	switch (curStat)
    	{
    	case DRIVE_OFFSET_FORWARD:
    		robot.move(-param_drive_down_of_barcode_distance);

    		curStat = LMState.FIND_LINE_START;
    		break;
    	case FIND_LINE_START:
    		if (robot.finished())
    		{
        		gyroStartFindValue = gyroSensor.sample()[0];
        		robot.turnOnSpot(param_find_angle);
            	gui.setVarValue(1, String.valueOf(gyroStartFindValue), 5);
        		
        		//gui.writeLine("Short straight");
        		//robot.stop();
    			//if (param_debugWaits) { while(Util.isPressed(Button.ID_DOWN) == false) {} }
    			
        		curStat = LMState.FIND_LINE_SHORT_STRAIGHT;
        		switchStateIfLineFound();
    		}
    		break;
    	case FIND_LINE_SHORT_STRAIGHT:
    		if (robot.finished())
    		{
        		robot.move(-param_find_moveStraigt_distance / 2);
        		
        		//gui.writeLine("Turn right");
        		//robot.stop();
    			//if (param_debugWaits) { while(Util.isPressed(Button.ID_DOWN) == false) {} }
    			
        		curStat = LMState.FIND_LINE_TURNRIGHT;
    		}
    		switchStateIfLineFound();
    		break;
    	case FIND_LINE_TURNRIGHT:
    		if (robot.finished())
    		{
    			float curGyro = gyroSensor.sample()[0];
    			float toTurn = gyroStartFindValue - curGyro - param_find_angle;
            	gui.setVarValue(2, String.valueOf(toTurn), 5);
    			
        		robot.turnOnSpot(toTurn);
        		
        		//gui.writeLine("Straight right");
        		//robot.stop();
    			//if (param_debugWaits) { while(Util.isPressed(Button.ID_DOWN) == false) {} }
    			
        		curStat = LMState.FIND_LINE_STRAIGTRIGHT;
    		}
    		switchStateIfLineFound();
    		break;
    	case FIND_LINE_STRAIGTRIGHT:
    		if (robot.finished())
    		{
    			robot.setSpeed(param_robotMaxSpeed);
        		robot.move(-param_find_moveStraigt_distance);
        		
        		//gui.writeLine("Turn left");
        		//robot.stop();
    			//if (param_debugWaits) { while(Util.isPressed(Button.ID_DOWN) == false) {} }
    			
        		curStat = LMState.FIND_LINE_TURNLEFT;
    		}
    		switchStateIfLineFound();
    		break;
    	case FIND_LINE_TURNLEFT:
    		if (robot.finished())
    		{
    			float curGyro = gyroSensor.sample()[0];
    			float toTurn = gyroStartFindValue - curGyro + param_find_angle;
    			
        		robot.turnOnSpot(toTurn);
        		
        		//gui.writeLine("Straight left");
        		//robot.stop();
    			//if (param_debugWaits) { while(Util.isPressed(Button.ID_DOWN) == false) {} }
    			
        		curStat = LMState.FIND_LINE_STRAIGHTLEFT;
    		}
    		switchStateIfLineFound();
    		break;
    	case FIND_LINE_STRAIGHTLEFT:
    		if (robot.finished())
    		{
    			robot.setSpeed(param_robotMaxSpeed);
        		robot.move(-param_find_moveStraigt_distance);
        		
        		//gui.writeLine("Turn right");
        		//robot.stop();
    			//if (param_debugWaits) { while(Util.isPressed(Button.ID_DOWN) == false) {} }
    			
        		curStat = LMState.FIND_LINE_TURNRIGHT;
    		}
    		switchStateIfLineFound();
    		break;
    	default:
    		if (curStat == LMState.STRAIGHT_LEFT || curStat == LMState.STRAIGHT_RIGHT)
        	{
        		float colorVal = RobotComponents.inst().getColorSensor().sample()[0];
        		
        		if (colorVal < param_redThreshhold)
        		{
        			//gui.writeLine("Lost line");
        			robot.stop();
        			
        			lostAngle = gyroSensor.sample()[0];
        			searchIteration = 0;
        			curStat = (curStat == LMState.STRAIGHT_LEFT ? LMState.SEARCH_LEFT : LMState.SEARCH_RIGHT);
        			
        			if (curStat == LMState.SEARCH_LEFT)
        			{
        				startTurnLeft = true;
            			//robot.turnOnSpot(param_searchAngles[searchIteration]);
        				turnRobotDegreesGyro(param_searchAngles[searchIteration]);
        			}
        			else 
        			{
        				startTurnLeft = false;
            			//robot.turnOnSpot(-param_searchAngles[searchIteration]);
            			turnRobotDegreesGyro(-param_searchAngles[searchIteration]);
        			}
        			
        			if (param_debugWaits)
        			{
            			//gui.writeLine("Wait for DOWN");
            			while(Util.isPressed(Button.ID_DOWN) == false) {}
        			}
        		}
        		else if (false)
        		{
        			//Fährt schön geradeaus
        			
        			if (timeCounter >= param_timeWhenNextGyroValueIsTaken)
        			{
        				lastGyros[lastGyrosIndex] = gyroSensor.sample()[0];
        				lastGyrosIndex++;
        				if (lastGyrosIndex >= lastGyros.length)
        				{
        					lastGyrosIndex = 0;
        				}
        				
        				float relTurn = 0f;
        				
        				float fac = 1f;
        				
        				for (int i = lastGyrosIndex + lastGyros.length; i > lastGyrosIndex; i--)
        				{
        					float first = lastGyros[(i) % lastGyros.length];
        					float second = lastGyros[(i - 1) % lastGyros.length];
        					relTurn += (first - second) * fac;

        					fac *= 0.5f;
        				}
        				
        				gui.setVarValue(0, relTurn);

    					//robot.setSpeed(param_robotMaxSpeed, param_robotMaxSpeed);

    	    			//robot.forward();
        			}
        		}
        	}
        	
        	if (curStat == LMState.SEARCH_LEFT || curStat == LMState.SEARCH_RIGHT)
        	{
        		float colorNow = colorSensor.sample()[0];
        		
        		if (colorNow > param_redThreshhold)
        		{
        			curStat = (curStat == LMState.SEARCH_RIGHT ? LMState.STRAIGHT_RIGHT : LMState.STRAIGHT_LEFT);
        			

        			//gui.writeLine("Found line");
        			if (param_debugWaits)
        			{
            			//gui.writeLine("Wait for DOWN");
            			while(Util.isPressed(Button.ID_DOWN) == false) {}
        			}

        			lastAngle = gyroSensor.sample()[0];
        			float estimatedDiff = lastAngle - preLastAngle;
        			estimatedDiff *= 0.002f;
        			estimatedDiff = estimatedDiff < -1f ? -1f : (estimatedDiff > 1f ? 1f : estimatedDiff);

        			preLastAngle = lastAngle;
        			
        	        robot.setSpeed(param_robotMaxSpeed, param_robotMaxSpeed);
        	        //robot.setSpeed(1f, 1f);

        	        //robot.steerFacSimonTest(estimatedDiff);//estimatedDiff);
        			robot.forward();
        		}
        		//else if (RobotComponents.inst().getLeftMotor().isMoving() == false && RobotComponents.inst().getRightMotor().isMoving() == false)
        		else if (robot.finished())
        		{
        			//robot.setSpeed(param_robotMaxSpeed);
        			curStat = (curStat == LMState.SEARCH_RIGHT ? LMState.SEARCH_LEFT : LMState.SEARCH_RIGHT);
        			
        			if (curStat == LMState.SEARCH_RIGHT && startTurnLeft == false
        				|| curStat == LMState.SEARCH_LEFT && startTurnLeft)
        			{
        				searchIteration++;
        			}
        			
        			if (searchIteration >= param_searchAngles.length)
        			{
        				curStat = LMState.SEARCH_LINE_END;
        			}
        			else
        			{
        				float curGyro = gyroSensor.sample()[0];
            			float turnDegree = param_searchAngles[searchIteration];
            			
            			

            			//gui.writeLine("Gonna turn");
            			if (param_debugWaits)
            			{
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
            			//turnRobotDegrees(turnDegree);
            			turnRobotDegreesGyro(turnDegree);
        			}
        		}
        	}
        	
        	
        	
        	if (curStat == LMState.SEARCH_LINE_END)
        	{
        		//gui.writeLine("TurnToLost");
    			if (param_debugWaits)
    			{
        			while(Util.isPressed(Button.ID_DOWN) == false) {}
    			}
    			
    			curStat = LMState.SEARCH_LINE_END_TURNTOLOST;
    			
    			float currAngle = gyroSensor.sample()[0];
    			
        		turnRobotDegreesGyro(lostAngle - currAngle);
        	}
        	
        	if (curStat == LMState.SEARCH_LINE_END_TURNTOLOST)
        	{
        		if (robot.finished())
        		{
        			robot.forward();
        			curStat = LMState.END;
        			end_of_line = true;
        			/*curStat = LMState.SEARCH_360_LINESCOUNT;
        			search360Count = 0;
        			search360onWhite = false;
            		//System.out.println("Start turning for 360");
            		turnRobotDegreesGyro(lostAngle + 360);*/
        		}
        	}
        	
        	if (curStat == LMState.SEARCH_360_LINESCOUNT)
        	{
        		if (robot.finished() == false)
        		{
            		float colorNow = colorSensor.sample()[0];
            		
            		//System.out.println("CurCol=" + String.valueOf(colorNow));
            		
            		if (colorNow > param_redThreshhold && search360onWhite == false)
            		{
            			search360onWhite = true;
            			search360Count++;
            		}
            		else
            		{
            			search360onWhite = false;
            		}
        		}
        		else
        		{
        			robot.stop();
        			//gui.writeLine("Lines: " + search360Count);
        			
        			curStat = LMState.STOP;
        		}
        	}
    		break;
    	}
    	
    	
    	
    	
    	
    	
    	
		//gui.setVarValue(0,  RobotComponents.inst().getColorSensor().sample()[0], 5);
		//gui.setVarValue(1,  RobotComponents.inst().getGyroSensor().sample()[0], 5);
    	
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
    
    private void turnRobotDegreesGyro(float degrees)
    {
    	//robot.setSpeed(1f);
    	robot.turnOnSpot(degrees, 20f);
    	//robot.turnOnSpotFastBy(degrees);
    }
    
    private void switchStateIfLineFound()
    {
		float colorNow = colorSensor.sample()[0];
		
		if (colorNow > param_redThreshhold)
		{
			curStat = LMState.SEARCH_LEFT;
		}
    }
    
}
