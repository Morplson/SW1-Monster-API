package Server.RouteWorkers.BattleWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.User;
import Server.RouteWorkers.RouteWorker;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShowScoreboardRouteWorker implements RouteWorker {

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
        HashMap<String, Integer> scoreboard;
        try {
            db.open();

            scoreboard = db.getScoreboard();

        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }finally {
            db.close();
        }


        // ---- Generate Response ---- //
        StringBuilder body = new StringBuilder();
        boolean plain = request.getQuery("format").equalsIgnoreCase("plain");
        if( plain ) {
            body.append("++++++++ SCOREBOARD ++++++++").append(System.lineSeparator());

            List<Map.Entry<String, Integer>> sortedEntries = scoreboard.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());

            int place = 1;
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                body.append(place).append("# ").append( entry.getKey()).append(" : ").append(entry.getValue()).append(System.lineSeparator());
                place++;
            }


        } else {
            body.append("[ ");

            int place = 1;
            for (Map.Entry<String, Integer> entry : scoreboard.entrySet()) {
                body.append("place: ").append(place).append(", ");
                body.append("username: ").append(entry.getKey()).append(", ");
                body.append("elo: ").append(entry.getValue()).append(", ");
                place++;
            }
            if (body.length() > 2) {
                body.deleteCharAt(body.length() - 2);
            }

            body.append(" ]");
        }

        return HTTPPackage.generateBasicResponse(body.toString(), true);
    }
}
