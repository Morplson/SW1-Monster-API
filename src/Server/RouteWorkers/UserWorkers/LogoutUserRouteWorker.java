package Server.RouteWorkers.UserWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.RouteWorkers.RouteWorker;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;


public class LogoutUserRouteWorker implements RouteWorker {

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

        //"login user"
        if(!sm.logout(token)){
            return HTTPPackage.generateErrorResponse(400, "Bad Request", "Not logged in");
        }

        return HTTPPackage.generateBasicResponse("Successfully logged out "+ token);
    }
}
