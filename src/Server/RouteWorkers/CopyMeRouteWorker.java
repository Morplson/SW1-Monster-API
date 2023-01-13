package Server.RouteWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;


public class CopyMeRouteWorker implements RouteWorker {

    @Override
    public HTTPPackage process(HTTPPackage request, MiddlewareRegister mw) {
        Database db = (Database) mw.get("db");
        SessionManager sm = (SessionManager) mw.get("sm");

        //TODO: do all the crating here

        //TODO: generate a real response
        String body = "response";
        return HTTPPackage.generateBasicResponse(body);
    }
}
