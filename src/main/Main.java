package main;

import java.util.List;

import lcdGui.LCDChooseList;
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
        robot = new Robot();
        String elements[] = new String[9];
        states.add(new LineState(robot));
        states.add(new LineSlowMode(robot));
        states.add(new LabyrinthState(robot));
        states.add(new TestDriveLTWall(robot));
        states.add(new TestGUI(robot));
        states.add(new TestState(robot));
        mainMenu = new LCDChooseList(elements);
    }
    
    public void run()
    {
        while (true) {
            mainMenu.repaint();
            
            while(!Util.isPressed(Button.RIGHT.getId())) {
                if (Util.isPressed(Button.UP.getId())) {
                    mainMenu.moveOneDown();
                }
                else if (Util.isPressed(Button.DOWN.getId())) {
                    mainMenu.moveOneDown();
                }
            }
            
            state = mainMenu.getCurrentSelected();
        
            while (!Util.isPressed(Button.ENTER.getId()))
            {
                states.get(state).update(50);
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
