package main;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import state.HumpbackBridgeState;
import state.LabyrinthState;
import state.LineMovingIIt1;
import state.LineState;
import state.ParcourState;
import state.SuspBridgeState;
import state.SwampState;
import state.TestDriveLTWall;
import state.TestGUI;
import state.TestState;
import util.Util;
import util.lcdGui.LCDChooseList;
import util.lcdGui.LCDGui;
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
    
    private final ParcourState[] states;
    private final String[] elements;
    private int state;
    
    private static final float COLOR_THRESHOLD = 0.9f;
    private boolean onLine;
    private float colorSample;
    
    public Main() {
        this.robot = new Robot();
        
        this.states = new ParcourState[] {
        		new SuspBridgeState(robot),
        		new SwampState(robot),
    		new LabyrinthState(robot),			// Start und Labyrinth
    		new LineMovingIIt1(robot),			// Linie nach Labyrinth
    		new HumpbackBridgeState(robot),		// Große Brücke
    		new LineMovingIIt1(robot),			// Linie nach Brücke
    		// new Seesaw(robot),				// Wippe
    		// new LineMovingIIt1(robot),		// Linie nach Wippe
    		// new Swamp(robot),				// Sumpf
    		new LabyrinthState(robot),			// Wand nach Sumpf
    		// new SuspensionBridge(robot),		// Hängebrücke
    		// new BossFight(robot),			// Da Endboss
    		
    		new TestDriveLTWall(robot),
    		new TestGUI(robot),
    		new TestState(robot),
        };
        
        this.elements = new String[states.length];
        for (int i = 0; i < elements.length; i++) {
        	this.elements[i] = states[i].getName();
        }
        
        this.mainMenu = new LCDChooseList(elements);
        this.state = 0;
        this.colorSample = 0.f;
        this.onLine = false;
    }

    /**
     * Detect barcode
     * @return true iff line was detected
     */
    public boolean barcode()
    {
    	//colorSample = 4.f / 5.f * colorSample + 1.f / 5.f * RobotComponents.inst().getColorSensor().sample()[0];
    	float[][] colorSamps20 = RobotComponents.inst().getColorSensor().lastSamples(20);
    	float average = 0f;
    	for (int i = 0; i < colorSamps20.length; i++)
    	{
    		average += colorSamps20[i][0];
    	}
    	average /= colorSamps20.length;
    	//System.out.println(String.valueOf(cou) + "=" + String.valueOf(average));
    	//cou++;
    	//return false;
    	return average > COLOR_THRESHOLD;
    }
    
    public void run()
    {
    	boolean backToMenu = false;
    	int btn;
    	
    	RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RED.getIdf());
    	
    	// Main Menu Loop
        while (true) {
            mainMenu.repaint();
            
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
            
            // Normal sequential parcour operation
            while (!backToMenu)
            {
	            states[state].init();
	        
	            // Main loop with active state
	            while (true)
	            {
	                robot.update();
	                states[state].update(50);
	                
	                if (onLine)
	                {
	                    if (!barcode())
	                    {
	                        onLine = false;
	                        colorSample = 0.f;
	                    }
	                }
	                else if (states[state].changeImmediately() || (states[state].changeOnBarcode() && barcode()))
	                {
	                	onLine = true;
	                	// Barcode excepted and detected, change to next state
	    	            states[state].reset();
	    	            if (state < states.length - 1)
	    	            {
	    	            	state++;
	    	            	mainMenu.moveOneDown();
	    	            }
	    	            else
	    	            	backToMenu = true;
	                	break;
	                }
	                
	                if (Util.isPressed(Button.ID_ENTER))
	                {
	                	backToMenu = true;
	    	            states[state].reset();
	                	break;
	                }
	                
	                try {
	                    Thread.sleep(50);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
            }
            
            backToMenu = false;
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
