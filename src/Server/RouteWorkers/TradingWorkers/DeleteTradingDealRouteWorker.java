package Server.RouteWorkers.TradingWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.Trade;
import Server.RouteWorkers.RouteWorker;

import java.util.ArrayList;

public class DeleteTradingDealRouteWorker implements RouteWorker {

    @Override
    public HTTPPackage process(HTTPPackage request, MiddlewareRegister mr) {
        // ---- Get Middlewares ---- //
        Database db = (Database) mr.get("db");
        SessionManager sm = (SessionManager) mr.get("sm");

        // ---- Check Authentication ---- //
        String token = request.getHeader("Authorization");

        String requestURI = request.getUri();
        String tradeId = requestURI.split("/")[2];

        if (!sm.isLoggedIn(token)) {
            return HTTPPackage.generateErrorResponse(401, "Unauthorized", "Not logged in");
        }

        String username = sm.getUsername(token);

        if(!sm.hasAccess(token, username)){
            return HTTPPackage.generateErrorResponse(403, "Access denied", "Token "+token+" has no authority over resource trades.delete");
        }


        // ---- Process in Database ---- //
        try {
            db.open();

            if(!db.checkUsernameByTradeId(tradeId, username)){
                return HTTPPackage.generateErrorResponse(403, "Access denied", username+" not owner of trade");
            };

            db.returnTradedCard(tradeId, username);

        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }finally {
            db.close();
        }


        // ---- Generate Response ---- //
        boolean plain = request.getQuery("format").equalsIgnoreCase("plain");
        return HTTPPackage.generateBasicResponse("removed card from trade", plain);
    }
}
