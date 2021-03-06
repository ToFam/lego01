package sensor.modes;


public enum GyroSensorMode {
	ANGLE(1), ANGLE_SPEED(0), BOTH(2);
	
	private int identifier;
	private GyroSensorMode(int c0)
	{
		this.identifier = c0;
	}

	public int getIdf(){
		return this.identifier;
	}
}