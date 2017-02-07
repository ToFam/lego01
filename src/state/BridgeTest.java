package state;

import lejos.hardware.Button;
import robot.Robot;
import robot.RobotComponents;
import sensor.GyroSensor;
import sensor.TouchSensorBThread;
import sensor.USSensor;
import util.Util;
import util.lcdGui.LCDGui;

public class BridgeTest implements ParcourState {
	
	
	public enum BridgeState {
		START, DRIVING_UP, STRAIGHT, TURNING_ON_INFINITY,
		WAIT_FOR_UV_INIT,
		WAIT_SHORT,

		UV_ISUP,
		UV_ISDOWN,
		
		END
	}

	
    private Robot robot;
    private LCDGui gui;
    
    
    private float param_robotMaxSpeed = 1f;
    private int param_timeNoBarcode = 3000;
    private int param_timeStraight = 2000;
    private float param_thresholdAbyss = 0.05f;
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
    
    
    @Override
    public void update(int elapsedTime)
    {
    	if (noBarcodeCounter * elapsedTime >= param_timeNoBarcode)
    	{
    		end_of_line = true;
    	}
    	else
    	{
    		noBarcodeCounter++;
    	}
    	
    	gui.setVarValue(0, uvSensor.sample()[0]);
    	
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
    		timeCount++;
    		if (timeCount * elapsedTime >= 2000)
    		{
        		state = BridgeState.WAIT_FOR_UV_INIT;
    		}
    		
    		break;
    	case WAIT_FOR_UV_INIT:
    		float uvVal = uvSensor.sample()[0];
    		
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
    		
    		
    		float sample = uvSensor.sample()[0];
    		
    		boolean onRamp = uvSensor.sample()[0] > param_thresholdAbyss ? false : true;
    		
    		float samp = onRamp ? 0.2f : -0.6f;
    		
    		if (sample == Float.POSITIVE_INFINITY)
    		{
    			samp = 0.f;
    		}
    		else
    		{
        		rampGyros[rampGyrosCount] = gyroSensor.sample()[0];
        		rampGyrosCount++;
        		if (rampGyrosCount >= rampGyros.length)
        		{
        			rampGyrosCount = 0;
        		}
    		}
    		
    		if (sample == Float.POSITIVE_INFINITY && foundBorderOnce)
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
    		}
    		
    		float curGyro = gyroSensor.sample()[0];
    		/*if (foundBorderOnce == false && curGyro > firstGyro + param_maxAngleOffsetLeft)
    		{
    			samp = 0f;
    		}*/
    		
    		if (! onRamp)
    		{
    			foundBorderOnce = true;
    		}
            
            turn = (samp) * 30f;
            robot.steer(Math.max(-0.8f, Math.min(0.8f, turn)));
            robot.forward();
    		
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
