package stream;

import java.io.*;
import java.net.*;

public interface ClientConnectionListener {
  void onDisconnect(PrintStream socOut, String name);
  void onConnect(PrintStream socOut, String name);
}
