package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphics.FindingPathsGUI;

public class ClientListener implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(ClientListener.class);
	private Socket server;
	private ObjectInputStream input;
	private FindingPathsGUI gui;
	
	public ClientListener(Socket server, FindingPathsGUI gui) {
		this.server = server;
		this.gui = gui;
		
		try {
			input = new ObjectInputStream(server.getInputStream());
		} catch (IOException e) {
			logger.error("Failed to open input stream");
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try {
				Object in = input.readObject();
				gui.addText(new String((byte[])in));
				logger.info("Read from server " + in);
			} catch (ClassNotFoundException e) {
				logger.error("Failed to read object.");
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("Failed to receive object.");
				e.printStackTrace();
			}
		}
		
		shutdown();
	}
	
	private void shutdown() {
		try {
			input.close();
			server.close();
		} catch (IOException e) {
			logger.error("Failed to close server?");
			e.printStackTrace();
		}
	}
}
