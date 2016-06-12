package protocol.reactor_protocols;

import protocol.AsyncServerProtocol;
import protocol.ProtocolCallback;

/**
 * a simple implementation of the server protocol interface
 */
public class EchoProtocol implements AsyncServerProtocol<String> {

	private boolean _shouldClose = false;
	private boolean _connectionTerminated = false;

	/**
	 * processes a message<BR>
	 * this simple interface prints the message to the screen, then composes a simple
	 * reply and sends it back to the client
	 *
	 * @param msg the message to process
	 * @return the reply that should be sent to the client, or null if no reply needed
	 */
	@Override
	public void processMessage(String msg, ProtocolCallback<String> callback) {        
		if (this._connectionTerminated) {
			return;
		}
		if (this.isEnd(msg)) {
			this._shouldClose = true;
			return;
		}
		return;
	}

	/**
	 * detetmine whether the given message is the termination message
	 *
	 * @param msg the message to examine
	 * @return false - this simple protocol doesn't allow termination...
	 */
	@Override
	public boolean isEnd(String msg) {
		return msg.equals("QUIT");
	}

	/**
	 * Is the protocol in a closing state?.
	 * When a protocol is in a closing state, it's handler should write out all pending data, 
	 * and close the connection.
	 * @return true if the protocol is in closing state.
	 */
	@Override
	public boolean shouldClose() {
		return this._shouldClose;
	}

	/**
	 * Indicate to the protocol that the client disconnected.
	 */
	@Override
	public void connectionTerminated() {
		this._connectionTerminated = true;
	}

}
