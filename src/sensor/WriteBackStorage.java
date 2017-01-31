package sensor;

public class WriteBackStorage {
	
	private float[] uvSamples;
	private float[] colorSamples;
	
	public WriteBackStorage() {
		uvSamples = new float[1];
		uvSamples[0] = -1f;
		colorSamples = new float[1];
		colorSamples[0] = -1f;
	}
	
	public float getUV() {
		if (uvSamples.length > 0)
			return uvSamples[0];
		return -1f;
	}
	
	public float getUV(int index) {
		if (index >= 0 && index < uvSamples.length)
		{
			return this.uvSamples[index];
		}
		return -1f;
	}
	
	public int uvSampleSize()
	{
		return uvSamples.length;
	}
	
	public void setUV(float[] uvs) {
		this.uvSamples = uvs;
	}
	
	public float getColor() {
		if (colorSamples.length > 0)
			return this.colorSamples[0];
		return -1f;
	}
	
	public float getColor(int index) {
		if (index >= 0 && index < colorSamples.length)
		{
			return this.colorSamples[index];
		}
		return -1f;
	}
	
	public int colorSampleSize()
	{
		return colorSamples.length;
	}
	
	public void setColor(float[] colors) {
		this.colorSamples = colors;
	}
}
