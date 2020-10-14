package stream;

public class Client implements Sender {
  private String name = "unnamed";
  private MulticastSocket groupSocket = null;
  private InetAddress groupAddr;
  private int groupPort;
  private SendingThread sendingThread;

  public static void main(String[] args) {
    Client client = new Client("224.125.85.13", 1234);
  }

  public Client(String groupAddressStr, int groupPort) {
    try {
      groupAddr = InetAddress.getByName(groupAddressStr);
      this.groupPort = groupPort;

      // Create a multicast socket
      groupSocket = new MulticastSocket(groupPort);
      // Join the group
      groupSocket.joinGroup(groupAddr);

        //TODO creer le thread
    } catch (Exception e) {
      System.out.println("Error in Client : " + e);
    }
  }

  @Override
  public void send(String message) {
    if (groupSocket == null) {
      System.out.println("Error in Client : send() with null groupSocket");
      return;
    }

    String formatedMessage = name + " : " + message;

    sendMessage(formatedMessage);
  }

  @Override
  public void disconnect() {
    if (groupSocket == null) {
      System.out.println(
        "Error in Client : disconnect() with null groupSocket"
      );
      return;
    }

    String formatedMessage = "." + name;

    sendMessage(formatedMessage);
  }

  private void sendMessage(String msg) {
    if (groupSocket == null) {
      System.out.println(
        "Error in Client : sendMessage() with null groupSocket"
      );
      return;
    }

    DatagramPacket packet = new DatagramPacket(
      msg.getBytes(),
      msg.length(),
      groupAddr,
      groupPort
    );

    groupSocket.send(packet);
  }

  @Override
  public void connect(String name) {
    this.name = name;

    String formatedMessage = name + " entered in the chat";

    sendMessage(formatedMessage);
  }
}
