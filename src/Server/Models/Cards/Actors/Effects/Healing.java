package Server.Models.Cards.Actors.Effects;


import Server.Models.Cards.Actors.Actor;

public class Healing extends StatusEffect {
    private float ammount;

    public Healing(Actor actor, float ammount, int lifetime){
        super(actor, lifetime);
        this.ammount = ammount;
    }

    @Override
    public String attackString(Actor opponent) {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getActor().attackString(opponent));
        sb.append(" But is healed by ").append(this.ammount).append(" health points.");

        return sb.toString();
    }

    @Override
    public float calculateVirtualHealth(float damage) {
        if (this.getLifetime() > 0){
            damage -= ammount;
        }
        return super.calculateVirtualHealth(damage);
    }

    @Override
    public String toString() {
        return null;
    }
}
