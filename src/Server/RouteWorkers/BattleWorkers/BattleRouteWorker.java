package Server.RouteWorkers.BattleWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.BattleQueue;
import Server.Middlewares.Database;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import Server.Models.Battle;
import Server.Models.Cards.Card;
import Server.Models.User;
import Server.RouteWorkers.RouteWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class BattleRouteWorker implements RouteWorker {
    @Override
    public HTTPPackage process(HTTPPackage request, MiddlewareRegister mr) {
        // ---- Get Middlewares ---- //
        Database db = (Database) mr.get("db");
        SessionManager sm = (SessionManager) mr.get("sm");
        BattleQueue bq = (BattleQueue) mr.get("bq");

        // ---- Check Authentication ---- //
        String token = request.getHeader("Authorization");

        String username = sm.getUsername(token);

        if(!sm.hasAccess(token, username)){
            return HTTPPackage.generateErrorResponse(403, "Access denied", "Token "+token+" has no authority over resource battles."+username);
        }


        StringBuilder body = new StringBuilder();
        boolean plain = request.getQuery("format").equalsIgnoreCase("plain");

        if( plain ) {
            body.append("####################################\n");
            body.append("#                                  #\n");
            body.append("#     Battle                       #\n");
            body.append("#                                  #\n");
            body.append("####################################\n");
        } else {
            body.append("{");
        }

        User user = null;
        try {
            db.open();
            user = db.getUserByUsername(username);
        } catch (SQLException e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        } finally {
            db.close();
        }

        Battle battle = null;
        try {
            battle = bq.register(user);
        } catch (InterruptedException e){
            return HTTPPackage.generateErrorResponse(500, "Interrupt Error","Interrupt Error: " + e.getMessage());
        } catch (TimeoutException e){
            return HTTPPackage.generateErrorResponse(500, "No Opponent found", "No Opponent found");
        }


        int userpoints = battle.getPointsByPlayerName(username);
        System.out.println(userpoints);

        User opponent = battle.getOpponentByPlayerName(username);


        try{
            db.open();

            if(! battle.isDraw()){
                if (username.equalsIgnoreCase(battle.getWinnerUsername())) {
                    // winner
                    System.out.println("winner");

                    user.setCoins(user.getCoins()+10);
                    user.setWins(user.getWins()+1);

                    ArrayList<Card> opponentDeck = opponent.getDeck();
                    for (Card card: opponentDeck) {
                        System.out.println("transfering card: "+ card.getName());
                        db.transferCard(opponent.getUsername(), username, card.getId());
                    }

                    if (plain) {
                        body.append("You has won the battle!\n");
                    } else {
                        body.append("result: 'win', ");
                    }


                }else {
                    // loser
                    System.out.println("loser");

                    user.setCoins(user.getCoins()+6);
                    user.setLosses(user.getLosses()+1);

                    if (plain) {
                        body.append("You has lost the battle!\n");
                    } else {
                        body.append("result: 'loss', ");
                    }
                }
            } else {
                //draw
                System.out.println("draw");

                user.setCoins(user.getCoins()+3);

                if (plain) {
                    body.append("The battle has ended in a draw!\n");
                } else {
                    body.append("result: 'draw', ");
                }
            }

            int opponentElo = opponent.getEloValue();
            int thisElo = user.getEloValue();

            double expected = calculateChance(thisElo, opponentElo);
            int earnedElo = (int) Math.floor(userpoints * expected);

            user.setEloValue(thisElo+earnedElo);

            db.updateUserByUsername(username, user);

            if (plain) {
                body.append("Your Opponent: ").append(opponent.getUsername()).append(System.lineSeparator());
                body.append("Earned Elo-points: ").append(earnedElo).append(System.lineSeparator());

                body.append("Battle log:").append(System.lineSeparator());
                body.append(battle.getGamelog());

            } else {
                body.append("opponent: '").append(opponent.getUsername()).append("', ");
                body.append("points: ").append(earnedElo).append(", ");
                body.append("log: '").append(battle.getGamelog()).append("'}");
            }

        } catch (SQLException e) {
            return HTTPPackage.generateErrorResponse(500, "Database Error","Database Error: " + e.getMessage());
        }  finally {
            db.close();
        }


        return HTTPPackage.generateBasicResponse(body.toString(), true);


    }

    public double calculateChance(int player1Score, int player2Score) {
        int totalScore = player1Score + player2Score;
        double lossChance = (double) player2Score / totalScore;
        return lossChance;
    }

}
