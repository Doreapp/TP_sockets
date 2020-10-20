package http;

import java.io.*;
import java.util.*;
import http.server.WebServer;

/**
 * Représente une réponse HTTP
 */
public class HttpResponse {
  private int code = Code.SC_BAD_REQUEST;
  private String httpVersion = "HTTP/1.1";
  private String contentType = null;
  private long contentLength = -1;
  private File fileToSend = null;

  /**
   * Constructeur vide
   */
  public HttpResponse() {}

  /**
   * Constructeur avec un code
   * @param code code de résultat
   */
  public HttpResponse(int code) {
    this.code = code;
  }

  /**
   * Construit une réponse HTTP avec un code Not Found (404)
   * @return La réponse HTTP
   */
  public static HttpResponse responseNotFound() {
    return new HttpResponse(Code.SC_NOT_FOUND);
  }

  // Setters

  public void setCode(int code) {
    this.code = code;
  }

  public void setHttpVersion(String httpVersion) {
    this.httpVersion = httpVersion;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public void setContentLength(long contentLength) {
    this.contentLength = contentLength;
  }

  /**
   * Construit et retourne le Header de la réponse
   * @return le header
   */
  public String getHeader() {
    String result = httpVersion + " " + code;
    if (code == Code.SC_OK) {
      result += " OK";
    }
    result += "\n";
    result += "Server: Bot\n";
    if (contentType != null) {
      result += "Content-Type: " + contentType + "\n";
    }
    if (contentLength > 0) {
      result += "Content-Length: " + contentLength + "\n";
    }

    return result + "\n";
  }

  /**
   * Trouve le content type correspondant au fichier situé à l'url entrée
   * @param url addresse du fichier
   */
  public void findContentType(String url) {
    contentType = "text/plain";
    int index = url.lastIndexOf(".");
    if (index == -1) {
      contentType = "text/html";
      return;
    }
    String extension = url.substring(index + 1);
    if (extension != null || extension.isEmpty()) {
      if (Arrays.asList(imageExt).contains(extension)) {
        contentType = "image/" + extension;
      } else if (Arrays.asList(songExt).contains(extension)) {
        contentType = "audio/mpeg"; //+ extension;
      } else if (extension.equals("html")) {
        contentType = "text/html";
      } else if (Arrays.asList(videoExt).contains(extension)) {
        contentType = "video/" + extension;
      }
    }
  }

  /**
   * Indique le fichier à envoyé en retour (pour les GET)
   * @param file fichier à transmettre
   */
  public void setFileToSend(File file) {
    this.fileToSend = file;
  }

  /**
   * Lit et transmet le fichier indiqué avec {@link setFileToSend}
   * à travert l'output stream fourni
   * @param os Buffered Output Stream utilisé pour communiquer avec le client
   */
  public void sendFile(BufferedOutputStream os) throws IOException {
    WebServer.log("Response.send");
    if (fileToSend == null) return;
    if (!fileToSend.isFile()) {
      String toSend = "";
      toSend += "<h1>Files in root</h1>\n";
      for (File child : fileToSend.listFiles()) {
        if (child.isFile()) {
          String name = child.getName();
          toSend += "<a href=\"./" + name + "\">" + name + "</a><br/>\n";
        }
      }
      os.write(toSend.getBytes());
    } else {
      BufferedInputStream is = new BufferedInputStream(
        new FileInputStream(fileToSend)
      );

      byte[] buffer = new byte[1024];
      int totalLength = 0;
      int length;
      WebServer.log("While writing start");
      while ((length = is.read(buffer)) > 0) {
        //System.out.print(new String(buffer, 0, length));
        os.write(buffer, 0, length);
        totalLength += length;
      }
      WebServer.log("While writing end");
      System.out.println(
        "HttpResponse.sendFile : " + totalLength + " bytes sent"
      );
      is.close();
    }
  }

  /**
   * Retourne une texte décrivant la réponse
   * @return un String sur une ligne
   */
  @java.lang.Override
  public java.lang.String toString() {
    return (
      "HttpResponse{" +
      "code=" +
      code +
      ", httpVersion='" +
      httpVersion +
      '\'' +
      ", contentType='" +
      contentType +
      '\'' +
      ", contentLength=" +
      contentLength +
      '}'
    );
  }

  private static final String[] imageExt = {
    "tif",
    "tiff",
    "bmp",
    "jpg",
    "jpeg",
    "gif",
    "png",
    "eps",
  };
  private static final String[] songExt = {
    "mpeg",
    "3gp",
    "aa",
    "aac",
    "aax",
    "act",
    "aiff",
    "alac",
    "amr",
    "ape",
    "au",
    "awb",
    "dct",
    "dss",
    "dvf",
    "flac",
    "gsm",
    "iklax",
    "ivs",
    "m4a",
    "m4b",
    "m4p",
    "mmf",
    "mp3",
    "mpc",
    "msv",
    "nmf",
    "ogg",
    "oga",
    "mogg",
    "opus",
    "ra",
    "rm",
    "raw",
    "rf64",
    "sln",
    "tta",
    "voc",
    "vox",
    "wav",
    "wma",
    "wv",
    "webm",
    "8svx",
    "cda",
  };
  private static final String[] videoExt = { "mp4", "mpeg" };

  /**
   * Classe statique contenant les codes HTTP
   */
  public static class Code {
    public static int SC_ACCEPTED = 202;
    public static int SC_BAD_GATEWAY = 502;
    public static int SC_BAD_REQUEST = 400;
    public static int SC_CONFLICT = 409;
    public static int SC_CONTINUE = 100;
    public static int SC_CREATED = 201;
    public static int SC_EXPECTATION_FAILED = 417;
    public static int SC_FAILED_DEPENDENCY = 424;
    public static int SC_FORBIDDEN = 403;
    public static int SC_GATEWAY_TIMEOUT = 504;
    public static int SC_GONE = 410;
    public static int SC_HTTP_VERSION_NOT_SUPPORTED = 505;
    public static int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419;
    public static int SC_INSUFFICIENT_STORAGE = 507;
    public static int SC_INTERNAL_SERVER_ERROR = 500;
    public static int SC_LENGTH_REQUIRED = 411;
    public static int SC_LOCKED = 423;
    public static int SC_METHOD_FAILURE = 420;
    public static int SC_METHOD_NOT_ALLOWED = 405;
    public static int SC_MOVED_PERMANENTLY = 301;
    public static int SC_MOVED_TEMPORARILY = 302;
    public static int SC_MULTI_STATUS = 207;
    public static int SC_MULTIPLE_CHOICES = 300;
    public static int SC_NO_CONTENT = 204;
    public static int SC_NON_AUTHORITATIVE_INFORMATION = 203;
    public static int SC_NOT_ACCEPTABLE = 406;
    public static int SC_NOT_FOUND = 404;
    public static int SC_NOT_IMPLEMENTED = 501;
    public static int SC_NOT_MODIFIED = 304;
    public static int SC_OK = 200;
    public static int SC_PARTIAL_CONTENT = 206;
    public static int SC_PAYMENT_REQUIRED = 402;
    public static int SC_PRECONDITION_FAILED = 412;
    public static int SC_PROCESSING = 102;
    public static int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
    public static int SC_REQUEST_TIMEOUT = 408;
    public static int SC_REQUEST_TOO_LONG = 413;
    public static int SC_REQUEST_URI_TOO_LONG = 414;
    public static int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    public static int SC_RESET_CONTENT = 205;
    public static int SC_SEE_OTHER = 303;
    public static int SC_SERVICE_UNAVAILABLE = 503;
    public static int SC_SWITCHING_PROTOCOLS = 101;
    public static int SC_TEMPORARY_REDIRECT = 307;
    public static int SC_UNAUTHORIZED = 401;
    public static int SC_UNPROCESSABLE_ENTITY = 422;
    public static int SC_UNSUPPORTED_MEDIA_TYPE = 415;
    public static int SC_USE_PROXY = 305;
  }
}
