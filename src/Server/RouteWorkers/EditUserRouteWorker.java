package Server.RouteWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;


public class EditUserRouteWorker implements RouteWorker {

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


        // Parse the JSON string
        String json = request.getBodyPlain();
        User user;
        try {
            user = User.fromJson(json);
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(400, "Invalid JSON","Invalid JSON: " + e.getMessage());
        }

        // database
        int result = 0;
        try {
            db.open();
            result = db.updateUserByUsername(searchForThisUser, user);
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }finally {
            db.close();
        }

        String body = "updated user: " + searchForThisUser + " (" + result + ")";
        return HTTPPackage.generateBasicResponse(body);
    }
}
