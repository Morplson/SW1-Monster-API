package Server.Models.Cards.Actors.Effects;

import Server.Models.Cards.Actors.Actor;

public abstract class StatusEffect extends Actor {
    private Actor actor;
    private int lifetime;


    public StatusEffect(Actor actor, int lifetime){
        super();
        this.actor = actor;
        this.lifetime = lifetime;
    }

    @Override
    public float calculateDamage(Actor opponent) {
        return this.getActor().calculateDamage(opponent);
    }
    @Override
    public void resetVirtualHealth(){
        this.getActor().resetVirtualHealth();
    }
    @Override
    public float calculateVirtualHealth(float damage){
        return this.getActor().calculateVirtualHealth(damage);
    }
    @Override
    public boolean isDead(){
        return this.getActor().isDead();
    }
    @Override
    public void setVirtualHealth(float health){
        this.getActor().setVirtualHealth(health);
    }
    @Override
    public float getVirtualHealth(){
        return this.getActor().getVirtualHealth();
    }
    @Override
    public void updateValues(){
        this.lifetime--;

        this.getActor().updateValues();
    }



    public Actor getActor() {
        return actor;
    }
    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public int getLifetime() {
        return lifetime;
    }
    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

}
