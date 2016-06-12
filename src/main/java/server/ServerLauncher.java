package server;

import protocol.TBGP;
import reactor.Reactor;

public class ServerLauncher
{
	private static final int PORT = 8080;
	public static void main(String[] args)
	{
//		Server server = new Server(PORT, new TBGP());
//		Thread t = new Thread(server);
//		t.start();
		
        try {
//            int port = Integer.parseInt(args[0]);
//            int poolSize = Integer.parseInt(args[1]);

        	int port = 8080;
        	int poolSize = 10;
            Reactor<String> reactor = Reactor.startEchoServer(port, poolSize);

            Thread thread = new Thread(reactor);
            thread.start();
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
