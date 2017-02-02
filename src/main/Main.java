package main;

import java.util.ArrayList;
import java.util.List;

import lcdGui.LCDChooseList;
import lcdGui.LCDGui;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import robot.Robot;
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
    
    private List<ParcourState> states;
    private int state;
    
    public Main() {
        states = new ArrayList<ParcourState>();
        robot = new Robot();
        
        String elements[] = new String[6];
        states.add(new LineState(robot));
        elements[0] = states.get(0).getName();
        states.add(new LineSlowMode(robot));
        elements[1] = states.get(1).getName();
        states.add(new LabyrinthState(robot));
        elements[2] = states.get(2).getName();
        states.add(new TestDriveLTWall(robot));
        elements[3] = states.get(3).getName();
        states.add(new TestGUI(robot));
        elements[4] = states.get(4).getName();
        states.add(new TestState(robot));
        elements[5] = states.get(5).getName();
        mainMenu = new LCDChooseList(elements);
        
        state = 0;
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
            states.get(state).init();
        
            while (!Util.isPressed(Button.ENTER.getId()))
            {
                states.get(state).update(50);
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            states.get(state).reset();
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
