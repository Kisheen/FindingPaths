package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientListener implements Runnable {
	
	private Socket server;
	private ObjectInputStream input;
	Logger logger = Logger.getLogger("Client listener.");
	
	public ClientListener(Socket server) {
		this.server = server;
		
		try {
			input = new ObjectInputStream(server.getInputStream());
		} catch (IOException e) {
			logger.warning("Failed to open input stream");
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true){
			try {
				Object in = input.readObject();
				logger.info("Read from server " + in);
			} catch (ClassNotFoundException e) {
				logger.warning("Failed to read object.");
				e.printStackTrace();
			} catch (IOException e) {
				logger.warning("Failed to receive object.");
				e.printStackTrace();
			}
		}
	}

}
