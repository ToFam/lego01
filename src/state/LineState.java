package state;

import lcdGui.LCDGui;
import lejos.hardware.lcd.LCD;
import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import util.MediumMotorTuple;
import util.Util;

public class LineState implements ParcourState {
	
	private final int angle;
	
    private Robot robot;
    private LCDGui gui;
    
    private final float threshold;
    
    /*
     * valid from [0, endIndex)
     */
    private MediumMotorTuple[] sample;
    private int endIndex;
    private boolean left;
    
    public LineState(Robot robot) {
    	this.angle = 130;
    	
        this.robot = robot;
        this.gui = new LCDGui(4,2);
        
        this.threshold = 0.5f;
        
        this.sample = new MediumMotorTuple[50000];
        this.endIndex = 0;
        this.left = true;
        
        
    }
    
    public String getName() {
        return "Line";
    }

    public void reset() {
        
    }

	@Override
	public void init() {

		RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RGB.getIdf());
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
		
		while (Math.abs(RobotComponents.inst().getMediumMotor().getTachoCount()) < angle - 5) {
			this.sample[counter] = new MediumMotorTuple(
					Util.howMuchOnLine(RobotComponents.inst().getColorSensor().sample()),
					RobotComponents.inst().getMediumMotor().getTachoCount()
					);
			counter++;
		}
		
		this.endIndex = counter;
		
		gui.setVarValue(0, String.valueOf(this.endIndex));
		
	}
	
	private void adapt() {
		
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
		
		r = i;
		
		float mid = (r - l) / 2;
		float linterpol = (1 - mid) * 1 + mid * -1;
		
		if (linterpol > 0) {
			robot.setSpeed(1.f - linterpol, 1.f);
			robot.forward();
		} else {
			robot.setSpeed(1.f, 1.f + linterpol);
			robot.forward();
		}
		
	}
}
