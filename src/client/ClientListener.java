package client;

import java.net.Socket;

public class ClientListener implements Runnable {
	
	private Socket server;
	
	public ClientListener(Socket server) {
		this.server = server;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
