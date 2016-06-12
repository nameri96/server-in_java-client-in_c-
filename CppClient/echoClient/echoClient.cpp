#include <stdlib.h>
#include <boost/locale.hpp>
#include "connectionHandler.h"
#include "../encoder/utf8.h"
#include "../encoder/encoder.h"
#include <thread>
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/

ConnectionHandler connectionHandler;
bool running = true;
void *ListenInput()
{
   while (running) {
     std::string answer;
     int len;
        if (!connectionHandler.getLine(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
	    running = false;
            break;
        }
        
		len=answer.length();
		// A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
		// we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
        answer.resize(len-1);
        std::cout <<  answer << " "  <<  std::endl << std::endl;
        
   }
connectionHandler.close();
return NULL;
  
}



void *SendOutput()
{
   while (running) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
		std::string line(buf);
        if (!connectionHandler.sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
	    running = false;
            break;
        }
        if (line == "QUIT") {
            std::cout << "Exiting...\n" << std::endl;
	    running = false;
            break;
        }
   }
return NULL;
}





int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    
    if (!connectionHandler.connect(host, port)) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
	
	 std::thread t1(SendOutput);
	 std::thread t2(ListenInput);
	 t1.join();
	 t2.join();
    
    return 0;
}
