package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class ServerListener implements Runnable {

	Logger logger;
	private Socket client;
	private ObjectInputStream input;
	private BlockingQueue<Object> inputQueue;
	
	public ServerListener(Socket client, BlockingQueue<Object> queue) {
		logger = Logger.getLogger("Worker-" + client.getInetAddress());
		logger.info("Connecting to " + client.getInetAddress());
		this.client = client;
		this.inputQueue = queue;
		
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
				logger.info("Read from client " + in);
				inputQueue.put(in);
			} catch (ClassNotFoundException e) {
				logger.warning("Failed to read object.");
				e.printStackTrace();
			} catch (IOException e) {
				logger.warning("Failed to receive object.");
				e.printStackTrace();
			} catch (InterruptedException e) {
				logger.warning("Failed to put object in queue.");
				e.printStackTrace();
			}
		}
	}
}
