package graphicTest;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.Instant;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import remoteTest.Main;
import robot.Robot;

public class ControlPanel extends JFrame{
	
	public ControlPanel() {	}
	
	private JTextArea tArea;
	private JScrollPane sPane;
	private boolean forward;
	
	public static void main(String[] args) {
		ControlPanel canvas = new ControlPanel();
		canvas.initialize();
	}
	
	private void initialize() {
		this.forward = false;
		this.setSize(600, 400);
		
		tArea = new JTextArea();
		tArea.setFocusable(false);
		sPane = new JScrollPane(tArea);
		this.add(sPane);
		
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				
				switch(e.getKeyCode()) {
				case 32:
					if (!forward) {
						forward = true;
						tArea.append("\tForward:\t" + Instant.now() + "\n");
					} else {
						forward = false;
						tArea.append("\tStop:\t" + Instant.now() + "\n\n");
					}
					break;
				case 38:
					if (!forward) {
						forward = true;
						tArea.append("\tForward:\t" + Instant.now() + "\n");
					}
					break;
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {

				switch(e.getKeyCode()) {
				case 38:
					forward = false;
					tArea.append("\tStop:\t" + Instant.now() + "\n\n");
					break;
				}
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
		});
		this.setVisible(true);
	}
}
