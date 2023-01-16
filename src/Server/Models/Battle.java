package Server.Models;

import Server.Models.Cards.Card;
import Server.Models.Cards.CardWrapper;
import Server.Models.Cards.Effects.Bleeding;
import Server.Models.Cards.Effects.Burning;
import Server.Models.Cards.Effects.Healing;
import Server.Models.Cards.Rules;
import Server.RouteWorkers.CardWorkers.CreatePackageRouteWorker;

import java.util.ArrayList;
import java.util.Collections;

public class Battle {
    User playerA;
    User playerB;

    ArrayList<CardWrapper> virtualDeckA = new ArrayList<CardWrapper>();
    ArrayList<CardWrapper> virtualDeckB = new ArrayList<CardWrapper>();

    short winner; // 0 = draw, 1 = player A, 2 = player B

    StringBuilder gamelog = new StringBuilder();

    // point rules
    //    Winning the game: 100 points
    //    Killing an opponent's card: 25 points
    //    Dealing damage to an opponent's card: 1 points per point of damage dealt
    //    Losing the game: -30 points
    //    Special effects such as burning: 10 points per effect
    int pointsPlayerA = 0;
    int pointsPlayerB = 0;

    //chances
    float burnchance = 0.2f;
    float bleedingchance = 0.05f;
    //float ragechance = 0.2f;
    float healingchance = 0.05f;


    public Battle(User player1,User player2) {
        this.playerA = player1;
        this.playerB = player2;
        
        this.setupBattle();
        
    }
    
    public void setupBattle() {
        ArrayList<Card> deckA = this.playerA.getDeck();
        for (int i = 0; i < deckA.size(); i++) {
            this.virtualDeckA.add(
                    new CardWrapper(deckA.get(i))
            );
        }
        ArrayList<Card> deckB = this.playerB.getDeck();
        for (int i = 0; i < deckB.size(); i++) {
            this.virtualDeckB.add(
                    new CardWrapper(deckA.get(i))
            );
        }
    }

    public void play() {

        // Run game
        int round = 1;
        do{
            gamelog.append("Round ").append(round).append(": {").append(System.lineSeparator());
            attackRound();
            gamelog.append("}").append(System.lineSeparator());
            round++;

            pointsPlayerA -= round*2;
            pointsPlayerB -= round*2;
        } while (this.virtualDeckA.size() > 0 && this.virtualDeckB.size() > 0);

        // Determen winner
        if (virtualDeckA.size() <= 0 && virtualDeckB.size() <= 0){
            winner = 0;
        } else if (virtualDeckA.size() <= 0) {
            winner = 1;
            pointsPlayerA += 100;
            pointsPlayerB -= 30;

            System.out.println("Player A won: "+ playerA.getUsername());
        } else if (virtualDeckB.size() <= 0) {
            winner = 2;
            pointsPlayerB += 100;
            pointsPlayerA -= 30;

            System.out.println("Player A won: "+ playerB.getUsername());

        } else {
            winner = -1;
        }

    }
    

