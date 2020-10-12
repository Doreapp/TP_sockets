package stream;

import java.net.*;

public interface ClientDeconnection {
  void disconnect(Socket soc, String name);
}
