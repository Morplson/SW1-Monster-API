package Server.HTTPUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class HTTPParser {
    public static HTTPPackage parse(BufferedReader buffread) throws IOException {
        HTTPPackage parsedPackageContainer = new HTTPPackage();

        StringBuilder received = new StringBuilder();
        String line;

        String[] requestLineElements  = null;
        String httpVersion = null;
        String completeUrl = null;
        String uri = null;
        HTTPMethod method = null;

        //Die erste Zeile des Requests (enthält Methode, URI und Version) wird gelesen.
        line = buffread.readLine();
        if (line != null && !line.isEmpty()) {
            requestLineElements = line.split(" ");
            completeUrl = requestLineElements [1];
            method = HTTPMethod.valueOf(requestLineElements [0]);
            httpVersion = requestLineElements [2];

            parsedPackageContainer.setVersion(httpVersion);
            parsedPackageContainer.setMethod(method);

            received.append(line);
        }

        // Die Query-Parameter werden aus dem URI des Requests extrahiert und dem Query-HashMap des HTTPPackages hinzugefügt.
        String[] splitForQueri = completeUrl.split("\\?");
        uri = splitForQueri[0];
        System.out.println(uri);
        if (splitForQueri.length > 1) {

            //Sets Query-Parameter if there is a query string
            String queryString = splitForQueri[1];
            System.out.println(queryString);
            String[] queryElements = queryString.split("\\&");
            for (String queryElement : queryElements) {
                System.out.println(queryElement);
                String[] parameter = queryElement.split("\\=");
                parsedPackageContainer.addQuery(parameter[0], parameter[1]);
            }
        }
        //Sets URI if there is no query string
        parsedPackageContainer.setUri(uri);


        // These next 6 lines get the text of a httprequest by first getting a line, then
        line = buffread.readLine();
        while (line != null && !line.isEmpty()) {

            int colonIndex = line.indexOf(":");
            String fieldName = line.substring(0, colonIndex);
            String fieldValue = line.substring(colonIndex + 1).trim();
            // Der Feldname und der Wert werden dem Header-HashMap des HTTPPackage-Objekts hinzugefügt.
            parsedPackageContainer.addHeader(fieldName, fieldValue);

            //Add to verbose server output.
            received.append(line);
            received.append(System.lineSeparator());

            line = buffread.readLine();
        }

        int bodyLength = 0;
        try{
            bodyLength = Integer.parseInt(
                    parsedPackageContainer.getHeader(
                            "Content-Length"
                    )
            );
        } catch (NumberFormatException nfe) {
            System.out.println("couldn't find Content-Length going with 0");
        }

        if (bodyLength > 0) {
            char[] charBuffer = new char[bodyLength];
            buffread.read(charBuffer, 0, bodyLength);
            String bodyString = new String(charBuffer);

            parsedPackageContainer.setBodyPlain(String.valueOf(charBuffer));

            received.append(charBuffer);
            received.append(System.lineSeparator());
        }

        parsedPackageContainer.setRaw(received.toString());

        return parsedPackageContainer;
    }



    public static String build(HTTPPackage httpPackage) {
        // Der StringBuilder wird erstellt, um die Antwort zu erstellen.
        StringBuilder responseString = new StringBuilder();

        // Die Statuszeile der Antwort wird hinzugefügt.
        responseString.append(httpPackage.getVersion());
        responseString.append(" ");
        responseString.append(httpPackage.getStatusCode());
        responseString.append(" ");
        responseString.append(httpPackage.getStatusText());
        responseString.append(System.lineSeparator());


        // Die Header-Felder der Antwort werden hinzugefügt.
        HashMap<String, String> headers = httpPackage.getHeaders();
        for (String headerName : headers.keySet()) {
            responseString.append(headerName);
            responseString.append(": ");
            responseString.append(headers.get(headerName));
            responseString.append(System.lineSeparator());
        }

        // Eine leere Zeile wird hinzugefügt, um die Header-Felder von der Antwort-Nachricht zu trennen.
        responseString.append(System.lineSeparator());

        // Der Body der Antwort wird hinzugefügt.
        responseString.append(httpPackage.getBodyPlain());

        // Die Antwort-Zeichenfolge wird zurückgegeben.
        return responseString.toString();  // Der StringBuilder wird erstellt, um die Antwort zu erstellen.
    }
}
