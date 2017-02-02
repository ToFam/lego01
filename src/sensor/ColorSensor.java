package sensor;

import lejos.hardware.sensor.EV3ColorSensor;
import robot.RobotComponents;
import util.MediumMotorTuple;

public class ColorSensor extends SensorThread {
	
	public ColorSensor(EV3ColorSensor sensor) {
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
            
            if (medianCounter < medianAmount)
            {
                sensor.fetchSample(medianSample, medianCounter * sample.length);
                for (int i = 0; i < instSample.length; i++)
                {
                    instSample[i] = medianSample[medianCounter * sample.length + i];
                }
                medianCounter++;
            }
            
            // Ist absichtlich keine else, da es gewollt ist, dass in manchen Fallen in beide Schleifen gegangen wird
            if (medianCounter >= medianAmount)
            {
            	medianCounter = 0;
            	
            	if (medianAmount > 1)
            	{
            		for (int i = 0; i < calculationTempValues.length; i++)
                	{
                		calculationTempValues[i] = 0.0f;
                	}
                	for (int i = 0; i < sample.length; i++)
                	{
                		for (int j = 0; j < medianAmount; j++)
                		{
                			calculationTempValues[i] += medianSample[j * sample.length + i];
                		}
                	}
                	
                	for (int i = 0; i < calculationTempValues.length; i++)
                	{
                		sample[i] = calculationTempValues[i] / medianAmount;
                	}
            	}
            	else
            	{
            		for (int i = 0; i < sample.length; i++)
            		{
            			sample[i] = medianSample[i];
            		}
            	}
            	
            }
        }
    }
    
//    public FloatTuple getFloatTuple() {
//    	return new FloatTuple(sample[0], RobotComponents.inst().getMediumMotor().getTachoCount());
//    }
}
