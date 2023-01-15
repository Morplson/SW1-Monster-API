package Server.Models.OldCards.Actors;

import Server.Models.Cards.Element;
import Server.Models.Cards.Monster;

/**
 * Alles was Karten sind Aktöre :)
 * @author Bingus
 * @version 0.0
 */
public abstract class OldActor {
    public OldActor() {}


    private String id;
    private String name; //{ get; set; }

    private float damage = 0;
    private float health = 0;



    private Element element;
    private Monster monster;
    private double criticalChance;


    public OldActor(String id, String name, int damage, int health, Element element, Monster monster, double criticalChance){
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.health = health;
        this.element = element;
        this.monster = monster;
        this.criticalChance = criticalChance;
    }


    public boolean isSpell() {
        if (monster == Monster.SPELL){
            return true;
        }
        return false;
    }

    public abstract String attackString(OldActor opponent);

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public abstract float calculateDamage(OldActor opponent);
    public abstract void resetVirtualHealth();
    public abstract float calculateVirtualHealth(float damage);
    public abstract void updateValues();

    public abstract boolean isDead();

    public abstract void setVirtualHealth(float health);
    public abstract float getVirtualHealth();

    public abstract String toString();




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getDamage() {
        return damage;
    }

    public float getHealth() {
        return health;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Monster getMonster() {
        return monster;
    }

    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public double getCriticalChance() {
        return criticalChance;
    }

    public void setCriticalChance(double criticalChance) {
        this.criticalChance = criticalChance;
    }
}
