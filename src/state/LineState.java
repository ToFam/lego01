package state;

import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import util.Util;
import util.lcdGui.LCDGui;

public class LineState implements ParcourState {
	
	private final int ANGLE;
	private final int NO_ERROR;
	
    private Robot robot;
    private LCDGui gui;
    
    private final float THRESHOLD;
    private final float COMFORT_ZONE;
    private final float SPEED;
    
    /*
     * valid from [0, endIndex)
     */
    private float[] sample;
    private int endIndex;
    private float mid;
    private float linterpol;
    private boolean left;
    
    public LineState(Robot robot) {
    	this.ANGLE = 130;
    	this.COMFORT_ZONE = 1.f;
    	
        this.robot = robot;
        this.gui = new LCDGui(4,2);
        
        this.THRESHOLD = 0.4f;
        this.NO_ERROR = 5;
        this.SPEED = .5f;
        
        this.sample = new float[50000];
        this.endIndex = 0;
        this.mid = 0.5f;
        this.linterpol = 0.f;
        this.left = true;
        
        
    }
    
	@Override
	public boolean changeOnBarcode()
	{
		return true;
	}
    
    public String getName() {
        return "Line";
    }

    public void reset() {
        if (left) {
    		RobotComponents.inst().getMediumMotor().rotate(-this.ANGLE / 2 - 5, false);
        } else {
    		RobotComponents.inst().getMediumMotor().rotate(this.ANGLE / 2 - 5, false);
        }
//        robot.setSpeed(1.f);
//        robot.forward();
    }

	@Override
	public void init() {

		RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RGB.getIdf());
//		RobotComponents.inst().getColorSensor().setMedianFilter(10);
		RobotComponents.inst().getMediumMotor().rotate(this.ANGLE / 2 - 5, false);
		this.left = true;
		
//		robot.setSpeed(this.SPEED);
//		robot.forward();
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
			RobotComponents.inst().getMediumMotor().rotate(-this.ANGLE, true);
		} else {
			RobotComponents.inst().getMediumMotor().rotate(this.ANGLE, true);
		}
		
		while (Math.abs(RobotComponents.inst().getMediumMotor().getTachoCount()) < ANGLE - 1) {
			if (Math.abs(RobotComponents.inst().getMediumMotor().getTachoCount()) > 1)
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
			if (this.sample[i] >= this.THRESHOLD) {
				if (errorPreventionCount < NO_ERROR) {
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
//			robot.backward();
//			if (this.left) {
//				RobotComponents.inst().getMediumMotor().rotate(-this.ANGLE, false);
//			} else {
//				RobotComponents.inst().getMediumMotor().rotate(this.ANGLE, false);
//			}
//			robot.stop();
//			if (this.left) {
//				RobotComponents.inst().getMediumMotor().rotate(-this.ANGLE, false);
//				RobotComponents.inst().getMediumMotor().rotate(-this.ANGLE, false);
//			} else {
//				RobotComponents.inst().getMediumMotor().rotate(this.ANGLE, false);
//				RobotComponents.inst().getMediumMotor().rotate(this.ANGLE, false);
//			}
		}

		l = i - this.NO_ERROR;
		i = this.endIndex - 1;
		errorPreventionCount = 0;
		
		while (i >= 0) {
			if (this.sample[i] >= this.THRESHOLD) {
				if (errorPreventionCount < NO_ERROR) {
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

		r = i + this.NO_ERROR;
		
		gui.clearLCD();
		gui.writeLine("l: " + String.valueOf(l));
		gui.writeLine("r: " + String.valueOf(r));
		
		this.mid = (((float) (r + l) / 2.f) / this.endIndex);
		
//		if (!left) {
//			this.mid = 1.f - this.mid;
//		}
		this.linterpol = (1 - mid) * 1 + mid * -1;
		this.linterpol *= /*1.f / this.linterpol / this.linterpol **/ this.SPEED;
		
		gui.writeLine(" - " + this.endIndex + " - ");
		gui.writeLine(" +++ " + this.linterpol + " +++ ");
		
		if (this.linterpol > 0) {
			gui.writeLine(String.valueOf(Math.max(this.SPEED - this.COMFORT_ZONE * this.linterpol, 0.f)));
			robot.setSpeed(Math.max(this.SPEED - this.COMFORT_ZONE * this.linterpol, 0.f), this.SPEED);
		} else {
			gui.writeLine(String.valueOf(Math.max(this.SPEED + this.COMFORT_ZONE * this.linterpol, 0.f)));
			robot.setSpeed(this.SPEED, Math.max(this.SPEED + this.COMFORT_ZONE * this.linterpol, 0.f));
		}
//		if (linterpol > 0) {
//			robot.setSpeed(Math.max(this.SPEED - this.linterpol, 0.f), this.SPEED);
//		} else {
//			robot.setSpeed(this.SPEED, Math.max(this.SPEED - this.linterpol, 0.f));
//		}

		robot.forward();
		
	}
}
