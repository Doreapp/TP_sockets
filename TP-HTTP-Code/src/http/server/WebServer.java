///A Simple Web Server (WebServer.java)

package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 *
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 *
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {
  private PrintWriter out;
  private static final String FILES_ROOT = "../";

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 3000");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      e.printStackTrace();
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(
          new InputStreamReader(remote.getInputStream())
        );
        out = new PrintWriter(remote.getOutputStream());

        String str = ".";
        String request = "";
        while (str != null && !str.equals("")) {
          str = in.readLine();
          request += str + "\n";
        }
        handleRequest(request);

        out.flush();
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
        e.printStackTrace();
      }
    }
  }

  /**
   * Start the application.
   *
   * @param args Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }

  public void handleRequest(String request) {
    log("REQUEST : " + request + "\n\n");
    if (request == null || request.trim().equals("null")) return;

    int delimitation = request.indexOf("\n\n");
    String head;
    String body;
    if (delimitation > 0) {
      head = request.substring(0, delimitation);
      body = request.substring(delimitation).trim();
    } else {
      head = request;
      body = "";
    }

    Map<String, String> headValues = readHead(head);
    readBody(body);

    System.out.println("--- start HEAD MAP --- ");
    for (String key : headValues.keySet()) {
      System.out.println(key + " : " + headValues.get(key));
    }
    System.out.println("--- end HEAD MAP --- \n");

    switch (headValues.get("Method")) {
      case "GET":
        handleGetRequest(headValues, request.substring(4));
        break;
      case "POST":
        handlePostRequest(headValues, request.substring(4));
        break;
      case "HEAD":
        handleHeadRequest(headValues, request.substring(4));
        break;
      case "PUT":
        handlePutRequest(headValues, request.substring(4));
        break;
      case "DELETE":
        handleDeleteRequest(headValues, request.substring(4));
        break;
      default:
        System.out.println("unhandled type of request : " + request);
    }
  }

  public void handleGetRequest(Map<String, String> headValues, String request) {
    log("handleGet");
    //System.out.println("handle GET request : " + request);
    String url = headValues.get("Url");
    File file = getFile(url);
    if (file == null) {
      sendFileNotFound();
    } else {
      String content = readFile(file);
      if (content == null) {
        sendFileNotFound();
      } else {
        printHeader(out, "200 OK", "text/html");
        out.println(content);
      }
    }
  }

  private void sendFileNotFound() {
    printHeader(out, "404", null);
    System.out.println("file not found");
  }

  public void handlePostRequest(
    Map<String, String> headValues,
    String request
  ) {
    log("handle post");
   

    String url = headValues.get("Url");
    File file = getFile(url);
    if (file == null) {
      sendFileNotFound();
    } else {
      String content = readFile(file);
      if (content == null) {
        sendFileNotFound();
      } else {
        printHeader(out, "200 OK", "text/html");
      }
    }
  }

  public void handleHeadRequest(
    Map<String, String> headValues,
    String request
  ) {
    System.out.println("handle HEAD request : " + request);
  }

  public void handlePutRequest(Map<String, String> headValues, String request) {
    System.out.println("handle PUT request : " + request);
  }

  public void handleDeleteRequest(
    Map<String, String> headValues,
    String request
  ) {
    System.out.println("handle DELETE request : " + request);
  }

  public static void printHeader(
    PrintWriter out,
    String code,
    String contentType
  ) {
    out.println("HTTP/1.0 " + code);
    if (contentType != null) out.println("Content-Type: " + contentType);
    out.println("Server: Bot");
    out.println("");
  }

  public static Map<String, String> readHead(String head) {
    Map<String, String> res = new HashMap<>();
    int endLine = head.indexOf("\n");
    String firstLine = head.substring(0, endLine);
    String[] firstLineValues = firstLine.split(" ");
    res.put("Method", firstLineValues[0].trim());
    res.put("Url", firstLineValues[1].trim());
    res.put("Http-Version", firstLineValues[2].trim());
    String[] lines = head.split("\n");
    for (String line : lines) {
      String[] values = line.split(": ");
      if (values.length == 2) res.put(values[0], values[1]);
    }
    return res;
  }

  public static File getFile(String url) {
    String formatedUrl = FILES_ROOT + url;
    File file = new File(formatedUrl);

    System.out.println("GET request url : " + formatedUrl);
    if (!file.exists()) {
      return null;
    } else {
      return file;
    }
  }

  public static String readFile(File file) {
    try {
      String res = "";
      Scanner myReader = new Scanner(file);
      while (myReader.hasNextLine()) {
        res += myReader.nextLine() + "\n";
      }
      myReader.close();
      return res;
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  public static String readBody(String body) {
    log("read body : " + body);
    return body;
  }

  public static void log(String msg) {
    System.out.println("[LOG] " + msg);
  }
}
