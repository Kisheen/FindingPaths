package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientTalker implements Runnable {

	Logger logger = LoggerFactory.getLogger(ClientTalker.class);
	private Socket server;
	private ObjectOutputStream output;
	private BlockingQueue<Object> queue;
	
	public ClientTalker(Socket server, BlockingQueue<Object> queue) {
		this.server = server;
		this.queue = queue;
		
		try {
			output = new ObjectOutputStream(server.getOutputStream());
		} catch (IOException e) {
			logger.error("Failed to open output stream");
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				Object out = queue.take();
				output.writeObject(out);
				output.flush();
			} catch (IOException e) {
				logger.error("Failed to send to server.");
				e.printStackTrace();
			} catch (InterruptedException e) {
				logger.error("Failed to get output");
				e.printStackTrace();
			}
		}
		
		shutdown();
	}
	
	private void shutdown() {
		try {
			output.close();
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
