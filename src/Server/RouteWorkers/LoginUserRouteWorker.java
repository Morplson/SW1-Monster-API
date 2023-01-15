package Server.RouteWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;


public class LoginUserRouteWorker implements RouteWorker {

    @Override
    public HTTPPackage process(HTTPPackage request, MiddlewareRegister mw) {
        Database db = (Database) mw.get("db");
        SessionManager sm = (SessionManager) mw.get("sm");

        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string
        String json = request.getBodyPlain();
        Map<String, String> map;

        try {
            map  = objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(400, "Invalid JSON","Invalid JSON: " + e.getMessage());
        }


        // Read the data
        String username = map.get("Username");
        String password = map.get("Password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return HTTPPackage.generateErrorResponse(400, "Invalid Data","Invalid Data");
        }

        String token, accessDomain;
        try {
            db.open();
            token = db.getUserToken(username, password);
            accessDomain = db.getUserAccessDomain(username, password);
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }finally {
            db.close();
        }

        if (token == null || accessDomain == null) {
            return HTTPPackage.generateErrorResponse(401, "Unauthorized", "Unauthorized: Bad credentials");
        }

        //"login user"
        if(!sm.register(token, accessDomain, username)){
            return HTTPPackage.generateErrorResponse(400, "Bad Request", "Already loged in");
        }




        return HTTPPackage.generateBasicResponse(token);
    }
}
