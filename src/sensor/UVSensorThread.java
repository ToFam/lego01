package sensor;

import lejos.robotics.SampleProvider;
import robot.RobotComponents;
import util.Util;

public class UVSensorThread extends SensorThread {

	SampleProvider provider;
	float[] sample;
	
	public UVSensorThread(WriteBackStorage storage) {
		super(storage);
	}
	
	@Override
	public void run() {

		provider = RobotComponents.inst().getUV();
		running = true;
		
		while (true) {
			
			if (!running) {
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			provider.fetchSample(sample, 0);
			storage.setUV(Util.average(sample));
			
		}
		
	}
}
