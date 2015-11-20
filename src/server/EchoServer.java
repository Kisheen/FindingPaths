package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphics.FindingPathsGUI;

public class EchoServer implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(EchoServer.class);
	private int port = 4040;
	private Map<InetAddress, Thread> echos;
	private BlockingQueue<Object> outputQueue, inputQueue;
	private CopyOnWriteArrayList<ObjectOutputStream> clients;
	private FindingPathsGUI gui;
	Thread talker, processor;
	
	public EchoServer(FindingPathsGUI gui, int port) {
		this.gui = gui;
		this.port = port;
		echos = new HashMap<InetAddress, Thread>();
		outputQueue = new ArrayBlockingQueue<Object>(1024);
		inputQueue = new ArrayBlockingQueue<Object>(1024);
		clients = new CopyOnWriteArrayList<ObjectOutputStream>();
	}
	
	public void run() {
		try {
			logger.info("Starting up Echo Server.");
			ServerSocket server = new ServerSocket(port);
			ServerTalker talker = new ServerTalker(clients, outputQueue);
			ServerProcessor processor = new ServerProcessor(inputQueue, outputQueue, gui);
			
			this.talker = new Thread(talker);
			this.processor = new Thread(processor);
			
			this.talker.start();
			this.processor.start();
			
			while(!Thread.currentThread().isInterrupted())
			{
				try {
					Socket client = server.accept();
					ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
					out.flush();
					Thread echoServer = new Thread(new ServerListener(client, inputQueue));
					echoServer.start();
					echos.put(client.getInetAddress(), echoServer);
					clients.add(out);
				} catch(IOException e)
				{
					logger.warn("Failed connecting to client.");
					e.printStackTrace();
				}
			}
			
			shutdown();
		} catch (IOException e) {
			logger.error("Failed to initialize server.");
			e.printStackTrace();
		} catch(RuntimeException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void shutdown() {
		talker.interrupt();
		processor.interrupt();
		
		for(Thread echo : echos.values())
			echo.interrupt();
	}
}
