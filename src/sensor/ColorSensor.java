package sensor;

import lejos.hardware.sensor.EV3ColorSensor;
import robot.RobotComponents;
import util.FloatTuple;

public class ColorSensor extends SensorThread {
	
	public ColorSensor(EV3ColorSensor sensor) {
		super(sensor);
	}

    @Override
    public void run() {
        
        while (true) {
            while (!running) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            sensor.fetchSample(sample, 0);
            
        }
        
    }
    
    public FloatTuple getFloatTuple() {
    	return new FloatTuple(sample[0], RobotComponents.inst().getMediumMotor().getTachoCount());
    }
}
