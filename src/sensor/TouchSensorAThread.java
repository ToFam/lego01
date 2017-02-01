package sensor;

import lejos.hardware.sensor.EV3TouchSensor;

public class TouchSensorAThread extends SensorThread {
    
    public TouchSensorAThread(EV3TouchSensor sensor) {
        super(sensor);
    }
    
    @Override
    public void run() {
        
        while (true) {
            while (!running) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            sensor.fetchSample(sample, 0);
            
        }
        
    }

}
