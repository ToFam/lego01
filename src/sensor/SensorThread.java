package sensor;

import lejos.hardware.sensor.BaseSensor;

public abstract class SensorThread extends Thread {
	
	protected boolean running;
	protected BaseSensor sensor;
	protected float[] sample;
	
	/**
	 * Create a threaded sensor
	 * @param sensor initialized sensor
	 */
	protected SensorThread(BaseSensor sensor) {
	    this.sensor = sensor;
	    sample = new float[sampleSize()];
	    running = true;
	}
	
    public synchronized void setMode(int mode) {
        sensor.setCurrentMode(mode);
        sample = new float[sensor.sampleSize()];
    }
	
	/**
	 * Convenience function to access other sensor elements. Should not be used if avoidable.
	 * @return the sensor
	 */
	public BaseSensor getSensor() {
		return sensor;
	}
	
	/**
	 * @return the size of the sample array
	 */
	public int sampleSize() {
	    return sensor.sampleSize();
	}
	
	/**
	 * The latest fetched sensor data
	 * @return an array of sensor data
	 */
	public float[] sample() {
	    return sample;
	}
	
	/**
	 * While running, the sensor actively fetches data
	 * @return whether the sensor is running
	 */
	public boolean runnning() {
	    return running;
	}
	
	/**
	 * Change the running status of the sensor
	 * @param running
	 */
	public void setRunning(boolean running) {
		this.running = running;
		if (running)
		    notifyAll();
	}

}
