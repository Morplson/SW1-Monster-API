package Server.Models.Cards.Actors.Effects;


import Server.Models.Cards.Actors.Actor;

import java.util.Random;

public class Burning extends StatusEffect{

    float ammount = 0.075f;

    public Burning(Actor actor, int lifetime) {
        super(actor, lifetime);
        this.ammount = new Random().nextFloat(0.0125f, 0.10f);
    }

    @Override
    public float calculateDamage(Actor opponent) {
        float health = opponent.getVirtualHealth();
        float burndamage = health*ammount;
        return this.getActor().calculateDamage(opponent) + burndamage;
    }

    @Override
    public String toString() {
        return null;
    }


    @Override
    public String attackString(Actor opponent) {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getActor().attackString(opponent));
        sb.append(" But is healed by ").append(this.ammount).append(" health points.");

        return sb.toString();
    }


}
