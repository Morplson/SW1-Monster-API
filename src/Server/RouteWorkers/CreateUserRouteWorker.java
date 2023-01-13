package Server.RouteWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Models.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;


public class CreateUserRouteWorker implements RouteWorker {

    @Override
    public HTTPPackage process(HTTPPackage request, MiddlewareRegister mw) {
        Database db = (Database) mw.get("db");
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string
        String json = request.getBodyPlain();
        User user;
        try {
            user = User.fromJson(json);
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(400, "Invalid JSON","Invalid JSON: " + e.getMessage());
        }


        //überprüfe eingabe
        String username = user.getUsername();
        String password = user.getPassword();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return HTTPPackage.generateErrorResponse(400, "Invalid Data","Invalid Data");
        }

        String token = "Basic "+username+"-mtcgToken";

        String domain = username.equalsIgnoreCase("admin") ? "\\S+" : username;


        int result = 0;
        try {
            db.open();
            result = db.insertUser(user, token, domain);
            db.close();
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }

        // Print the data
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        // Response:
        StringBuilder body = new StringBuilder();
        if( request.getQuery("format").equalsIgnoreCase("plain") ) {
           body.append("created user: ").append(username).append(" (").append(result).append(")");
        } else {
            body.append("{ \"message\": \"user creation successful\"}");
        }

        return HTTPPackage.generateBasicResponse(body.toString());
    }
}
