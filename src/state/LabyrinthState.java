package state;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import robot.Robot;
import robot.RobotComponents;
import util.Util;
import util.lcdGui.LCDGui;

public class LabyrinthState implements ParcourState 
{
    private static final float DISTANCE_SHOULD = 0.15f;
    private static final float TOO_NEAR = 0.04f;
    private static final float TOO_FAR = 0.2f;
    private static final float CRITICAL_JUMP = 0.03f;
    private static final float TURN_FACTOR = 15.f;
    private static final float STANDARD_SPEED = 1.f;
    private static final int RETREAT_DEGREE = 50;
    private static final int TIME_STRAIGHT = 200;

    private Robot robot;
    private LCDGui gui;
    
    private float turn = 0.f;
    private float last_samp = DISTANCE_SHOULD;
    private float turnAngle = 0.f;
    private int elapsedInterval = 0;
    
    public class DeadlockDetector
    {
        private List<Float> samples;
        private float elapsed;
        private float interval;
        private float minDiff;

        public DeadlockDetector(float interval, float minDiff)
        {
            this.interval = interval;
            this.minDiff = minDiff;
            samples = new LinkedList<Float>();
        }
        
        public void init()
        {
            elapsed = 0.f;
            samples.clear();
            samples.add(RobotComponents.inst().getGyroSensor().sample()[0]);
        }
        
        public boolean stuck(float elapsedTime)
        {
            elapsed += elapsedTime;
            if (elapsed > interval)
            {
                elapsed  = 0.f;
                
                float sample = RobotComponents.inst().getGyroSensor().sample()[0];
                samples.add(sample);
                if (samples.size() > 8)
                    samples.remove(samples.size() - 1);
                if (sample - samples.get(samples.size() - 2) < minDiff)
                {
                    LCDGui.clearLCD();
                    for (int i = 0; i < samples.size(); i++)
                    {
                        LCD.drawString(String.valueOf(samples.get(i)), 2, i);
                    }
                    robot.stop();
//                  while (!Util.isPressed(Button.ID_LEFT))
//                  {
//                      try {
//                          Thread.sleep(100);
//                      } catch (InterruptedException e) {
//                          // TODO Auto-generated catch block
//                          e.printStackTrace();
//                      }
//                  }
                  LCDGui.clearLCD();
                    
                    return true;
                }
            }
            
            return false;
        }
    }
    
    // Ignore this for now
    public abstract class Action 
    {
        private String name;
        private Action next;
        
        public Action(String name, Action next)
        {
            this.name = name;
            this.next = next;
        }
        public String toString()
        {
            return name;
        }
        
        public void enter() {}
        public void update() {}
        public Action exit() { return next; }
    }
    
    public enum State {
        RETREAT,
        ROTATE,
        FOLLOW,
        RETREAT2,
        SHARP_TURN,
        STRAIGHT,
        DEBUG
    }
    private State state;
    
    private DeadlockDetector bernd;
    
    public LabyrinthState(Robot robo) 
    {
        robot = robo;
        bernd = new DeadlockDetector(2000, 5);
    }
    
	@Override
	public boolean changeOnBarcode()
	{
		return true;
	}
    
    @Override
    public boolean changeImmediately()
    {
        return false;
    }
    
    public String getName() 
    {
        return "Labyrinth";
    }

    @Override
    public void init() 
    {
        robot.setSpeed(STANDARD_SPEED);
        
        if (!robot.USisUp())
            robot.raiseUS();
        robot.forward();
        gui = new LCDGui(2, 1);
        state = State.FOLLOW;
    }
    
    public void stateChangeDebug() 
    {
//        LCD.drawString(state.toString(), 0, 2);
//        while (!Util.isPressed(Button.ID_LEFT))
//        {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
    }
    
    public void reset() 
    {
        robot.stop();
    }

    @Override
    public void update(int elapsedTime) 
    {
        switch (state) 
        {
        case FOLLOW:
            if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) 
            {
                robot.stop();
                state = State.RETREAT;
                stateChangeDebug();
                robot.setSpeed(STANDARD_SPEED);
                robot.move(RETREAT_DEGREE);
            }
            else
            {
                float samp = RobotComponents.inst().getUS().sample()[0];
                
                if (last_samp < TOO_NEAR)
                {
                    if (samp > last_samp + CRITICAL_JUMP)
                    {
                        // got too near to wall, back off and try again
                        state = State.RETREAT2;
                        robot.stop();
                        stateChangeDebug();
                        robot.setSpeed(STANDARD_SPEED);
                        robot.move(RETREAT_DEGREE);
                        return;
                    }
                }
                
                if (samp > TOO_FAR)
                {
                    robot.stop();
                    state = State.SHARP_TURN;
                    stateChangeDebug();
                    robot.setSpeed(STANDARD_SPEED);
                    robot.forward();
                    turnAngle = RobotComponents.inst().getGyroSensor().sample()[0];
                    bernd.init();
                    return;
                }
                
                last_samp = samp;
                turn = (samp - DISTANCE_SHOULD) * TURN_FACTOR;
                robot.steer(Math.max(-0.8f, Math.min(.8f, turn)));
                robot.forward();
                
                gui.setVarValue(0, samp);
                gui.setVarValue(1, turn);
            }
            break;
        case RETREAT:
            if (robot.finished())
            {
                state = State.ROTATE;
                stateChangeDebug();
                robot.turnOnSpot(-90);
                robot.setSpeed(STANDARD_SPEED);
            }
            break;
        case RETREAT2:
            if (robot.finished())
            {
                last_samp = DISTANCE_SHOULD;
                state = State.ROTATE;
                stateChangeDebug();
                robot.turnOnSpot(-20);
                robot.setSpeed(STANDARD_SPEED);
            }
            break;
        case ROTATE:
            if (robot.finished())
            {
                state = State.FOLLOW;
                stateChangeDebug();
                robot.setSpeed(STANDARD_SPEED);
                robot.forward();
            }
            break;
        case SHARP_TURN:
            if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) 
            {
                robot.stop();
                state = State.RETREAT;
                stateChangeDebug();
                robot.setSpeed(STANDARD_SPEED);
                robot.move(360);
                return;
            }
            if (RobotComponents.inst().getGyroSensor().sample()[0] < turnAngle + 90)
            {
                float sample = RobotComponents.inst().getUS().sample()[0];
                
                if (sample < TOO_FAR)
                {
                    robot.stop();
                    state = State.FOLLOW;
                    stateChangeDebug();
                    robot.setSpeed(STANDARD_SPEED);
                    robot.forward();
                    return;
                }
                
                robot.steer(0.7f);
                robot.forward();
                
                if (bernd.stuck(elapsedTime))
                {
                    robot.stop();
                    state = State.RETREAT2;
                    stateChangeDebug();
                    robot.setSpeed(STANDARD_SPEED);
                    robot.move(360);
                    return;
                }
            }
            else
            {
                robot.stop();
                robot.setSpeed(STANDARD_SPEED);
                elapsedInterval = 0;
                state = State.STRAIGHT;
                stateChangeDebug();
            }
            break;
        case STRAIGHT:
            if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) 
            {
                robot.stop();
                state = State.RETREAT;
                stateChangeDebug();
                robot.setSpeed(STANDARD_SPEED);
                robot.move(RETREAT_DEGREE);
                return;
            }
            elapsedInterval += elapsedTime;
            if (elapsedInterval < TIME_STRAIGHT)
            {
                robot.forward();
            }
            else
            {
                state = State.FOLLOW;
                robot.stop();
                stateChangeDebug();
                robot.forward();
            }
            break;
        default:
        }
    }
}
