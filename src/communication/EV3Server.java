package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import graphicTest.ControlPanel;

public class EV3Server {
	
	private final int port;
	private ServerSocket serverSocket;
	private Socket localClient;
	private PrintWriter out;
	private BufferedReader in;
	
	private ControlPanel window;
	
	public EV3Server(int port) {
		this.window = new ControlPanel(this);
		this.port = port;
		try {
			this.serverSocket = new ServerSocket(port);
			this.localClient = serverSocket.accept();
			this.out = new PrintWriter(localClient.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(localClient.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			window.append(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		EV3Server server = new EV3Server(4443);
		String input;
		
		while(true) {
			input = server.readLine();
			System.out.println(input);
		}
	}
	
	public Socket getLocalSocket() {
		return this.localClient;
	}
	
	public void close() {
		try {
			serverSocket.close();
			localClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public String readLine() {
		try {
			return in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ServerSocket getServerSocket() {
		return this.serverSocket;
	}
	
}
