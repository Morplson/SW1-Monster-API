package Server.RouteWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.AccessData;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;

import java.util.HashMap;


public class ShowSessionsRouteWorker implements RouteWorker {

    @Override
    public HTTPPackage process(HTTPPackage request, MiddlewareRegister mw) {
        Database db = (Database) mw.get("db");
        SessionManager sm = (SessionManager) mw.get("sm");

        String token = request.getHeader("Authorization");

        if(!sm.hasAccess(token, "sessions.*")){
            return HTTPPackage.generateErrorResponse(403, "Access denied", "Token "+token+" has no authority over resource sessions.*");
        }

        StringBuilder sb = new StringBuilder("Running sessions list:").append(System.lineSeparator());

        HashMap<String, AccessData> accounts = sm.getAccessData();
        for (String key : accounts.keySet()) {
            sb.append(key).append(" = ").append(accounts.get(key)).append(";").append(System.lineSeparator());
        }

        //TODO: generate a real response
        String body = sb.toString();
        return HTTPPackage.generateBasicResponse(body);
    }
}
