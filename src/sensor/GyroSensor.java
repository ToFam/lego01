package sensor;

import lejos.hardware.sensor.EV3GyroSensor;
import robot.RobotComponents;

public class GyroSensor extends SensorThread {
	
	EV3GyroSensor sensor;
	float[] sample;
	
	public GyroSensor(WriteBackStorage storage) {
		super(storage);
	}
	
	@Override
	public void run() {

		sensor = RobotComponents.inst().getGyroSensor();
		sample = new float[sensor.sampleSize()];
		running = true;
		
		while (true) {
			
			if (!running) {
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if (sensor.getCurrentMode() != oldMode)
			{
				sample = new float[sensor.sampleSize()];
				oldMode = sensor.getCurrentMode();
			}
			
			sensor.fetchSample(sample, 0);
			storage.setGyro(sample);
			
		}
		
	}
}
