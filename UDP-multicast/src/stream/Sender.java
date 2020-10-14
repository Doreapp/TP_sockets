package stream;

public interface Sender {
    void send(String message);
    void disconnect();
    void connect(String name);
}