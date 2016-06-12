package connectionHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import protocol.ServerProtocol;

public class ThreadPerClientConnectionHandler implements ConnectionHandler
{
	Socket clientSocket;
	ServerProtocol<String> protocol;
	private BufferedReader in;
	private PrintWriter out;
	public ThreadPerClientConnectionHandler(Socket clientSocket, ServerProtocol<String> protocol)
	{
		this.clientSocket = clientSocket;
		this.protocol = protocol;
		try
		{
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
			out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"), true);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("I/O initialized");
	}

	@Override
	public void run()
	{
		String msg;
		try
		{
			while ((msg = in.readLine()) != null)
			{
				//System.out.println("Received \"" + msg + "\" from client");
				
				protocol.processMessage(clientSocket.getInetAddress()+":"+clientSocket.getPort()+"###"+msg, result ->{
					out.println(result );
				});
				
				if (protocol.isEnd(msg))
				{
					break;
				}
				
			}
			System.out.println(clientSocket.getInetAddress()+":"+clientSocket.getPort()+" Disconnected...");
			
			// Close all I/O
			out.close();
			in.close();
			clientSocket.close();
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
