package Server.RouteWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.User;

import java.util.ArrayList;
import java.util.HashMap;

public class AcquirePackageRouteWorker implements RouteWorker {

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

        // ---- Process in Database ---- //
        try {
            db.open();

            System.out.println(1);
            User user = db.getUserByUsername(username);
            int coins = user.getCoins();
            System.out.println(2);
            HashMap<String, Object> pack = db.getRandomPack();
            int price = (Integer) pack.get("price");
            System.out.println(3);

            if(coins < price){
                return HTTPPackage.generateErrorResponse(402, "Insufficient balance", "You don't have enough coins to buy this package");
            }

            user.setCoins(coins-price);
            db.updateUserByUsername(username, user);

            int pack_id = (Integer) pack.get("id");
            ArrayList<String> card_ids = db.getCardIdsByPackIdFromPackCards(pack_id);
            System.out.println(card_ids.size());

            for(String card_id : card_ids){
                db.addCardToUser(username, card_id, false);
                System.out.println(card_id);
            }

            db.removePackByPackIdCascade(pack_id);

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
            body.append("{ \"message\": \"package bought\"}");
        }

        return HTTPPackage.generateBasicResponse(body.toString());

    }
}
