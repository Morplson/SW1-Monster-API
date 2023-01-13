package Server.Routing;

import Server.HTTPUtil.HTTPMethod;
import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.MiddlewareRegister;
import Server.RouteWorkers.RouteWorker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Die RoutingTable Klasse verwaltet alle Routen, die vom Server unterstützt werden.
 * Sie enthält eine HashMap, in der jeder HTTP-Methode eine weitere HashMap zugeordnet ist,
 * die wiederum jeder Route einen RouteWorker zuordnet.
 * Die RoutingTable Klasse bietet Methoden zum Hinzufügen von Routen und zum Verarbeiten von HTTP-Anfragen.
 */
public class RoutingTable {
    HashMap<HTTPMethod, HashMap<String, RouteWorker>> uriList;
    MiddlewareRegister middleware;

    /**
     * Der Konstruktor initialisiert die uriList HashMap und fügt für jede unterstützte HTTP-Methode
     * eine leere HashMap hinzu.
     */
    public RoutingTable(MiddlewareRegister middleware) {
        this.uriList = new HashMap<HTTPMethod, HashMap<String, RouteWorker> >();
        uriList.put(HTTPMethod.GET,  new HashMap<String, RouteWorker>());
        uriList.put(HTTPMethod.POST, new HashMap<String, RouteWorker>());
        uriList.put(HTTPMethod.DELETE, new HashMap<String, RouteWorker>());
        uriList.put(HTTPMethod.PUT, new HashMap<String, RouteWorker>());

        this.middleware = middleware;
    }

    /**
     * Die add() Methode fügt eine neue Route für eine bestimmte HTTP-Methode hinzu.
     *
     * @param method Die HTTP-Methode, für die die Route hinzugefügt werden soll.
     * @param route  Der Pfad der Route.
     * @param worker Der RouteWorker, der für die Verarbeitung der Anfrage zuständig ist.
     */
    public void add(HTTPMethod method, String route, RouteWorker worker) {
        this.uriList.get(method).put(route, worker);
    }

    /**
     * Die addGet() Methode fügt eine neue Route für die HTTP-Methode GET hinzu.
     *
     * @param route  Der Pfad der Route.
     * @param worker Der RouteWorker, der für die Verarbeitung der Anfrage zuständig ist.
     */
    public void addGet(String route, RouteWorker worker) {
        this.add(HTTPMethod.GET, route, worker);
    }

    /**
     * Die addPost() Methode fügt eine neue Route für die HTTP-Methode POST hinzu.
     *
     * @param route  Der Pfad der Route.
     * @param worker Der RouteWorker, der für die Verarbeitung der Anfrage zuständig ist.
     */
    public void addPost(String route, RouteWorker worker) {
        this.add(HTTPMethod.POST, route, worker);
    }

    /**
     * Die addDelete() Methode fügt eine neue Route für die HTTP-Methode DELETE hinzu.
     *
     * @param route  Der Pfad der Route.
     * @param worker Der RouteWorker, der für die Verarbeitung der Anfrage zuständig ist.
     */
    public void addDelete(String route, RouteWorker worker) {
        this.add(HTTPMethod.DELETE, route, worker);
    }

    /**
     * Die addPut() Methode fügt eine neue Route für die HTTP-Methode PUT hinzu.
     *
     * @param route  Der Pfad der Route.
     * @param worker Der RouteWorker, der für die Verarbeitung der Anfrage zuständig ist.
     */
    public void addPut(String route, RouteWorker worker) {
        this.add(HTTPMethod.PUT, route, worker);
    }

    /**
     * Die process() Methode verarbeitet eine HTTP-Anfrage.
     * Sie sucht in der uriList nach der entsprechenden Route und gibt die Anfrage an den zugehörigen RouteWorker weiter.
     * Wenn keine passende Route gefunden wird, wird eine InvalidRouteException ausgelöst.
     *
     * @param request Das HTTPPackage-Objekt, das die Anfrage enthält.
     * @return Das HTTPPackage-Objekt, das die Antwort enthält.
     * @throws InvalidRouteException Wird ausgelöst, wenn keine passende Route gefunden wird.
     */
    public HTTPPackage process(HTTPPackage request) throws InvalidRouteException{
        String requestUri = request.getUri();
        HTTPMethod method = request.getMethod();

        // Der RouteWorker, der für die Verarbeitung der Anfrage zuständig ist, wird initialisiert.
        RouteWorker worker = null;

        // Ein Iterator für die Einträge der HashMap wird erstellt.
        HashMap<String, RouteWorker> targetMap = uriList.get(method);
        Iterator<Map.Entry<String, RouteWorker>> iterator = targetMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, RouteWorker> entry = iterator.next();

            // Der Schlüssel des Eintrags (der Pfad der Route) wird gelesen und in ein Pattern-Objekt kompiliert.
            String key = entry.getKey();
            Pattern p = Pattern.compile(key);

            // Ein Matcher-Objekt wird erstellt, um den Pfad der Anfrage mit dem Schlüssel zu vergleichen.
            Matcher matcher = p.matcher(requestUri);

            if (matcher.matches()) {
                // set the worker if matches :)
                worker = entry.getValue();
            }
        }


        //sends the request off to route worker
        if (worker != null) {
            return worker.process(request, this.middleware);
        }

        throw new InvalidRouteException();
    }

}
