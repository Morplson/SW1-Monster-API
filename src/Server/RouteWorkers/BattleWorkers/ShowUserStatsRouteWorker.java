package Server.RouteWorkers.BattleWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.Cards.Card;
import Server.Models.User;
import Server.RouteWorkers.RouteWorker;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;

public class ShowUserStatsRouteWorker implements RouteWorker {

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
        User user;
        try {
            db.open();

            user = db.getUserByUsername(username);

        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }finally {
            db.close();
        }


        // ---- Generate Response ---- //
        StringBuilder body = new StringBuilder();
        boolean plain = request.getQuery("format").equalsIgnoreCase("plain");
        if( plain ) {
            body.append(user.getName()).append(": ").append(System.lineSeparator());
            body.append("\tElo: ").append(user.getEloValue()).append(System.lineSeparator());
            body.append("\tWins: ").append(user.getWins()).append(System.lineSeparator());
            body.append("\tLosses: ").append(user.getLosses()).append(System.lineSeparator());
            body.append("\tRate: ").append(user.getWins()/(user.getLosses()+1)).append(System.lineSeparator());

        } else {
            body.append("{ ");
            body.append("name: '").append(user.getName()).append("', ");
            body.append("elo: ").append(user.getEloValue()).append(", ");
            body.append("wins: ").append(user.getWins()).append(", ");
            body.append("losses: ").append(user.getLosses()).append(", ");
            body.append("rate: ").append(user.getWins()/(user.getLosses()+1));
            body.append(" }");
        }

        return HTTPPackage.generateBasicResponse(body.toString(), true);
    }
}
