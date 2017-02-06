package state;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.internal.ev3.EV3Audio;
import robot.Robot;
import robot.RobotComponents;
import sensor.ColorSensor;
import sensor.GyroSensor;
import sensor.TouchSensorBThread;
import sensor.UVSensor;
import sensor.modes.ColorSensorMode;
import sensor.modes.GyroSensorMode;
import sensor.modes.UVSensorMode;
import util.MediumMotorTuple;
import util.Util;
import util.lcdGui.LCDGui;

public class SuspBridgeState implements ParcourState {

    private Robot robot;
    private LCDGui gui;
    
    
    private float param_robotMaxSpeed = 1f;
    private float param_robotSpeedOnRampUp = 0.6f;
    private float param_robotSpeedOnBridge = 1f;
    private float param_robotRetreatSpeed = 0.6f;
    private float param_goalDistance = 0.087f;
    private float param_goalDistanceRampUp = 0.058f;
    private float param_uvReachedTopOfRamp = 0.09f;
    private float param_kpStraight = 32f * 1.3f;
    private float param_kpRamp = 32f * 1.3f;
    private int param_gyroFilterSize = 4;
    private int param_uvFilterSize = 4;
    private int param_timeGyroMedianOnStraight = 5000;
    private int param_timeGyroMedianOnRamp = 3000;
    private int param_minTimeOnBridge = 5000;
    private int param_minTimeOnRampDown = 3000;
    private float param_angleStraightToSuspBridge = 20f;
    private float param_uvCatchRobotAtEndWhenUnder = 0.2f;
    private float param_percentCutBegin = 0.32f;
    private float param_percentCutEnd = 0.2f;
    private boolean param_debugWaits = false;

    private boolean end_of_line = true;
    
    public SuspBridgeState(Robot robot) {
        this.robot = robot;
        this.gui = new LCDGui(2, 1);
    }
    
	@Override
	public boolean changeOnBarcode()
	{
		return end_of_line;
	}
    
    @Override
    public void init() {
    	state = S_SuspBridgeState.BEFORE_DOTS;
    	
        robot.setSpeed(param_robotMaxSpeed, param_robotMaxSpeed);

        RobotComponents.inst().getTouchSensorB().setMode(0);
        
        RobotComponents.inst().getUV().setMode(UVSensorMode.DISTANCE.getIdf());
        RobotComponents.inst().getUV().setMedianFilter(param_uvFilterSize);
        
        RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
        RobotComponents.inst().getGyroSensor().setMedianFilter(param_gyroFilterSize);
        
        gyroSensor = RobotComponents.inst().getGyroSensor();
        uvSensor = RobotComponents.inst().getUV();
        touchSensor = RobotComponents.inst().getTouchSensorB();

		robot.forward();
    }

    
    public String getName() {
        return "Suspension bridge";
    }
    
    public void reset() {
        RobotComponents.inst().getMediumMotor().rotateTo(0, true);
    }
    
    private GyroSensor gyroSensor;
    private UVSensor uvSensor;
    private TouchSensorBThread touchSensor;
    
    private S_SuspBridgeState state;
    
    MediumMotorTuple[] measures = new MediumMotorTuple[10000];
    MediumMotorTuple[] measures2 = new MediumMotorTuple[10000];
    
    float[] straightGyros = new float[10000];
    int straightGyrosCount = 0;
    float[] rampGyros = new float[10000];
    int rampGyrosCount = 0;
    float straightAngle = Float.MAX_VALUE;
    float rampAngle = Float.MAX_VALUE;
    float uvMax = -1f;
    int c = 0;
    private float turn = 0;
    private int timeInfinityCounter = 0;
    
