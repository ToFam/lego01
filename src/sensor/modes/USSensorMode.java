package sensor.modes;

public enum USSensorMode {
	DISTANCE(0), ONLY_LISTEN(1);
	
	private int identifier;
	private USSensorMode(int c0)
	{
		this.identifier = c0;
	}

	public int getIdf(){
		return this.identifier;
	}
}