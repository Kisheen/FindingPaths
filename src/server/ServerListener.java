package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ServerListener implements Runnable {
	
	private static int PORT;
	private static ServerSocket SERVER;
	Logger logger = Logger.getLogger(ServerListener.class.getName());
	private Map<InetAddress, Thread> echos;
	
	public ServerListener(int port) {
		PORT = port;
		echos = new HashMap<InetAddress, Thread>();
	}
	
	public void run() {
		try {
			SERVER = new ServerSocket(PORT);
			
			while(true)
			{
				try {
					Socket client = SERVER.accept();
					Thread echoServer = new Thread(new Worker(client));
					echoServer.start();
					echos.put(client.getInetAddress(), echoServer);
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
