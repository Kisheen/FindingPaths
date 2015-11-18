package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class EchoServer implements Runnable {
	
	private static final int PORT = 4040;
	Logger logger = Logger.getLogger(EchoServer.class.getName());
	private Map<InetAddress, Thread> echos;
	private BlockingQueue<Object> outputQueue;
	private BlockingQueue<Object> inputQueue;
	private CopyOnWriteArrayList<Socket> clients;
	Thread talker, processor;
	
	public EchoServer() {
		echos = new HashMap<InetAddress, Thread>();
		outputQueue = new ArrayBlockingQueue<Object>(1024);
		inputQueue = new ArrayBlockingQueue<Object>(1024);
		clients = new CopyOnWriteArrayList<Socket>();
	}
	
	public void run() {
		try {
			logger.info("Starting up Echo Server.");
			ServerSocket server = new ServerSocket(PORT);
			ServerTalker talker = new ServerTalker(clients, outputQueue);
			ServerProcessor processor = new ServerProcessor(inputQueue, outputQueue);
			
			this.talker = new Thread(talker);
			this.processor = new Thread(processor);
			
			this.talker.start();
			this.processor.start();
			
			while(true)
			{
				try {
					Socket client = server.accept();
					Thread echoServer = new Thread(new ServerListener(client, inputQueue));
					echoServer.start();
					echos.put(client.getInetAddress(), echoServer);
					clients.add(client);
				} catch(IOException e)
				{
					logger.warning("Failed connecting to client.");
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			logger.warning("Failed to initialize server.");
			e.printStackTrace();
		}
	}
}
