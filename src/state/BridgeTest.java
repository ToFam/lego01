package state;

import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.GyroSensor;
import sensor.TouchSensorBThread;
import sensor.USSensor;
import sensor.modes.USSensorMode;
import util.Util;
import util.lcdGui.LCDGui;

public class BridgeTest implements ParcourState {
	
	
	public enum BridgeState {
		START, DRIVING_UP, STRAIGHT, TURNING_ON_INFINITY,
		WAIT_FOR_UV_INIT,
		WAIT_SHORT,

		UV_ISUP,
		UV_ISDOWN,
		
		GYRO_ALARM_TURNING,
		
		END
	}

	
    private Robot robot;
    private LCDGui gui;
    
    
    private float param_robotMaxSpeed = 1f;
    private int param_timeNoBarcode = 3000;
    private int param_timeStraight = 1000;
    private float param_thresholdAbyss = 0.075f;
    private float param_gyroAlarm = 17f;
    private float param_maxAngleOffsetLeft = 30f;
    private boolean param_debugWaits = false;

    private boolean end_of_line = false;
    
    public BridgeTest(Robot robot) {
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
    	state = BridgeState.START;
		robot.lowerUS();
    	
        robot.setSpeed(param_robotMaxSpeed, param_robotMaxSpeed);

        
        gyroSensor = RobotComponents.inst().getGyroSensor();
        uvSensor = RobotComponents.inst().getUS();
        touchSensor = RobotComponents.inst().getTouchSensorB();

    }

    
    public String getName() {
        return "Bridge Test";
    }
    
    public void reset() {
		this.robot.raiseUS();
    }
    
    private GyroSensor gyroSensor;
    private USSensor uvSensor;
    private TouchSensorBThread touchSensor;
    
    private BridgeState state;
    
    private int timeStraightCounter = 0;
    
    private int timeCount = 0;
    
    float firstGyro = 0f;
    boolean foundBorderOnce = false;
    
    float[] rampGyros = new float[10000];
    int rampGyrosCount = 0;
    float rampAngle = Float.MAX_VALUE;
    boolean onceNotInfinity = false;
    
    float turn = 0;
    int noBarcodeCounter = 0;
    
    int timeGyroCount = 0;
    
    int timeNoAlarm = 0;
    boolean onceAlarm = false;
    boolean alarmTurned = false;
    
    
    @Override
    public void update(int elapsedTime)
    {
    	if (uvSensor.instSample()[0] == Float.POSITIVE_INFINITY)
    	{
    		robot.stop();
			uvSensor.setMode(USSensorMode.DISTANCE.getIdf());
    	}
    	
    	if (noBarcodeCounter * elapsedTime >= param_timeNoBarcode)
    	{
    		end_of_line = true;
    	}
    	else
    	{
    		noBarcodeCounter++;
    	}
    	
    	gui.setVarValue(0, gyroSensor.instSample()[0]);
    	
    	switch (state)
    	{
    	case START:
    		
    		firstGyro = gyroSensor.sample()[0];
    		timeCount = 0;
    		state = BridgeState.WAIT_SHORT;
    		break;
    	case UV_ISDOWN:
    		
    		break;
    	case UV_ISUP:
    		
    		break;
    	case WAIT_SHORT:
    		robot.stop();
    		if (timeCount == 0)
    		{
    			uvSensor.setMode(USSensorMode.DISTANCE.getIdf());
    		}
    		
    		timeCount++;
    		if (timeCount * elapsedTime >= 2000)
    		{
        		state = BridgeState.WAIT_FOR_UV_INIT;
    		}
    		
    		break;
    	case WAIT_FOR_UV_INIT:
    		float uvVal = uvSensor.instSample()[0];
    		
    		if (uvVal == Float.POSITIVE_INFINITY)
    		{
    			robot.stop();
    		}
    		else
    		{
        		robot.forward();
    			state = BridgeState.STRAIGHT;
    		}
    		
    		break;
    	case STRAIGHT:
    		if (timeStraightCounter * elapsedTime >= param_timeStraight)
    		{
    			state = BridgeState.DRIVING_UP;
    		}
    		else
    		{
    			timeStraightCounter++;
    		}
    		break;
    	case DRIVING_UP:
    		timeGyroCount++;
    		
    		float sample = uvSensor.instSample()[0];
    		
    		boolean onRamp = uvSensor.instSample()[0] > param_thresholdAbyss ? false : true;
    		
    		float samp = onRamp ? 0.07f : -0.6f;
    		
    		if (sample == Float.POSITIVE_INFINITY)
    		{
    			samp = 0.f;
    		}
    		else if (rampAngle == Float.MAX_VALUE)
    		{
    			if (rampGyrosCount < rampGyros.length)
    			{
            		rampGyros[rampGyrosCount] = gyroSensor.sample()[0];
            		rampGyrosCount++;
    			}
        		/*if (rampGyrosCount >= rampGyros.length)
        		{
        			rampGyrosCount = 0;
        		}*/
        		
        		if (timeGyroCount * elapsedTime >= 3000)
        		{
        			rampAngle = Util.average(rampGyros, rampGyrosCount);
        		}
    		}
    		
    		/*if (sample == Float.POSITIVE_INFINITY && foundBorderOnce)
    		{
    			float avg = 0f;
    			for (int i = 0; i < 10; i++)
    			{
    				avg += rampGyros[(rampGyrosCount - i + 2 * rampGyros.length) % rampGyros.length];
    			}
    			
    			avg /= 10f;
    			
    			while (Util.isPressed(Button.ID_DOWN) == false) {}
    			
    			robot.stop();
    			
    			robot.turnOnSpotExact(avg);
    			
    			state = BridgeState.TURNING_ON_INFINITY;
    		}*/
    		
    		float curGyro = gyroSensor.sample()[0];
    		/*if (foundBorderOnce == false && curGyro > firstGyro + param_maxAngleOffsetLeft)
    		{
    			samp = 0f;
    		}*/
    		
    		if (rampAngle != Float.MAX_VALUE && curGyro < rampAngle - param_gyroAlarm)
    		{
    			onceAlarm = true;
    			timeNoAlarm = 0;
    			robot.turnOnSpot(param_gyroAlarm);
    			state = BridgeState.GYRO_ALARM_TURNING;
    		}
    		else
    		{
    			if (onceAlarm && alarmTurned == false)
    			{
        			timeNoAlarm++;
    			}
        		if (! onRamp)
        		{
        			foundBorderOnce = true;
        		}
                
                turn = (samp) * 30f;
                robot.steer(Math.max(-0.8f, Math.min(0.53f, turn)));
                robot.forward();
    		}
    		
    		
    		if (alarmTurned == false && timeNoAlarm * elapsedTime >= 5000)
    		{
    			alarmTurned = true;
    			rampAngle += 38f;
    			gui.writeLine("Turned: " + rampAngle);
    		}
    		
    		
    		
    		
    		
    		
    		break;
    	case GYRO_ALARM_TURNING:
    		if (robot.finished())
    		{
    			state = BridgeState.DRIVING_UP;
    		}
    		break;
    	case TURNING_ON_INFINITY:
    		if (robot.finished())
    		{
    			state = BridgeState.DRIVING_UP;
    		}
    		break;
    	case END:
    		
    		break;
    	}
    	
    	
    	
		
        
    }
}
