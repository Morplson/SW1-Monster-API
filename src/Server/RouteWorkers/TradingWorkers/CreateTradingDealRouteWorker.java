package Server.RouteWorkers.TradingWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.Cards.Card;
import Server.Models.Trade;
import Server.RouteWorkers.RouteWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class CreateTradingDealRouteWorker implements RouteWorker {

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

        // ---- Parse JSON ---- //
        String json = request.getBodyPlain();
        Trade trade;
        try {
            trade = Trade.fromJson(json);
        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(400, "Invalid JSON","Invalid JSON: " + e.getMessage());
        }

        System.out.println(trade.toFancyString());

        // ---- Process in Database ---- //
        ArrayList<Trade> trades = new ArrayList<Trade>();
        try {
            db.open();

            db.tradeCard(
                    username,
                    trade
            );

        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }finally {
            db.close();
        }


        // ---- Generate Response ---- //
        boolean plain = request.getQuery("format").equalsIgnoreCase("plain");
        return HTTPPackage.generateBasicResponse("Put card into trading", plain);
    }
}
