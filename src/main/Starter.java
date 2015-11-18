package main;

import client.Client;
import server.ServerListener;

public class Starter {
	public static void main(String args[]){
		Thread testServer = new Thread(new ServerListener(4040));
		testServer.start();
		
		Client testClient = new Client("localhost", 4040);
	}
}
