package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientTalker implements Runnable {

	private Socket server;
	private ObjectOutputStream output;
	Logger logger = Logger.getLogger("ClientTalker");
	
	public ClientTalker(Socket server) {
		this.server = server;
		
		try {
			output = new ObjectOutputStream(server.getOutputStream());
		} catch (IOException e) {
			logger.warning("Failed to open output stream");
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			output.writeObject("Yes.");
		} catch (IOException e) {
			logger.warning("Failed to send to server.");
			e.printStackTrace();
		}
	}

}
