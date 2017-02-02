package state;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import lcdGui.LCDGui;
import lejos.hardware.ev3.LocalEV3;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.GyroSensorMode;
import sensor.modes.UVSensorMode;

public class LabyrinthState implements ParcourState 
{
    private static final int TIME_UNTIL_FREE = 1000;
    private static final float DISTANCE_SHOULD = 0.1f;
    private static final float TURN_FACTOR = 10.f;

    private int free = 0;
    private Robot robot;
    private LCDGui gui;
    
    private PrintWriter writer;
    
    private enum Path
    {
        STRAIGHT,
        LEFT,
        RIGHT
    }
    
    private Path choice = Path.STRAIGHT;
    
    public LabyrinthState(Robot robo) 
    {
        robot = robo;
    }
    
    public String getName() 
    {
        return "Labyrinth";
    }

    @Override
    public void init() 
    {
        RobotComponents.inst().getGyroSensor().setMode(GyroSensorMode.ANGLE.getIdf());
        RobotComponents.inst().getTouchSensorB();
        RobotComponents.inst().getUV().setMode(UVSensorMode.DISTANCE.getIdf());
        RobotComponents.inst().getUV().setMedianFilter(100);
        robot.setSpeed(0.5f);
        
        robot.rotateMiddle(-(Robot.SENSOR_ANGLE / 2 - 5));
        robot.forward();
        gui = new LCDGui(2, 1);

        try {
            writer = new PrintWriter("log.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        File chacarron = new File("Chaccaron_Intro.wav");
        int rtn = LocalEV3.get().getAudio().playSample(chacarron, 100);
        gui.setVarValue(0, rtn);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void reset() 
    {
        robot.stop();
        robot.rotateMiddle((Robot.SENSOR_ANGLE / 2 - 5));
        
        writer.close();
    }

    @Override
    public void update(int elapsedTime) 
    {
        if (RobotComponents.inst().getTouchSensorB().sample()[0] == 1) 
        {
            robot.stop();
            robot.move(360);
            free = 0;
            
            switch (choice) {
            case STRAIGHT:
                robot.turnOnSpot(-90);
                choice = Path.RIGHT;
                break;
            case RIGHT:
                robot.turnOnSpot(-90);
                choice = Path.STRAIGHT;
                break;
            default:
                break;
            }
            robot.forward();
        }
        else
        {
            free++;
            
            float samp = RobotComponents.inst().getUV().sample()[0];
            float turn = (samp - DISTANCE_SHOULD) * TURN_FACTOR;
            robot.steer(Math.max(-1.f, Math.min(1.f, turn)));
            robot.forward();
            
            gui.setVarValue(0, samp);
            gui.setVarValue(1, turn);
        }
        
        if (free > TIME_UNTIL_FREE) 
        {
            // we made it out (hopefully)
            choice = Path.STRAIGHT;
        }
    }
}
