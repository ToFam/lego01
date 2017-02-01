package state;

import lcdGui.LCDGui;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import util.FloatTuple;
import util.Util;

public class LineState implements ParcourState {
	
    private Robot robot;
    private LCDGui gui;
    private float leftScale;
    private float rightScale;
    
    private final float threshold;
    
    /*
     * valid from [0, endIndex)
     */
    private FloatTuple[] sample;
    private int endIndex;
    private boolean left;
    
    public LineState(Robot robot, LCDGui gui) {
        this.robot = robot;
        this.gui = gui;
        this.leftScale = 0.f;
        this.rightScale = 0.f;
        
        this.threshold = 0.02f;
        
        this.sample = new FloatTuple[50000];
        this.endIndex = 0;
        this.left = true;
        
        
    }

	@Override
	public void init() {

		RobotComponents.inst().getMediumMotor().rotate(Util.getEffectiveAngle(25), true);
		this.left = true;
	}

	@Override
	public void update() {
		
		this.scan();
		this.evaluate();
		this.adapt();
		
		left = !left;
		
	}
	
	private void scan() {
		
		int counter = 0;
        RobotComponents.inst().getMediumMotor().resetTachoCount();
        
		if (this.left) {
			RobotComponents.inst().getMediumMotor().rotate(Util.getEffectiveAngle(-50), true);
		} else {
			RobotComponents.inst().getMediumMotor().rotate(Util.getEffectiveAngle(50), true);
		}
		
		while (Math.abs(RobotComponents.inst().getMediumMotor().getTachoCount()) < Util.getEffectiveAngle(45)) {
			this.sample[counter] = RobotComponents.inst().getColorSensor().getFloatTuple();
			counter++;
		}
		
		this.endIndex = counter;
		
		gui.writeLine(String.valueOf(this.endIndex));
		
	}
	
	private void evaluate() {
		
		int l = 0; //left bound == first encounter with line from the left
		int r = 0; //right bound == last encounter with line from the left
		int i = 0;
		
		while (this.sample[i].getF1() < threshold && i < this.endIndex) {
			i++;
		}
		
		if (i >= this.endIndex) {
			
			/*
			 * NO LINE FOUND!
			 */
			
		}
		
		l = i;
		i = this.endIndex - 1;
		
		while (this.sample[i].getF1() < threshold && i >= 0) {
			i--;
		}
		
		r = this.endIndex - i - 1;
		
		if (l == 0 && r != 0) {
			if (left) {
				//turn left
				this.leftScale = .8f;
				this.rightScale = 1.f;
			} else {
				//turn right
				this.leftScale = 1.f;
				this.rightScale = .8f;
			}
		} else if (l != 0 && r == 0) {
			if (left) {
				//turn right
				this.leftScale = 1.f;
				this.rightScale = .8f;
			} else {
				//turn left
				this.leftScale = .8f;
				this.rightScale = 1.f;
			}
		} else {
			
			/*
			float direction;
			
			if (left) {
				direction = (r - l) / this.sample.length;
			} else {
				direction = (l - r) / this.sample.length;
			}
			
			if (direction < 0) {
				//turn right
			} else if (direction > 0) {
				//turn left
			} else {
				//go straight
			}
			*/
			
			this.leftScale = 1.f;
			this.rightScale = 1.f;
		}
		
	}
	
	private void adapt() {
		robot.setSpeed(this.leftScale, this.rightScale);
		robot.forward();
	}

}
