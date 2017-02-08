package state;

import java.io.File;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import util.lcdGui.LCDGui;

public class Music implements ParcourState {
    
    private String filename;
    
    public Music(String filename)
    {
        this.filename = filename;
    }

    @Override
    public String getName() {
        return "Music";
    }

    @Override
    public void init() {
        File f = new File(filename);
        
        int rtn = LocalEV3.get().getAudio().playSample(f, 100);
        LCDGui.clearLCD();
        LCD.drawInt(rtn, 2, 2);
    }

    @Override
    public void update(int elapsedTime) {
    }

    @Override
    public void reset() {
    }

    @Override
    public boolean changeOnBarcode() {
        return true;
    }

    @Override
    public boolean changeImmediately() {
        return false;
    }

}
