package sensor;

import lejos.hardware.sensor.EV3ColorSensor;
import robot.RobotComponents;

public class ColorSensorThread extends SensorThread {

	EV3ColorSensor sensor;
	float[] sample;
	
	public ColorSensorThread(WriteBackStorage storage) {
		super(storage);
	}
	
	@Override
	public void run() {

		sensor = RobotComponents.inst().getColorSensor();
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
			
			if (running || true)
			{
				if (sensor.getCurrentMode() != oldMode)
				{
					sample = new float[sensor.sampleSize()];
					oldMode = sensor.getCurrentMode();
				}
				
				sensor.fetchSample(sample, 0);
				storage.setColor(sample);
			}
			
			
		}
		
	}
	
	/**
	 * Changes the sensor mode.
	 * 
	 * @param mode
	 * @return If the mode was changed
	 */
	public boolean setMode(int mode) {
		if (sensor != null)
		{
			sensor.setCurrentMode(mode);
			return true;
		}
		return false;
	}
}
