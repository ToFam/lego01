package sensor;

import lejos.hardware.sensor.EV3ColorSensor;

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
}