    public void attackRound() {
        StringBuilder roundLog = new StringBuilder();

        // ------- GET A CARD --------- //
        Collections.shuffle(this.virtualDeckA);
        Collections.shuffle(this.virtualDeckB);
        CardWrapper stagedCardA = this.virtualDeckA.remove(0);
        CardWrapper stagedCardB = this.virtualDeckB.remove(0);

        //log
        roundLog.append(playerA.getUsername())
                .append(" staged: ")
                .append(stagedCardA.getHost().getName())
                .append(" (").append(stagedCardA.getHost().getDamage()).append(") ")
                .append(" vs. ")
                .append(playerB.getUsername())
                .append(" staged: ")
                .append(stagedCardB.getHost().getName())
                .append(" (").append(stagedCardB.getHost().getDamage()).append(") ")
                .append(System.lineSeparator());


        // -------- GENERATE RULESETS -------- //
        Rules benefitsPlayerA = new Rules(stagedCardA.getHost(), stagedCardB.getHost());
        Rules benefitsPlayerB = new Rules(stagedCardB.getHost(), stagedCardA.getHost());


        // ------- CALCULATE ROUND DAMAGE -----//
        //attack a1 -> a2
        float damage1 = stagedCardA.calculateDamage(benefitsPlayerA);
        pointsPlayerA += damage1;
        //attack a2 -> a1
        float damage2 = stagedCardB.calculateDamage(benefitsPlayerB);
        pointsPlayerB += damage2;

        //log
        roundLog.append(stagedCardA.attackString(stagedCardB.getHost().getName(), benefitsPlayerA)).append(System.lineSeparator());
        roundLog.append(stagedCardB.attackString(stagedCardA.getHost().getName(), benefitsPlayerB)).append(System.lineSeparator());


        // -------- APPLY STATUS EFFECTS ------ //
        //burning
        if (benefitsPlayerA.burningRule(burnchance)){
            stagedCardB.addStatusEffect(new Burning(stagedCardA.getHost().getDamage()/2,3));
            roundLog.append(stagedCardB.getHost().getName()).append(" caught Fire for 3 rounds!").append(System.lineSeparator());
            pointsPlayerA += 10;
        }
        if (benefitsPlayerB.burningRule(burnchance)){
            stagedCardA.addStatusEffect(new Burning(stagedCardB.getHost().getDamage()/2, 3));
            roundLog.append(stagedCardA.getHost().getName()).append(" caught Fire for 3 rounds!").append(System.lineSeparator());
            pointsPlayerB += 10;
        }
        //healing
        if (benefitsPlayerA.healingRule(healingchance)){
            stagedCardA.addStatusEffect(new Healing(stagedCardA.getHost().getHealth()/5,2));
            roundLog.append(stagedCardA.getHost().getName()).append(" heals themselves through magical powers for the next 2 turns!").append(System.lineSeparator());
            pointsPlayerA += 10;
        }
        if (benefitsPlayerB.healingRule(healingchance)){
            stagedCardB.addStatusEffect(new Healing(stagedCardB.getHost().getHealth()/5, 2));
            roundLog.append(stagedCardB.getHost().getName()).append(" heals themselves through magical powers for the next 2 turns!").append(System.lineSeparator());
            pointsPlayerB += 10;
        }
        //bleeding
        if (benefitsPlayerA.bleedingRule(bleedingchance)){
            stagedCardB.addStatusEffect(new Bleeding(stagedCardA.getHost().getDamage(),5));
            roundLog.append(stagedCardB.getHost().getName()).append(" has gotten struck BADLY and will bleed out in 5 rounds.").append(System.lineSeparator());
            pointsPlayerA += 10;
        }
        if (benefitsPlayerB.bleedingRule(bleedingchance)){
            stagedCardA.addStatusEffect(new Bleeding(stagedCardB.getHost().getDamage(), 5));
            roundLog.append(stagedCardA.getHost().getName()).append(" has gotten struck BADLY and will bleed out in 5 rounds.").append(System.lineSeparator());
            pointsPlayerB += 10;
        }




        // -------- APPLY CALCULATED DAMAGE ------ //
        float previousHealthOfA = stagedCardA.getVirtualHealth();
        float afterHealthOfA = stagedCardA.calculateVirtualHealth(damage2);
        stagedCardA.setVirtualHealth(afterHealthOfA);

        float previousHealthOfB = stagedCardB.getVirtualHealth();
        float afterHealthOfB = stagedCardB.calculateVirtualHealth(damage1);
        stagedCardB.setVirtualHealth(afterHealthOfB);

        //log
        roundLog.append(stagedCardA.getHost().getName()).append("-health: ");
        roundLog.append("(").append(previousHealthOfA).append(") => (").append(afterHealthOfA).append(")");
        roundLog.append(" ⚔ ");
        roundLog.append(stagedCardB.getHost().getName()).append("-health: ");
        roundLog.append("(").append(previousHealthOfB).append(") => (").append(afterHealthOfB).append(")");
        roundLog.append(System.lineSeparator());

        // -------- CHECK IF CARD IS DEAD -------- //
        if (!stagedCardA.isDead()){
            this.virtualDeckA.add(stagedCardA);
        } else {
            this.pointsPlayerB += 25;

            //log
            roundLog.append(stagedCardA.getHost().getName()).append(" was slain!").append(System.lineSeparator());
        }
        if (!stagedCardB.isDead()){
            this.virtualDeckB.add(stagedCardB);
        } else {
            this.pointsPlayerA += 25;

            //log
            roundLog.append(stagedCardB.getHost().getName()).append(" was slain!").append(System.lineSeparator());
        }

        System.out.println(roundLog);

        this.gamelog.append(roundLog);
    }

    public int getPointsByPlayerName(String playerName){
        if (playerName.equalsIgnoreCase(this.playerA.getUsername())){
            return this.pointsPlayerA;
        } else {
            return this.pointsPlayerB;
        }
    }

    public String getWinnerUsername(){
        if (winner == 1){
            return this.playerA.getUsername();
        } else {
            return this.playerB.getUsername();
        }
    }

    public boolean isDraw(){
        return (winner == 0);
    }

    public User getOpponentByPlayerName(String playerName){
        if (playerName.equalsIgnoreCase(this.playerA.getUsername())){
            return this.playerB;
        } else {
            return this.playerA;
        }
    }

    public StringBuilder getGamelog(){
        return this.gamelog;
    }

}
