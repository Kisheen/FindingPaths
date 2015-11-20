package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphics.FindingPathsGUI;

public class Client {
	private Socket server;
	private Thread talker, listener;
	private BlockingQueue<Object> output;
	private FindingPathsGUI gui;
	
	Logger logger = LoggerFactory.getLogger("Client");
	
	public Client(String host, int port, FindingPathsGUI gui) throws UnknownHostException, IOException{
		this.gui = gui;
		server = new Socket(host, port);
		output = new ArrayBlockingQueue<Object>(1024);
		
		talker = new Thread(new ClientTalker(server, output));
		talker.start();
		
		listener = new Thread(new ClientListener(server, gui));
		listener.start();
	}
	
	public void shutdown() {
		gui.addText("Disconnecting from server.");
		talker.interrupt();
		listener.interrupt();
	}
	
	public boolean send(Object packet) {
		try {
			output.put(packet);
		} catch (InterruptedException e) {
			logger.warn("Failed to send to server.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
