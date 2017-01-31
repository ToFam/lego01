package sensor;

public abstract class SensorThread extends Thread {
	
	protected WriteBackStorage storage;
	protected boolean running;
	protected int oldMode = -1;
	
	protected SensorThread(WriteBackStorage storage) {
		this.storage = storage;
	}
	
	public WriteBackStorage getStorage() {
		return this.storage;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}

}
