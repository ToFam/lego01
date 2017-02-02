package util;

public class MediumMotorTuple {
	private float sensorData;
	private int tachoCount;
	
	public MediumMotorTuple(float sensorData, int tachoCount) {
		this.sensorData = sensorData;
		this.tachoCount = tachoCount;
	}
	
	public float getF1() {
		return this.sensorData;
	}
	
	public float getF2() {
		return this.tachoCount;
	}
}
