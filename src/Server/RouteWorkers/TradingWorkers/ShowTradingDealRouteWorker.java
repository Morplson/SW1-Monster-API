package Server.RouteWorkers.TradingWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.Cards.Card;
import Server.Models.Trade;
import Server.Models.User;
import Server.RouteWorkers.RouteWorker;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;

public class ShowTradingDealRouteWorker implements RouteWorker {

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
        ArrayList<Trade> trades = new ArrayList<Trade>();
        try {
            db.open();

            trades = db.getAllTrades();

        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Processing Error", e.getMessage());
        }finally {
            db.close();
        }


        // ---- Generate Response ---- //
        StringBuilder body = new StringBuilder();
        boolean plain = request.getQuery("format").equalsIgnoreCase("plain");
        if( plain ) {
            body.append("####################################\n");
            body.append("#                                  #\n");
            body.append("#     Open Trades                  #\n");
            body.append("#                                  #\n");
            body.append("####################################\n");
            for( Trade trade : trades){
                body.append(trade.toString()).append(System.lineSeparator());
            }

        } else {
            body.append("[");
            for( Trade trade : trades){
                try {
                    body.append(trade.toJSON()).append(", ");
                }
                catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            if(body.length()>2) {
                body.deleteCharAt(body.length() - 2);
            }
            body.append("]");
        }

        return HTTPPackage.generateBasicResponse(body.toString(), true);
    }
}
