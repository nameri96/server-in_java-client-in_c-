package protocol;
/**
 * An interface that bridges between the protocol and the server .
 * The server should implement this interface , and pass an instance of it
 * along with any message .
 * The instance passed should be unique to each TCP connection .
 * It therefore allows the protocol to identify the sending user between
 * messages , and reply at any point in time .
 *
 * @param <T> type of message that the protocol handles .
 */
public interface ProtocolCallback <T > {
	/**
	 * @param msg message to be sent
	 * @throws IOException if the message could not be sent , or if the
	 * connection to this client has been closed .
	 */
	void sendMessage ( T msg ) throws java . io . IOException;
}