package Server.HTTPUtil;

import java.util.HashMap;

/**
 * Die HTTPPackage Klasse repräsentiert ein HTTP-Paket.
 * Sie enthält Informationen über die HTTP-Methode, den URI, die Version, die Header, die Query-Parameter und den Body des Pakets.
 * Die Informationen werden in HashMaps gespeichert.
 */
public class HTTPPackage {
    // Die HTTP-Methode der Anfrage (z.B. GET, POST, DELETE usw.)
    private HTTPMethod method;
    // Der URI der Anfrage
    private String uri;
    // Die HTTP-Version der Anfrage (z.B. HTTP/1.1)
    private String version;
    //
    private int statusCode;
    private String statusText;


    // Die Header der Anfrage, gespeichert als HashMap von Headernamen und -werten
    private HashMap<String, String> headers;
    // Die Query-Parameter der Anfrage, gespeichert als HashMap von Parameternamen und -werten
    private HashMap<String, String> query;

    String bodyPlain;

    String raw;

    /**
     * Der Konstruktor initialisiert die Felder der HTTPPackage-Klasse mit standardmäßigen Werten.
     */
    public HTTPPackage() {
        this.method = HTTPMethod.GET;
        this.uri = "/";
        this.version = "HTTP/1.1";
        this.statusCode = 404;
        this.statusText = "Not Found";
        this.headers = new HashMap<String, String>();
        this.query = new HashMap<String, String>();
        this.bodyPlain = "";
        this.raw = "";
    }

    public static HTTPPackage generateErrorResponse(int code, String status, String message){
        return generateErrorResponse(code, status, message, false);
    }

    public static HTTPPackage generateErrorResponse(int code, String status, String message, boolean plain){

        HTTPPackage paket = new HTTPPackage();

        if (!plain) {
            message = "{ message: '" + message + "' }";
        }

        paket.statusCode = code;
        paket.statusText = status;

        paket.addHeader("Content-Type", "text/plain;charset=utf-8");
        paket.addHeader("Content-Length", ""+message.length());
        paket.bodyPlain = message;
        return paket;
    }


    public static HTTPPackage generateBasicResponse(String message){
        return generateBasicResponse(message, false);
    }

    public static HTTPPackage generateBasicResponse(String message, boolean plain){

        HTTPPackage paket = new HTTPPackage();

        if (!plain) {
            message = "{ message: '" + message + "' }";
        }

        paket.statusCode = 200;
        paket.statusText = "OK";

        paket.addHeader("Content-Type", "text/plain;charset=utf-8");
        paket.addHeader("Content-Length", ""+message.length());
        paket.bodyPlain = message;
        return paket;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public void setMethod(HTTPMethod method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    //Functions for Header
    public void addHeader(String name, String value){
        headers.put(name, value);
    }
    public String getHeader(String name){
        return headers.get(name);
    }
    public void removeHeader(String name){
        headers.remove(name);
    }

    //Functions for Query
    public void addQuery(String name, String value){
        query.put(name, value);
    }
    public String getQuery(String name){
        return query.get(name) != null ? query.get(name) : "";
    }
    public void removeQuery(String name){
        query.remove(name);
    }


    public String getBodyPlain() {
        return bodyPlain;
    }
    public void setBodyPlain(String body) {
        this.bodyPlain = body;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public HashMap<String, String> getQuery() {
        return query;
    }

    public void setQuery(HashMap<String, String> query) {
        this.query = query;
    }


    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}
