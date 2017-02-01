package sensor;

import lejos.hardware.sensor.EV3UltrasonicSensor;
import robot.RobotComponents;

public class UVSensorThread extends SensorThread {

	EV3UltrasonicSensor sensor;
	private float[] sample;
	
	public UVSensorThread(WriteBackStorage storage) {
		super(storage);
	}

	public UVSensorThread() {
		super(null);
	}
	
	public float[] getSamples()
	{
		return sample;
	}
	
	@Override
	public void run() {

		sensor = RobotComponents.inst().getUV();
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
			storage.setUV(sample);
			
		}
		
	}
}
