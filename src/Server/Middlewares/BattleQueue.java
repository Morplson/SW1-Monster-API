package Server.Middlewares;

import Server.Models.Battle;
import Server.Models.User;

import java.util.concurrent.TimeoutException;


public class BattleQueue {
    private User waitingUser;
    private Battle battle;


    public Battle register(User user) throws InterruptedException {
        if (waitingUser == null) {
            // if there is no waiting user, create a lobby
            return waitForOpponent(user);
        } else {
            // if there is a waiting user, battle them
            return runBattle(user);
        }
    }

    private Battle waitForOpponent(User user) throws InterruptedException, TimeoutException{
        // set user as waiting
        this.battle = null;
        this.waitingUser = user;

        // wait for confirmation that another player has been found and battle commenced
        this.wait(10000);

        if (battle == null) {
            // no opponent found in timeout window
            this.waitingUser = null;
            throw new TimeoutException();
        } else {

            // return result
            return battle;
        }
    }

    private Battle runBattle(User user) {
        // Code to run the battle between user1 and user2
        if (this.waitingUser.getUsername().equalsIgnoreCase(user.getUsername())) {
            throw new IllegalStateException(); // you cant be a cake and eat it too XD
        }

        this.battle = new Battle(waitingUser, user);

        this.battle.

    }
}
