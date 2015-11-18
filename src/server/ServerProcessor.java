package server;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class ServerProcessor implements Runnable {
	
	private BlockingQueue<Object> input;
	private BlockingQueue<Object> output;
	Logger logger = Logger.getLogger("Server Processor");
	
	public ServerProcessor(BlockingQueue<Object> inputQueue, BlockingQueue<Object> outputQueue) {
		input = inputQueue;
		output = outputQueue;
	}

	public void run() {
		while(true) {
			try {
				input.take();
				output.put("Nope.");
			} catch (InterruptedException e) {
				logger.warning("Failed to access queues.");
				e.printStackTrace();
			}
		}
	}
}
