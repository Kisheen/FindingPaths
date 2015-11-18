package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class ServerTalker implements Runnable {

	private ObjectOutputStream output;
	Logger logger = Logger.getLogger("ServerTalker");
	private BlockingQueue<Object> outputQueue;
	private CopyOnWriteArrayList<Socket> clients;
	
	public ServerTalker(CopyOnWriteArrayList<Socket> clients, BlockingQueue<Object> queue) {
		this.clients = clients;
		this.outputQueue = queue;
	}
	
	public void run() {
		while(true){
			try {
				Object out = outputQueue.take();
				for(Socket client : clients)
				{
					try {
					output = new ObjectOutputStream(client.getOutputStream());
					output.writeObject(out);
					output.flush();
					} catch (IOException e) {
						logger.warning("Failed to write to " + client.getInetAddress());
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e) {
				logger.warning("Failed to take from output queue.");
				e.printStackTrace();
			}
		}
	}

}
