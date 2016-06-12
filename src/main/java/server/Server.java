package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import protocol.ServerProtocol;
import connectionHandler.ConnectionHandler;
import connectionHandler.ThreadPerClientConnectionHandler;

public class Server implements Runnable
{
	ServerSocket echoServerSocket;
	Socket clientSocket;
	int listenPort;
	ServerProtocol<String> protocol;
	
	
	public Server(int port, ServerProtocol<String> p)
	{
		echoServerSocket = null;
		clientSocket = null;
		listenPort = port;
		protocol = p;
		
	}


	@Override
	public void run()
	{
		try
		{
			echoServerSocket = new ServerSocket(listenPort);
			while(true)
			{
				clientSocket = echoServerSocket.accept();
				System.out.println("Accepted connection from client!");
				System.out.println("The client is from: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
				ConnectionHandler CH = new ThreadPerClientConnectionHandler(clientSocket, this.protocol);
				(new Thread(CH)).start();
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}