package http;

import java.io.*;
import java.util.*;
import http.server.WebServer;

/**
 * Classe représentant une requête HTTP
 */
public class HttpRequest {
  public static final String METHOD = "Method";
  public static final String URL = "Url";
  public static final String HTTP_VERSION = "Http-Version";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String CONTENT_LENGTH = "Content-Length";
  private Map<String, String> valuesMap;
  private String body = null;

  /**
   * Enumération des méthodes HTTP (implémentées uniquement)
   */
  public enum Method {
    UNKNOWN,
    GET,
    POST,
    PUT,
    HEAD,
    DELETE,
  }

  /**
   * Lit l'input fourni et retourne une requête HTTP correspondante
   * @param inputStream stream à lire (contenant la requête)
   * @return la requête construite ou null si inputStream vide
   */
  public static HttpRequest read(InputStream inputStream) throws IOException {
    WebServer.log("Request.read");
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

    // Read header :
    String str = ".";
    String head = "";
    while (str != null && !str.equals("")) {
      WebServer.log("While str != null");
      str = in.readLine();
      head += str + "\n";
    }
    if (head == null || head.trim().isEmpty() || head.trim().equals("null")) {
      WebServer.log("header null, stop reading");
      return null;
    }

    HttpRequest result = new HttpRequest(head);
    Long contentLength = result.getContentLength();
    if (contentLength != null) {
      char[] buffer = new char[(int) contentLength.longValue()];
      in.read(buffer, 0, (int) contentLength.longValue());
      String body = new String(buffer);
      result.setBody(body);
    }
    WebServer.log("Retrun Built Web Request");
    return result;
  }

  /**
   * Constructeur privé
   */
  private HttpRequest(String header) {
    valuesMap = readHeader(header);
  }

  private void setBody(String body) {
    this.body = body;
  }

  public String getBody() {
    return body;
  }

  public String getUrl() {
    return get(URL);
  }

  public String getContentType() {
    return get(CONTENT_TYPE);
  }

  public Long getContentLength() {
    String contentLengthStr = get(CONTENT_LENGTH);
    if (contentLengthStr == null) {
      return null;
    }
    return Long.parseLong(contentLengthStr);
  }

  /**
   * Permet d'obtenir une information de la requête
   * @param key clé / nom de l'information voulu (ex Content-Type)
   * @return la valeur si elle se trouve dans la requête, sinon null
   */
  public String get(String key) {
    return valuesMap.get(key);
  }

  /**
   * Utilisé pour obtenir la méthode de la requête
   * @return la méthode si implémentée, sinon Method.UNKNOWN
   */
  public Method getMethod() {
    String methodStr = valuesMap.get(METHOD);
    if (methodStr == null) return Method.UNKNOWN;
    try {
      return Method.valueOf(methodStr);
    } catch (IllegalArgumentException e) {
      return Method.UNKNOWN;
    }
  }

  /**
   * Lit le header et retourne les informations lues dans une map
   * @param head header de la requête
   * @return map des informations lues
   */
  private static Map<String, String> readHeader(String head) {
    Map<String, String> res = new HashMap<>();
    int endLine = head.indexOf("\n");
    String firstLine = head.substring(0, endLine);
    String[] firstLineValues = firstLine.split(" ");
    res.put("Method", firstLineValues[0].trim());
    String url = firstLineValues[1].trim().replaceAll("%20"," ");
    int markIndex;
    if((markIndex = url.indexOf('?')) == -1){
      res.put("Url", url);
    }else{
      res.put("Url", url.substring(0,markIndex));
      res.put("params",url.substring(markIndex+1));
    }
    res.put("Http-Version", firstLineValues[2].trim());
    String[] lines = head.split("\n");
    for (String line : lines) {
      String[] values = line.split(": ");
      if (values.length == 2) res.put(values[0], values[1]);
    }
    return res;
  }

  /**
   * Construit un texte décrivant la requête
   * @return un String sur une ligne
   */
  @java.lang.Override
  public java.lang.String toString() {
    String header = "";
    for (String key : valuesMap.keySet()) {
      header += key + "='" + valuesMap.get(key) + "', ";
    }
    return "HttpRequest{" + header + "body='" + body + '\'' + '}';
  }
}
