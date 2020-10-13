package stream;

import java.net.*;
import java.io.*;

public interface ClientConnectionListener {
  void onDisconnect(PrintStream socOut, String name);
  void onConnect(PrintStream socOut, String name);
}