    @Override
    public void update(int elapsedTime) {

    	switch (state)
    	{
    	case BEFORE_DOTS:
    		if (touchSensor.sample()[0] == 1)
    		{
                robot.stop();
                robot.setSpeed(param_robotRetreatSpeed);
                robot.move(230);
                state = S_SuspBridgeState.RETREAT;
    		}
    		break;
    	case RETREAT:
    		if (robot.finished())
    		{
                robot.turnOnSpot(-90);
                state = S_SuspBridgeState.TURNING;
    		}
    		break;
    	case TURNING:
    		if (robot.finished())
    		{
    			robot.stop();
    			robot.forward();
    			state = S_SuspBridgeState.HOLD_DISTANCE;
    		}
    		break;
    	case HOLD_DISTANCE:
    		robot.setSpeed(param_robotMaxSpeed);
    		
    		if (straightGyrosCount * elapsedTime < param_timeGyroMedianOnStraight)
    		{
    			straightGyros[straightGyrosCount] = gyroSensor.sample()[0];
    			straightGyrosCount++;
    		}
    		else if (straightAngle == Float.MAX_VALUE)
    		{
    			straightAngle = Util.average(straightGyros, straightGyrosCount);
    			
    			gui.writeLine("Saved straight");
    			gui.writeLine("Val is: " + String.valueOf(straightAngle));
    			
    		}
    		else if (Math.abs((gyroSensor.sample()[0] - straightAngle)) >= param_angleStraightToSuspBridge)
    		{
    			state = S_SuspBridgeState.ON_SUSP_RAMP_UP;
    			
    			gui.writeLine("STATE OnSuspRamp");
    			
    		}
    			
    		
    		
    		
    		/*measures[c] = new MediumMotorTuple(gyroSensor.sample()[0], c);
    		
    		if (uvSensor.sample()[0] > uvMax)
    		{
    			uvMax = uvSensor.sample()[0];
    		}
    		
    		measures2[c] = new MediumMotorTuple(uvSensor.sample()[0], c);
    		c++;
    		if (c >= measures.length)
    			c = measures.length - 1;*/
    		float samp = uvSensor.sample()[0];
            
            turn = (samp - param_goalDistance) * param_kpStraight;
            robot.steer(Math.max(-0.8f, Math.min(0.8f, turn)));
            robot.forward();
            
            //gui.setVarValue(0, samp);
            //gui.setVarValue(0, turn);
    		break;
    	case ON_SUSP_RAMP_UP:
    		robot.setSpeed(param_robotSpeedOnRampUp);
    		float samp2 = uvSensor.sample()[0];
            
            turn = (samp2 - param_goalDistanceRampUp) * param_kpRamp;
            robot.steer(Math.max(-0.8f, Math.min(0.8f, turn)));
            robot.forward();
            
            
            
    		if (rampGyrosCount * elapsedTime < param_timeGyroMedianOnRamp || uvSensor.sample()[0] < param_uvReachedTopOfRamp)
    		{
    			rampGyros[rampGyrosCount] = gyroSensor.sample()[0];
    			rampGyrosCount++;
    		}
    		else if (rampAngle == Float.MAX_VALUE)
    		{
    			float[] cutted2 = new float[rampGyrosCount];
    			int cuttedCounter = 0;
    			for (int i = 0; i < rampGyrosCount; i++)
    			{
    				float curPos = ((float)(i)) / ((float)(rampGyrosCount));
    				if (curPos >= param_percentCutBegin && curPos <= 1f - param_percentCutEnd)
    				{
    					cutted2[cuttedCounter] = rampGyros[i];
    					cuttedCounter++;
    				}
    			}
    			float[] cutted = new float[cuttedCounter];
    			for (int i = 0; i < cuttedCounter; i++)
    			{
    				cutted[i] = cutted2[i];
    			}
    			
    			rampAngle = Util.average(cutted);

    			gui.writeLine("Saved ramp");
    			gui.writeLine("Val is: " + String.valueOf(rampAngle));
    			//gui.writeLine("GyrCount: " + String.valueOf(rampGyrosCount));
    			//gui.writeLine("CutCount: " + String.valueOf(cuttedCounter));
    			if (param_debugWaits)
    			{
    				robot.stop();
        			while(Util.isPressed(Button.ID_DOWN) == false) {}
        			robot.forward();
    			}
    			
    			robot.stop();
    			float curAngle = gyroSensor.sample()[0];

    			gui.writeLine("Gonna turn");
    			if (param_debugWaits) { while(Util.isPressed(Button.ID_DOWN) == false) {} }
    			
    			//robot.turnOnSpot(rampAngle - curAngle);
    			robot.turnOnSpotExact(rampAngle);
    			
    			state = S_SuspBridgeState.WAIT_FOR_ADJUSTANCE;
    		}
    		
    		
    		break;
    	case WAIT_FOR_ADJUSTANCE:
    		if (robot.finished())
    		{
    			gui.writeLine("Adjusted");
    			if (param_debugWaits) { while(Util.isPressed(Button.ID_DOWN) == false) {} }
    			

    			gui.writeLine("Adjusted");
    			
    			robot.setSpeed(param_robotSpeedOnBridge);
    			robot.forward();
    			state = S_SuspBridgeState.DRIVE_TILL_INFINITY;
    		}
    		break;
    	case DRIVE_TILL_INFINITY:
    		timeInfinityCounter++;
    		if (uvSensor.sample()[0] > param_uvCatchRobotAtEndWhenUnder + 0.05 && timeInfinityCounter * elapsedTime >= param_minTimeOnBridge)
    		{
    			state = S_SuspBridgeState.FULLSPEED;
    		}
    		break;
    	case FULLSPEED:
    		if (uvSensor.sample()[0] < param_uvCatchRobotAtEndWhenUnder)
    		{
    			gui.writeLine("Catched");
    			
    			timeInfinityCounter = 0;
    			robot.stop();
    			state = S_SuspBridgeState.CATCH_AGAIN;
    		}
    		break;
    	case CATCH_AGAIN:
    		robot.setSpeed(param_robotSpeedOnBridge);
    		
    		float samp3 = uvSensor.sample()[0];
            
            turn = (samp3 - param_goalDistance) * param_kpRamp * 0.85f;
            robot.steer(Math.max(-0.8f, Math.min(0.8f, turn)));
            robot.forward();
            
            if (uvSensor.sample()[0] > param_uvCatchRobotAtEndWhenUnder + 0.1f && timeInfinityCounter * elapsedTime >= param_minTimeOnRampDown)
            {
            	state = S_SuspBridgeState.PRE_END;
            	robot.stop();
            }
    		break;
    	case PRE_END:
    		Sound.setVolume(100);
    		Sound.beepSequenceUp();
    		
    		state = S_SuspBridgeState.END;
    		break;
    	case END:
    		
    		break;
    	}
    	
    	
    	
		gui.setVarValue(0,  uvSensor.sample()[0], 5);
		gui.setVarValue(1,  gyroSensor.sample()[0], 5);
		//gui.setVarValue(1,  RobotComponents.inst().getGyroSensor().sample()[0], 5);
    	
        if (Util.isPressed(Button.ID_UP))
        {
        	robot.stop();
    		for (int i = 0; i < c; i++)
            {
            	System.out.println(String.valueOf(measures[i].getF2()) + "=" + String.valueOf(measures[i].getF1()));
        		
            }
        	System.out.println("--------------Measures end-----------------------");
    		for (int i = 0; i < c; i++)
            {
            	System.out.println(String.valueOf(measures2[i].getF2()) + "=" + String.valueOf(measures2[i].getF1()));
        		
            }
        	System.out.println("--------------Measures2 end----------------------");
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
