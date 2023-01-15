package Server.Models.Cards;

import Server.Models.Cards.Card;
import Server.Models.OldCards.Actors.OldActor;

public abstract class Actor {


    Integer virtualHealth;
    Integer virtualDamage;

    v

    public Actor(){
        virtualHealth = 100;
        virtualDamage = 10;
    }

    public abstract String attackString(OldActor opponent);

    public abstract float calculateDamage(OldActor opponent);
    public abstract void resetVirtualHealth();
    public abstract float calculateVirtualHealth(float damage);
    public abstract void updateValues();

    public abstract boolean isDead();

    public abstract void setVirtualHealth(float health);
    public abstract float getVirtualHealth();

    public abstract String toString();

}
