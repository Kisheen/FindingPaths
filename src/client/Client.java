package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class Client {
	private Socket server;
	Logger logger = Logger.getLogger("Client");
	
	public Client(String host, int port){
		try {
			server = new Socket(host, port);
		} catch (UnknownHostException e) {
			logger.warning("Invalid host.");
			e.printStackTrace();
		} catch (IOException e) {
			logger.warning("Failed to connect to server.");
			e.printStackTrace();
		}
		
		Thread talker = new Thread(new ClientTalker(server));
		talker.start();
	}
}
