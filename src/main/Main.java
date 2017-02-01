package main;

import lcdGui.LCDGui;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import robot.Robot;
import state.LineState;
import state.ParcourState;
import state.TestState;
/**
 * 
 * @author	Team AndreasBot: Simon,
 * 			Team AndreasBot: Tobias,
 * 			Team AndreasBot: Adrian
 *
 */
public class Main {

    private Robot robot;
    private ParcourState state;
    
    static boolean ultraLooksDown = false;
    
    public void run()
    {
        robot = new Robot();
        
        LCDGui gui = new LCDGui();
        
        
        state = new LineState(robot, gui);
        //state = new LineState(robot, gui);
        state.init();
        
        
        while (true)
        {
            
            state.update();
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
    }
    
	public static void main(String[] args)
	{
		Button.ESCAPE.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(Key k) {
				// TODO Auto-generated method stub
				
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
