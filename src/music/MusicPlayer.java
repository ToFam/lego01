package music;

import lejos.hardware.Sound;

public class MusicPlayer
{
	public MusicPlayer()
	{
		
	}
	
	public void playNote(Note note, int duration, int volume)
	{
		Sound.playTone(note.getInt(), duration, volume);
		
	}
}
