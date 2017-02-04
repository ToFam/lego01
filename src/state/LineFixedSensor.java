package state;

import robot.Robot;
import robot.RobotComponents;
import sensor.modes.ColorSensorMode;
import util.Util;
import util.lcdGui.LCDGui;

public class LineFixedSensor implements ParcourState {
    
    private Robot robot;
    private LCDGui gui;
    
    private enum State {
        START,
        FOLLOWING
    }
    private State state;
    
    public LineFixedSensor(Robot robot) {
        this.robot = robot;
    }

    @Override
    public String getName() {
        return "LineFixedSensor";
    }
    
	@Override
	public boolean changeOnBarcode()
	{
		return true;
	}

    @Override
    public void init() {
        gui = new LCDGui(4,2);
        state = State.START;
        
        RobotComponents.inst().getColorSensor().setMode(ColorSensorMode.RGB.getIdf());
        robot.forward();
    }

    @Override
    public void update(int elapsedTime) {
        switch (state) {
        case START:
            if (Util.howMuchOnLine(RobotComponents.inst().getColorSensor().sample()) > 0.17f) {
                state = State.FOLLOWING;
            }
            break;
        case FOLLOWING:
            
        }
    }

    @Override
    public void reset() {
        robot.stop();
    }

}
