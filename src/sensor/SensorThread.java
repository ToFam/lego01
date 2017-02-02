package sensor;

import lejos.hardware.sensor.BaseSensor;

public abstract class SensorThread extends Thread {
	
	protected boolean running;
	protected BaseSensor sensor;
	protected float[] sample;
	/**
	 * This is, what was sample before. It contains the most current measured value
	 */
	protected float[] instSample;
	/**
	 * Sets the amount of samples, where the median is beeing calculated, before the sample is provided to other classes
	 */
	protected int medianAmount = 1;
	protected int medianCounter = 0;
	protected float[] medianSample;
	protected float[] calculationTempValues;
	
	/**
	 * Create a threaded sensor
	 * @param sensor initialized sensor
	 */
	protected SensorThread(BaseSensor sensor) {
	    this.sensor = sensor;
	    sample = new float[sampleSize()];
	    instSample = new float[sampleSize()];
	    medianSample = new float[sampleSize()];
	    calculationTempValues = new float[sampleSize()];
	    running = true;
	}
	
    public synchronized void setMode(int mode) {
        sensor.setCurrentMode(mode);
        sample = new float[sensor.sampleSize()];
	    instSample = new float[sampleSize()];
	    medianSample = new float[sampleSize() * medianAmount];
	    calculationTempValues = new float[sampleSize()];
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
	 * The median fetched sensor data
	 * @return an array of sensor data
	 */
	public float[] sample() {
	    return sample;
	}
	
	/**
	 * The latest fetched sensor data
	 * @return an array of sensor data
	 */
	public float[] instSample() {
	    return instSample;
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
	
	/**
	 * Sets the amount of samples, over which the median is beeing calculated before the values can be accessed
	 * @param medianAmount The amount greater than 0
	 */
	public void setMedianFilter(int medianAmount)
	{
		this.medianAmount = medianAmount >= 1 ? medianAmount : 1;
		sample = new float[sensor.sampleSize()];
	    instSample = new float[sampleSize()];
	    medianSample = new float[sampleSize() * medianAmount];
	    calculationTempValues = new float[sampleSize()];
	}

}
