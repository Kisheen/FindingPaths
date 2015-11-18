package main;

import client.Client;
import server.EchoServer;

public class Starter {
	public static void main(String args[]){
		Thread testServer = new Thread(new EchoServer());
		testServer.start();
		
		Client testClient = new Client("localhost", 4040);
	}
}
