package sensor;

import lejos.hardware.sensor.EV3UltrasonicSensor;

public class UVSensor extends SensorThread {
	
	public UVSensor(EV3UltrasonicSensor sensor) {
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
