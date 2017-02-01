package sensor;

import lejos.hardware.sensor.EV3GyroSensor;

public class GyroSensor extends SensorThread {
	
	public GyroSensor(EV3GyroSensor sensor) {
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
