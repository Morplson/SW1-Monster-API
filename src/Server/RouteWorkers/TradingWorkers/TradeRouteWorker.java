package Server.RouteWorkers.TradingWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.Cards.Card;
import Server.Models.Trade;
import Server.RouteWorkers.RouteWorker;

import java.util.ArrayList;

public class TradeRouteWorker implements RouteWorker {

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
            return HTTPPackage.generateErrorResponse(403, "Access denied", "Token "+token+" has no authority over resource user."+username+".cards");
        }

        // ---- Parse cardId ---- //
        String cardId = request.getBodyPlain();
        cardId = cardId.substring(1,cardId.length()-1);

        // ---- Process in Database ---- //
        Trade trade = null;
        Card card = null;
        try {
            db.open();

            if(db.checkUsernameByTradeId(tradeId, username)){
                return HTTPPackage.generateErrorResponse(403, "Access denied", "can't trade with yourself");
            };

            card = db.getCardById(cardId);
            trade = db.getTradeById(tradeId);

            if(card == null || trade == null){
                return HTTPPackage.generateErrorResponse(404, "Not found", "Card or trade not found");
            }

            if(card.getDamage() < trade.getMinimumDamage()){
                return HTTPPackage.generateErrorResponse(403, "Access denied", "Damage of your card is too low");
            }

            if(trade.getElement() != null && trade.getElement() != card.getElement()){
                return HTTPPackage.generateErrorResponse(403, "Access denied", "You can't trade with a different element");
            }

            if(trade.getType() != null && trade.getType() != card.getMonster()){
                return HTTPPackage.generateErrorResponse(403, "Access denied", "You can't trade with a different monster");
            }

            db.returnTradedCard(tradeId, username);
            db.transferCard(username, trade.getPostedBy(), cardId);

        } catch (Exception e) {
            return HTTPPackage.generateErrorResponse(500, "Processing Error", e.getMessage());
        }finally {
            db.close();
        }


        // ---- Generate Response ---- //
        boolean plain = request.getQuery("format").equalsIgnoreCase("plain");
        return HTTPPackage.generateBasicResponse("traded cards! "+cardId+" with "+trade.getCardToTrade(), plain);
    }
}
