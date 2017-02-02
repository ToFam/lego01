package state;

import lcdGui.LCDGui;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import util.Util;

public class LineState implements ParcourState {
	
	private final int angle;
	private final int noError;
	
    private Robot robot;
    private LCDGui gui;
    
    private final float threshold;
    
    /*
     * valid from [0, endIndex)
     */
    private float[] sample;
    private int endIndex;
    private boolean left;
    
    public LineState(Robot robot) {
    	this.angle = 130;
    	
        this.robot = robot;
        this.gui = new LCDGui(4,2);
        
        this.threshold = 0.2f;
        this.noError = 10;
        
        this.sample = new float[50000];
        this.endIndex = 0;
        this.left = true;
        
        
    }
    
    public String getName() {
        return "Line";
    }

    public void reset() {
        if (left) {
    		RobotComponents.inst().getMediumMotor().rotate(-this.angle / 2 - 5, false);
        } else {
    		RobotComponents.inst().getMediumMotor().rotate(this.angle / 2 - 5, false);
        }
    }

	@Override
	public void init() {

		RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RGB.getIdf());
//		RobotComponents.inst().getColorSensor().setMedianFilter(10);
		RobotComponents.inst().getMediumMotor().rotate(this.angle / 2 - 5, false);
		this.left = true;
		
	}

	@Override
	public void update(int elapsedTime) {
		
		this.scan();
		this.adapt();
		
		left = !left;
		
	}
	
	private void scan() {
		
		int counter = 0;
        RobotComponents.inst().getMediumMotor().resetTachoCount();
        
		if (this.left) {
			RobotComponents.inst().getMediumMotor().rotate(-this.angle, true);
		} else {
			RobotComponents.inst().getMediumMotor().rotate(this.angle, true);
		}
		
		while (Math.abs(RobotComponents.inst().getMediumMotor().getTachoCount()) < angle - 2) {
			this.sample[counter] = Util.howMuchOnLine(RobotComponents.inst().getColorSensor().sample());
			counter++;
		}
		
		this.endIndex = counter;
		
	}
	
	private void adapt() {
		
		int l = 0; //left bound == first encounter with line from the left
		int r = 0; //right bound == last encounter with line from the left
		int i = 0;
		int errorPreventionCount = 0;
		
		while (i < this.endIndex) {
			if (this.sample[i] >= this.threshold) {
				if (errorPreventionCount < noError) {
					errorPreventionCount++;
				} else {
					//found left limit
					break;
				}
			} else {
				errorPreventionCount = 0;
			}
			i++;
//			gui.setVarValue(1, "LEFT: " + String.valueOf(i));
		}
		
		if (i == this.endIndex) {
			robot.stop();
			return;
		}

		l = i;
		i = this.endIndex - 1;
		errorPreventionCount = 0;
		
		while (i >= 0) {
			if (this.sample[i] >= this.threshold) {
				if (errorPreventionCount < noError) {
					errorPreventionCount++;
				} else {
					//found right limit
					break;
				}
			} else {
				errorPreventionCount = 0;
			}
			i--;
//			gui.setVarValue(1, "RIGHT: " + String.valueOf(i));
		}

		r = i;
		
		gui.clearLCD();
		gui.writeLine("l: " + String.valueOf(l));
		gui.writeLine("r: " + String.valueOf(r));
		
		float mid = ((float) Math.abs(r + l) / 2.f / this.endIndex);
		float linterpol = (1 - mid) * 1 + mid * -1;
		gui.writeLine(" - " + this.endIndex + " - ");
		gui.writeLine(" +++ " + linterpol + " +++ ");
		
		if (linterpol > 0) {
			robot.setSpeed((1.f - 2.f * linterpol) / 3.f, 1.f);
			robot.forward();
		} else {
			robot.setSpeed(1.f, (1.f + 2 * linterpol) / 3.f);
			robot.forward();
		}
		
	}
}
