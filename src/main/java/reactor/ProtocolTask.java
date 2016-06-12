package reactor;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import protocol.ServerProtocol;
import tokenizer.*;

/**
 * This class supplies some data to the protocol, which then processes the data,
 * possibly returning a reply. This class is implemented as an executor task.
 * 
 */
public class ProtocolTask implements Runnable {

	private final ServerProtocol<String> _protocol;
	private final MessageTokenizer<String> _tokenizer;
	private final ConnectionHandler<String> _handler;

	public ProtocolTask(final ServerProtocol<String> protocol, final MessageTokenizer<String> tokenizer, final ConnectionHandler<String> h) {
		this._protocol = protocol;
		this._tokenizer = tokenizer;
		this._handler = h;
	}

	// we synchronize on ourselves, in case we are executed by several threads
	// from the thread pool.
	public synchronized void run() {
		// go over all complete messages and process them.
		while (_tokenizer.hasMessage()) {
			String msg = _tokenizer.nextMessage();
			msg= _handler._sChannel.socket().getInetAddress()+":"+_handler._sChannel.socket().getPort()+"###"+msg;
			this._protocol.processMessage(msg, response ->{
				if (response != null) {
					try {
						ByteBuffer bytes = _tokenizer.getBytesForMessage(response);
						this._handler.addOutData(bytes);
					} catch (CharacterCodingException e) 
					{ e.printStackTrace(); }
				}
			});


		}
	}

	public void addBytes(ByteBuffer b) {
		_tokenizer.addBytes(b);
	}
}
