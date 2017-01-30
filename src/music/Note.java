package music;

/**
 * 
 * @author	Team AndreasBot: Simon
 *
 */
public enum Note {
	C0(0), CS0(0), D0(0), DS0(0), E0(0), F0(0), FS0(0), G0(0), GS0(0), A0(110), AS0(0), H0(0),
	C1(0), CS1(0), D1(0), DS1(0), E1(0), F1(0), FS1(0), G1(0), GS1(0), A1(220), AS1(0), H1(0), 
	C2(0), CS2(0), D2(0), DS2(0), E2(0), F2(0), FS2(0), G2(0), GS2(0), A2(440), AS2(0), H2(0), 
	C3(0), CS3(0), D3(0), DS3(0), E3(0), F3(0), FS3(0), G3(0), GS3(0), A3(880), AS3(0), H3(0), 
	C4(0), CS4(0), D4(0), DS4(0), E4(0), F4(0), FS4(0), G4(0), GS4(0), A4(880*2), AS4(0), H4(0), 
	C5(0), CS5(0), D5(0), DS5(0), E5(0), F5(0), FS5(0), G5(0), GS5(0), A5(880*4), AS5(0), H5(0), 
	C6(0), CS6(0), D6(0), DS6(0), E6(0), F6(0), FS6(0), G6(0), GS6(0), A6(880*8), AS6(0), H6(0), 
	C7(0), CS7(0), D7(0), DS7(0), E7(0), F7(0), FS7(0), G7(0), GS7(0), A7(880*16), AS7(0), H7(0);
	
private int c0;
private Note(int c0)
{
	this.c0 = c0;
}

public int getInt(){
	return this.c0;
}
}

