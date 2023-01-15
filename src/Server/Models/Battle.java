package Server.Models;

import Server.Models.OldCards.Actors.OldActor;

import java.util.ArrayList;
import java.util.Collections;

public class Battle {
    User p1;
    User p2;

    ArrayList<OldActor> vdP1;
    ArrayList<OldActor> vdP2;

    int pointsP1 = 0;
    int pointsP2 = 0;

    //chances
    float burnchance = 0.2f;


    public Battle(User player1,User player2) {
        this.p1 = player1;
        this.p2 = player2;
        
        this.setupBattle();
        
    }
    
    public void setupBattle() {
        for (int i = 0; i < this.vdP1.size(); i++) {
            this.vdP1.get(i).resetVirtualHealth();
        }
        for (int i = 0; i < this.vdP1.size(); i++) {
            this.vdP1.get(i).resetVirtualHealth();
        }
    }

    public void play() {
        boolean win = false;



        do{

            attackRound();




        } while (vdP1.size() > 0 || vdP2.size() > 0);

    }
    

    public void attackRound() {
        Collections.shuffle(this.vdP1);
        Collections.shuffle(this.vdP2);
        OldActor stagedCard1 = this.vdP1.remove(0);
        OldActor stagedCard2 = this.vdP2.remove(0);


        //attack a1 -> a2
        float d1 = stagedCard1.calculateDamage(stagedCard2);

        //attack a2 -> a1
        float d2 = stagedCard2.calculateDamage(stagedCard1);

        // TODO apply effects
        /*
        if (Rules(stagedCard1, stagedCard2).burningRule()){
            stagedCard2 = new Burning(stagedCard2, 2);
        }
        if (Rules(stagedCard1, stagedCard2).burningRule()){
            stagedCard1 = new Burning(stagedCard1, 2);
        }
        */


        stagedCard1.calculateVirtualHealth(d2);
        stagedCard2.calculateVirtualHealth(d1);

        if (!stagedCard1.isDead()){
            this.vdP1.add(stagedCard1);
            this.pointsP1 += 10;
        }
        if (!stagedCard2.isDead()){
            this.vdP2.add(stagedCard2);
            this.pointsP2 += 10;
        }

        //TODO: Wincondition


    }
}
