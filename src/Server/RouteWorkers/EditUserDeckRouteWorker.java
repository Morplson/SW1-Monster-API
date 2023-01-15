package Server.RouteWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class EditUserDeckRouteWorker implements RouteWorker {

    @Override
    public HTTPPackage process(HTTPPackage request, MiddlewareRegister mr) {
        // ---- Get Middlewares ---- //
        Database db = (Database) mr.get("db");
        SessionManager sm = (SessionManager) mr.get("sm");

        // ---- Check Authentication ---- //
        String token = request.getHeader("Authorization");

        String username = sm.getUsername(token);

        if(!sm.hasAccess(token, username)){
            return HTTPPackage.generateErrorResponse(403, "Access denied", "Token "+token+" has no authority over resource package.new");
        }

        // ---- Parse JSON ---- //
        String json = request.getBodyPlain();
        ArrayList<String> card_ids = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            card_ids = mapper.readValue(json, new TypeReference<ArrayList<String>>(){});
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(400, "Invalid JSON","Invalid JSON: " + e.getMessage());
        }

        if(card_ids.size()!=4){
            return HTTPPackage.generateErrorResponse(400, "Not enough cards","Not enough cards in request");
        }

        System.out.println(card_ids);

        // ---- Process in Database ---- //
        try {
            db.open();
            for(int i = 0; i < card_ids.size(); i++){
                db.updateCardInUser(username, card_ids.get(i), true);
            }
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }finally {
            db.close();
        }


        // ---- Generate Response ---- //
        boolean plain = request.getQuery("format").equalsIgnoreCase("plain");
        return HTTPPackage.generateBasicResponse("reassigned cards to deck", plain);

    }
}
