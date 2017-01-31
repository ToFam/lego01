package sensor;

public class WriteBackStorage {
	
	private float[] uvSamples;
	private float[] colorSamples;
	private float[] gyroSamples;
	private float[] touchASamples;
	private float[] touchBSamples;
	
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
	
	public float getGyro() {
		if (gyroSamples.length > 0)
			return this.gyroSamples[0];
		return -1f;
	}
	
	public float getGyro(int index) {
		if (index >= 0 && index < gyroSamples.length)
		{
			return this.gyroSamples[index];
		}
		return -1f;
	}
	
	public int gyroSampleSize()
	{
		return gyroSamples.length;
	}
	
	public void setGyro(float[] gyros) {
		this.gyroSamples = gyros;
	}
	
	public float getTouchA() {
		if (touchASamples.length > 0)
			return this.touchASamples[0];
		return -1f;
	}
	
	public float getTouchA(int index) {
		if (index >= 0 && index < touchASamples.length)
		{
			return this.touchASamples[index];
		}
		return -1f;
	}
	
	public int touchASampleSize()
	{
		return touchASamples.length;
	}
	
	public void setTouchA(float[] touchAs) {
		this.touchASamples = touchAs;
	}
	
	public float getTouchB() {
		if (touchBSamples.length > 0)
			return this.touchBSamples[0];
		return -1f;
	}
	
	public float getTouchB(int index) {
		if (index >= 0 && index < touchBSamples.length)
		{
			return this.touchBSamples[index];
		}
		return -1f;
	}
	
	public int touchBSampleSize()
	{
		return touchBSamples.length;
	}
	
	public void setTouchB(float[] touchBs) {
		this.touchBSamples = touchBs;
	}
}
