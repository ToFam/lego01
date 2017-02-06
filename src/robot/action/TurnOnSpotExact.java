package robot.action;

import robot.RobotComponents;

public class TurnOnSpotExact implements RobotAction {
    
    private float deg;
    
    private float speedLeft = 0.f;
    private float speedRight = 0.f;
    
    private float gyroValue;
    private float goalValue;
    private boolean goalGreater;
    private boolean finished;
    
    private static final float turnOnSpot_slowSpeedFactor = 0.3f;
    private static final float turnOnSpot_stopBeforeAngle = 1.5f;
    
    public TurnOnSpotExact(float destinyDegree, float speedLeft, float speedRight)
    {
        deg = destinyDegree;
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
        RobotComponents.inst().getLeftMotor().setSpeed(speedLeft * turnOnSpot_slowSpeedFactor);
        RobotComponents.inst().getRightMotor().setSpeed(speedRight * turnOnSpot_slowSpeedFactor);
        gyroValue = RobotComponents.inst().getGyroSensor().sample()[0];
        goalValue = /*gyroValue + */deg;
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
        
        //slowMode = false;
        finished = false;
    }
    
    public void update() 
    {
        // DEBUG
        // RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
        //LCDGui.clearLCD();
        
        if (finished)
            return;
        
        if ((goalGreater && gyroValue < goalValue) || (!goalGreater && gyroValue > goalValue))
        {
            // DEBUG
            //LCD.drawString(String.valueOf(gyroValue), 4, 2);
            //LCD.drawString(String.valueOf(goalValue), 4, 4);
            
            /*if (slowMode == false && Math.abs(gyroValue - goalValue) < turnOnSpot_angleToSlowDown)
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
            }*/
            
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
