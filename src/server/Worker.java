package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class Worker implements Runnable {

	Logger logger;
	private Socket client;
	private ObjectInputStream input;
	
	public Worker(Socket client) {
		logger = Logger.getLogger("Worker-" + client.getInetAddress());
		this.client = client;
		
		try {
			input = new ObjectInputStream(this.client.getInputStream());
		} catch (IOException e) {
			logger.warning("Failed to open input stream.");
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			try {
				Object in = input.readObject();
				logger.info("Read " + in);
			} catch (ClassNotFoundException e) {
				logger.warning("Failed to read object.");
				e.printStackTrace();
			} catch (IOException e) {
				logger.warning("Failed to receive object.");
				e.printStackTrace();
			}
		}
	}
}
