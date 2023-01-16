package Server.RouteWorkers.CardWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.Cards.Card;
import Server.RouteWorkers.RouteWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class CreatePackageRouteWorker implements RouteWorker {

    @Override
    public HTTPPackage process(HTTPPackage request, MiddlewareRegister mr) {
        // ---- Get Middlewares ---- //
        Database db = (Database) mr.get("db");
        SessionManager sm = (SessionManager) mr.get("sm");

        // ---- Check Authentication ---- //
        String token = request.getHeader("Authorization");
        if(!sm.hasAccess(token, "package.new")){
            return HTTPPackage.generateErrorResponse(403, "Access denied", "Token "+token+" has no authority over resource package.new");
        }

        // ---- Parse JSON ---- //
        String json = request.getBodyPlain();
        ArrayList<Card> pack;
        try {
            ObjectMapper mapper = new ObjectMapper();
             pack = mapper.readValue(json, new TypeReference<ArrayList<Card>>(){});
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(400, "Invalid JSON","Invalid JSON: " + e.getMessage());
        }

        System.out.println(pack);

        // ---- Process in Database ---- //
        try {
            db.open();
            for(int i = 0; i < pack.size(); i++){
                db.insertCard(pack.get(i));
            }
            int pid = db.insertPack("Generic Pack", 5);
            for(int i = 0; i < pack.size(); i++){
                db.addCardToPack(pid, pack.get(i).getId());
            }
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }finally {
            db.close();
        }


        // ---- Generate Response ---- //
        StringBuilder body = new StringBuilder();
        if( request.getQuery("format").equalsIgnoreCase("plain") ) {
            //body.append("created user: ").append(username).append(" (").append(result).append(")");
        } else {
            body.append("{ \"message\": \"package creation successful\"}");
        }

        return HTTPPackage.generateBasicResponse(body.toString());

    }
}
