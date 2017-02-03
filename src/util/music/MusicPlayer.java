package util.music;

import lejos.hardware.Sound;

/**
 * 
 * @author	Team AndreasBot: Simon
 *
 */
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
