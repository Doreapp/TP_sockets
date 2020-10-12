package stream;

import java.io.IOException;
import java.net.Socket;

public interface Handler {

    void handle(String message) throws IOException;
} 