package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerTalker implements Runnable {

	Logger logger = LoggerFactory.getLogger(ServerTalker.class);
	private BlockingQueue<Object> outputQueue;
	private CopyOnWriteArrayList<ObjectOutputStream> clients;
	
	public ServerTalker(CopyOnWriteArrayList<ObjectOutputStream> clients, BlockingQueue<Object> queue) {
		this.clients = clients;
		this.outputQueue = queue;
	}
	
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try {
				Object out = outputQueue.take();
				for(ObjectOutputStream client : clients)
				{
					try {
					client.writeObject(out);
					client.flush();
					} catch (IOException e) {
						logger.warn("Failed to write to client.");
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e) {
				logger.error("Failed to take from output queue.");
				e.printStackTrace();
			}
		}
		
		shutdown();
	}

	private void shutdown() {
		for(ObjectOutputStream client : clients)
			try {
				client.close();
			} catch (IOException e) {
				logger.warn("Failed to close client socket?");
				e.printStackTrace();
			}
	}
}
