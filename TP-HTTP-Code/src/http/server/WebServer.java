package http.server;

import http.HttpRequest;
import http.HttpResponse;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;

/**
 * Web Server gérant des requêtes HTTP
 */
public class WebServer {
  private static final String FILES_ROOT = "../doc/";
  private static final int PORT = 3000;

  /**
   * Lance le Web server
   */
  protected void start() {
    log("start");
    ServerSocket s;

    System.out.println("Webserver starting up on port " + PORT);
    System.out.println("(press ctrl-c to exit)");
    try {
      s = new ServerSocket(PORT);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      e.printStackTrace();
      return;
    }

    log("** Waiting for connection **");
    for (;;) {
      log("for enter");
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        log("Connection found");

        BufferedOutputStream out = new BufferedOutputStream(
          remote.getOutputStream()
        );

        HttpRequest request = HttpRequest.read(remote.getInputStream());
        if (request != null) {
          log("Request : " + request);
          HttpResponse response = handleRequest(request);
          if (response != null) {
            log("Response : " + response);
            String toSend = response.getHeader();

            out.write(toSend.getBytes());

            try {
              response.sendFile(out);
            } catch (IOException e) {
              log("Exception thrown while sending file : " + e.getMessage());
              try {
                HttpResponse responseError = new HttpResponse(
                  HttpResponse.Code.SC_INTERNAL_SERVER_ERROR
                );
                out.write(responseError.getHeader().getBytes());
              } catch (IOException e2) {}
            }
            try {
              out.flush();
            } catch (IOException e) {
              log("Exception thrown while flushing : " + e.getMessage());
            }
          }
        } else {
          continue;
        }

        remote.close();
      } catch (Exception e) {
        log("Main Error: " + e);
        e.printStackTrace();
      }
    }
  }

  /**
   * Commence l'application
   *
   * @param args paramètres de ligne de commande. Inutiles ici.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }

  /**
   * Interprete une requête quelconque
   * @param request Requête à interpréter
   * @return La réponse lié à la requête (peut être null)
   */
  public HttpResponse handleRequest(HttpRequest request) {
    switch (request.getMethod()) {
      case GET:
        return handleGetRequest(request);
      case POST:
        return handlePostRequest(request);
      case HEAD:
        return handleHeadRequest(request);
      case PUT:
        return handlePutRequest(request);
      case DELETE:
        return handleDeleteRequest(request);
      case UNKNOWN:
      default:
        log("unhandled type of request : " + request);
        return null;
    }
  }

  /**
   * Interprete une requête GET : retourne un fichier
   * @param request Requête à interpréter
   * @return La réponse lié à la requête (peut être null)
   */
  public HttpResponse handleGetRequest(HttpRequest request) {
    File file = getFile(request.getUrl());
    if (file == null || !file.exists()) {
      return HttpResponse.responseNotFound();
    } else {
      HttpResponse response = new HttpResponse(HttpResponse.Code.SC_OK);
      response.findContentType(request.getUrl());
      if ("text/json".equals(response.getContentType())) {
        int exitValue = 1;
        String stdout = "";
        String stderr = "";
        try {
          Runtime runtime = Runtime.getRuntime();
          String[] command;
          String param = request.get("params");
          if (param != null && param.length() > 0) {
            String[] params = param.split("&");
            command = new String[params.length + 1];
            for (int i = 0; i < params.length; i++) {
              command[i + 1] = params[i].substring(params[i].indexOf("=") + 1);
            }
          } else {
            command = new String[1];
          }
          command[0] = FILES_ROOT + request.getUrl();
          Process process = runtime.exec(command);
          stdout =
            new BufferedReader(
              new InputStreamReader(
                process.getInputStream(),
                StandardCharsets.UTF_8
              )
            )
              .lines()
              .collect(Collectors.joining("\n"));
          stderr =
            new BufferedReader(
              new InputStreamReader(
                process.getErrorStream(),
                StandardCharsets.UTF_8
              )
            )
              .lines()
              .collect(Collectors.joining("\n"));
          exitValue = process.waitFor();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        String answer =
          "{\n  \"exitValue\":\"" +
          exitValue +
          "\",\n  \"stdout\":\"" +
          stdout +
          "\",\n  \"stderr\":\"" +
          stderr +
          "\"\n}";
        response.setContentLength(answer.length());
        response.setStringToSend(answer);
      } else {
        response.findContentType(request.getUrl());
        if (file.isFile()) {
          response.setContentLength(file.length());
        }
        response.setFileToSend(file);
      }
      return response;
    }
  }

  /**
   * Interprete une requête POST : ajoute des informations
   * @param request Requête à interpréter
   * @return La réponse lié à la requête (peut être null)
   */
  public HttpResponse handlePostRequest(HttpRequest request) {
    File file = getFile(request.getUrl());
    if (file.exists() && !file.isFile()) {
      return new HttpResponse(HttpResponse.Code.SC_BAD_REQUEST);
    } else {
      boolean fileExisted = file.exists();
      boolean ok = writeToFile(file, request.getBody(), true);
      if (!ok) {
        return new HttpResponse(HttpResponse.Code.SC_NOT_MODIFIED);
      } else if (fileExisted) {
        return new HttpResponse(HttpResponse.Code.SC_OK);
      } else {
        return new HttpResponse(HttpResponse.Code.SC_CREATED);
      }
    }
  }

  /**
   * Interprete une requête HEAD : donne les informations sur un fichier
   * @param request Requête à interpréter
   * @return La réponse lié à la requête (peut être null)
   */
  public HttpResponse handleHeadRequest(HttpRequest request) {
    //Same as GET without printing body
    File file = getFile(request.getUrl());
    if (file == null || !file.exists() || !file.isFile()) {
      return HttpResponse.responseNotFound();
    } else {
      HttpResponse response = new HttpResponse(HttpResponse.Code.SC_OK);
      response.findContentType(request.getUrl());
      if (file.isFile()) {
        response.setContentLength(file.length());
      }
      response.setFileToSend(null); // don't send the file into a head request
      return response;
    }
  }

  /**
   * Interprete une requête PUT : modifie (écrase) des informations
   * @param request Requête à interpréter
   * @return La réponse lié à la requête (peut être null)
   */
  public HttpResponse handlePutRequest(HttpRequest request) {
    // Same as post without appending

    File file = getFile(request.getUrl());
    if (file.exists()) {
      return new HttpResponse(HttpResponse.Code.SC_BAD_REQUEST);
    } else if (!file.isFile()) {
      return new HttpResponse(HttpResponse.Code.SC_FORBIDDEN);
    } else {
      boolean fileExisted = file.exists();
      boolean ok = writeToFile(file, request.getBody(), false);
      if (!ok) {
        return new HttpResponse(HttpResponse.Code.SC_METHOD_NOT_ALLOWED);
      } else if (fileExisted) {
        return new HttpResponse(HttpResponse.Code.SC_OK);
      } else {
        return new HttpResponse(HttpResponse.Code.SC_CREATED);
      }
    }
  }

  /**
   * Interprete une requête DELETE : supprime un fichier
   * @param request Requête à interpréter
   * @return La réponse lié à la requête (peut être null)
   */
  public HttpResponse handleDeleteRequest(HttpRequest request) {
    File file = getFile(request.getUrl());
    if (file == null || !file.exists()) {
      return HttpResponse.responseNotFound();
    } else if (!file.isFile()) {
      return new HttpResponse(HttpResponse.Code.SC_FORBIDDEN);
    }else {
      if (file.delete()) {
        return new HttpResponse(HttpResponse.Code.SC_OK);
      } else {
        return new HttpResponse(HttpResponse.Code.SC_METHOD_NOT_ALLOWED);
      }
    }
  }

  /**
   * Retrouve un fichier via une url
   * @param url url, entrée, du fichier recherché
   * @return le fichier, jamais null
   */
  public static File getFile(String url) {
    String formatedUrl = FILES_ROOT + url;

    return new File(formatedUrl);
  }

  /**
   * Ecrit dans un fichier
   * @param file Fichier dans lequel écrire
   * @param value texte à écrire dans le fichier
   * @param append on ajoute le texte à la fin ou on écrase tout (true pour ajouter)
   * @return si le fichier à bien été modifier
   */
  public static boolean writeToFile(File file, String value, boolean append) {
    try (
      FileWriter fw = new FileWriter(file, append);
      BufferedWriter bw = new BufferedWriter(fw);
      PrintWriter out = new PrintWriter(bw)
    ) {
      out.println(value);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Ecrit un message sur la console (précédé de [LOG])
   * A utiliser pour comprendre le comportement du server
   * @param msg message à logger
   */
  public static void log(String msg) {
    System.out.println("[LOG] " + msg);
  }
}
