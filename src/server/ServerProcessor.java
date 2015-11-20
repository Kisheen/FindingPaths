package server;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphics.FindingPathsGUI;

public class ServerProcessor implements Runnable {
	
	private BlockingQueue<Object> input, output;
	private FindingPathsGUI gui;
	Logger logger = LoggerFactory.getLogger("Server Processor");
	
	public ServerProcessor(
			BlockingQueue<Object> inputQueue, 
			BlockingQueue<Object> outputQueue,
			FindingPathsGUI gui) {
		this.gui = gui;
		input = inputQueue;
		output = outputQueue;
	}

	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				Object out = input.take();
				output.put(out);
			} catch (InterruptedException e) {
				logger.error("Failed to access queues.");
				e.printStackTrace();
			}
		}
	}
}
