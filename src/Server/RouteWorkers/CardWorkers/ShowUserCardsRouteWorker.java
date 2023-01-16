package Server.RouteWorkers.CardWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.Cards.Card;
import Server.RouteWorkers.RouteWorker;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;

public class ShowUserCardsRouteWorker implements RouteWorker {

    @Override
    public HTTPPackage process(HTTPPackage request, MiddlewareRegister mr) {
        // ---- Get Middlewares ---- //
        Database db = (Database) mr.get("db");
        SessionManager sm = (SessionManager) mr.get("sm");

        // ---- Check Authentication ---- //
        String token = request.getHeader("Authorization");

        if (!sm.isLoggedIn(token)) {
            return HTTPPackage.generateErrorResponse(401, "Unauthorized", "Not logged in");
        }

        String username = sm.getUsername(token);

        if(!sm.hasAccess(token, username)){
            return HTTPPackage.generateErrorResponse(403, "Access denied", "Token "+token+" has no authority over resource user."+username+".cards");
        }

        // ---- Process in Database ---- //
        ArrayList<Card> cards = new ArrayList<Card>();
        try {
            db.open();

            cards = db.getCardsByUsername(username, false);

        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }finally {
            db.close();
        }


        // ---- Generate Response ---- //
        StringBuilder body = new StringBuilder();
        boolean plain = request.getQuery("format").equalsIgnoreCase("plain");
        if( plain ) {
            body.append("####################################\n");
            body.append("#                                  #\n");
            body.append("#     User Cards                   #\n");
            body.append("#                                  #\n");
            body.append("####################################\n");
            for( Card card : cards){
                body.append(card.toString()).append(System.lineSeparator());
            }
        } else {
            body.append("[");
            for( Card card : cards){
                try {
                    body.append(card.toJSON()).append(", ");
                }
                catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            body.deleteCharAt(body.length() - 2);
            body.append("]");
        }

        return HTTPPackage.generateBasicResponse(body.toString(), plain);

    }
}
