package robot.action;

import lejos.hardware.lcd.LCD;
import robot.RobotComponents;
import util.lcdGui.LCDGui;

public class TurnOnSpot implements RobotAction {
    
    private float deg;
    
    private float speedLeft = 0.f;
    private float speedRight = 0.f;
    
    private float gyroValue;
    private float goalValue;
    private boolean goalGreater;
    private boolean slowMode;
    private boolean finished;
    
    private static final float turnOnSpot_angleToSlowDown = 50f;
    private static final float turnOnSpot_slowDownSpeedFactor = 0.4f;
    private static final float turnOnSpot_stopBeforeAngle = 4f;
    
    public TurnOnSpot(float degree, float speedLeft, float speedRight)
    {
        deg = degree;
        this.speedLeft = speedLeft;
        this.speedRight = speedRight;
    }
    
    private void startTurnOnSpot(boolean left) 
    {
        if (!left) 
        {
            RobotComponents.inst().getLeftMotor().backward();
            RobotComponents.inst().getRightMotor().forward();
        }
        else
        {
            RobotComponents.inst().getLeftMotor().forward();
            RobotComponents.inst().getRightMotor().backward();
        }
    }
    
    public void start() {
        RobotComponents.inst().getLeftMotor().setSpeed(speedLeft);
        RobotComponents.inst().getRightMotor().setSpeed(speedRight);
        gyroValue = RobotComponents.inst().getGyroSensor().sample()[0];
        goalValue = gyroValue + deg;
        goalGreater = gyroValue < goalValue;
        
        float addition = goalGreater ? -turnOnSpot_stopBeforeAngle : turnOnSpot_stopBeforeAngle;
        goalValue += addition;
        
        if (goalGreater)
        {
            startTurnOnSpot(true);
        }
        else
        {
            startTurnOnSpot(false);
        }
        
        slowMode = false;
        finished = false;
    }
    
    public void update() 
    {
        // DEBUG
        // RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
        LCDGui.clearLCD();
        
        if (finished)
            return;
        
        if ((goalGreater && gyroValue < goalValue) || (!goalGreater && gyroValue > goalValue))
        {
            // DEBUG
            //LCD.drawString(String.valueOf(gyroValue), 4, 2);
            //LCD.drawString(String.valueOf(goalValue), 4, 4);
            
            if (slowMode == false && Math.abs(gyroValue - goalValue) < turnOnSpot_angleToSlowDown)
            {
                slowMode = true;
                RobotComponents.inst().getLeftMotor().stop(true);
                RobotComponents.inst().getRightMotor().stop(true);
                RobotComponents.inst().getLeftMotor().setSpeed(speedLeft * turnOnSpot_slowDownSpeedFactor);
                RobotComponents.inst().getRightMotor().setSpeed(speedRight * turnOnSpot_slowDownSpeedFactor);

                if (goalGreater)
                {
                    startTurnOnSpot(true);
                }
                else
                {
                    startTurnOnSpot(false);
                }
            }
            
            gyroValue = RobotComponents.inst().getGyroSensor().sample()[0];
        }
        else
        {
            finished = true;
            RobotComponents.inst().getLeftMotor().stop(true);
            RobotComponents.inst().getRightMotor().stop(true);

            RobotComponents.inst().getLeftMotor().setSpeed(speedLeft);
            RobotComponents.inst().getRightMotor().setSpeed(speedRight);
        }
    }

    public boolean finished() {
        return finished;
    }
}
