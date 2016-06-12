package protocol.reactor_protocols;
import protocol.*;
public interface ServerProtocolFactory<T> {
   AsyncServerProtocol<T> create();
}
