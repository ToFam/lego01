package sensor;

import lejos.hardware.sensor.EV3TouchSensor;
import robot.RobotComponents;

public class TouchSensorAThread extends SensorThread {
	
	EV3TouchSensor sensor;
	float[] sample;
	
	public TouchSensorAThread(WriteBackStorage storage) {
		super(storage);
	}
	
	@Override
	public void run() {

		sensor = RobotComponents.inst().getTouchSensorA();
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
			storage.setTouchA(sample);
			
		}
		
	}

}
