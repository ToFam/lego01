package util;

public class Regulation
{
	private float kp = 1.0f;
	private float currentError = 0.0f;
	
	public Regulation(float kp)
	{
		this.kp = kp;
	}
	
	public float getY()
	{
		return kp * currentError;
	}
	
	public void setError(float e)
	{
		currentError = e;
	}
}
