package rconsole;

import java.io.IOException;
import java.io.PrintStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class RConsole extends Thread
{
	static final int MODE_SWITCH = 0xff;
	static final int MODE_LCD = 0x0;
	
	static final int RCONSOLE_PORT = 8001;
	ServerSocket ss = null;
	Socket conn = null;
	PrintStream origOut = System.out, origErr = System.err;
	
	private TextLCD lcd = LocalEV3.get().getTextLCD();
	
	public RConsole()
	{
		super();
		setDaemon(true);
	}
	
	public boolean isConnected()
	{
		return (conn != null && conn.isConnected());
	}
	
	
	@Override
	public void run()
	{
		try
		{
			System.out.println("Waiting for a connection");
			conn = ss.accept();
			conn.setSoTimeout(2000);
		}
		catch (IOException e)
		{
			System.err.println("Error accepting connection " + e);
		}
	}
}
