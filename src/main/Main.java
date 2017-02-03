package main;

import java.util.ArrayList;
import java.util.List;

import lcdGui.LCDChooseList;
import lcdGui.LCDGui;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import robot.Robot;
import state.HumpbackBridgeState;
import state.LabyrinthState;
import state.LineSlowMode;
import state.LineState;
import state.ParcourState;
import state.TestDriveLTWall;
import state.TestGUI;
import state.TestState;
import util.Util;
/**
 * 
 * @author	Team AndreasBot: Simon,
 * 			Team AndreasBot: Tobias,
 * 			Team AndreasBot: Adrian
 *
 */
public class Main {
    private Robot robot;
    private LCDChooseList mainMenu;
    
    private final ParcourState[] states = {
    		new LabyrinthState(robot),
    		new TestDriveLTWall(robot),
    		new HumpbackBridgeState(robot),
    		new LineState(robot),
    		new LineSlowMode(robot),
    		new TestGUI(robot),
    		new TestState(robot),
    };
    private final String[] elements;
    private int state;
    
    public Main() {
        this.robot = new Robot();
        
        this.elements = new String[states.length];
        for (int i = 0; i < elements.length; i++) {
        	this.elements[i] = states[i].getName();
        }
        
        this.mainMenu = new LCDChooseList(elements);
        this.state = 0;
        
    }
    
    public void run()
    {
        while (true) {
            mainMenu.repaint();
            
            int btn;
            do {
                btn = Button.waitForAnyPress();
                
                if (Util.isPressed(Button.UP.getId())) {
                    mainMenu.moveOneUp();
                }
                else if (Util.isPressed(Button.DOWN.getId())) {
                    mainMenu.moveOneDown();
                }
                
            } while (btn != Button.RIGHT.getId());

            LCDGui.clearLCD();
            state = mainMenu.getCurrentSelected();
            states[state].init();
        
            while (!Util.isPressed(Button.ENTER.getId()))
            {
                states[state].update(50);
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            states[state].reset();
        }
    }
    
	public static void main(String[] args)
	{
		Button.ESCAPE.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(Key k) {
			}
			@Override
			public void keyPressed(Key k) {
				System.exit(0);
			}
		});
		
	    Main m = new Main();
	    m.run();
	}

}
