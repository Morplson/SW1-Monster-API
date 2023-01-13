package Server.RouteWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.User;

import java.util.HashMap;


public class ShowUserRouteWorker implements RouteWorker {

    @Override
    public HTTPPackage process(HTTPPackage request, MiddlewareRegister mw) {
        Database db = (Database) mw.get("db");
        SessionManager sm = (SessionManager) mw.get("sm");

        String requestURI = request.getUri();
        String searchForThisUser = requestURI.split("/")[2];
        String token = request.getHeader("Authorization");

        if(!sm.hasAccess(token, searchForThisUser)){
            return HTTPPackage.generateErrorResponse(403, "Access denied", "Token "+token+" has no authority over resource user."+searchForThisUser);
        }

        User user = null;
        try {
            db.open();
            user = db.getUserByUsername(searchForThisUser);
            db.close();
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }

        return HTTPPackage.generateBasicResponse(user.toString());

    }
}
