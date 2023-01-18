package Server.Models.Cards;

import Server.Misc.Finetuners;
import Server.Models.Cards.Effects.Bleeding;
import Server.Models.Cards.Effects.Burning;
import Server.Models.Cards.Effects.Healing;
import Server.Models.Cards.Effects.StatusEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class CardWrapper{
    Card host;

    private float virtualHealth = 0f;
    private ArrayList<StatusEffect> statusEffects = new ArrayList<StatusEffect>();
    private boolean virtualCritical = false;


    public CardWrapper(Card card){
        this.host = card;
        this.virtualHealth = this.host.getHealth();
    }

    // -------------- Setter / Getter
    public Card getHost() {
        return host;
    }

    public void setHost(Card host) {
        this.host = host;
    }

    public void addStatusEffect(StatusEffect se){
        this.statusEffects.add(se);
    }

    public boolean isVirtualCritical() {
        return virtualCritical;
    }

    public void setVirtualCritical(boolean virtualCritical) {
        this.virtualCritical = virtualCritical;
    }

    // ----------------- For Battle -----------------
    public String attackString(String opponentName, Rules rules) {
        StringBuilder sb = new StringBuilder();

        sb.append(this.host.getName()).append(": ");

        if(virtualCritical) {
            sb.append("Critical hit on ");
        } else {
            sb.append("Attack on ");
        }

        sb.append( opponentName );

        short rulecode = rules.checkRules();

        if (rulecode != 0) {
            sb.append(" resulted in ");
            switch (rulecode) {
                case -2:
                    sb.append("total failure!");
                    break;
                case -1:
                    sb.append("INSANE DAMAGE!");
                    break;
                case 1:
                    sb.append("a slight advantage.");
                    break;
                case 2:
                    sb.append("a slight disadvantage.");
                    break;
            }
        }


        return sb.toString();
    }

    public float calculateDamage(Rules rules) {

        virtualCritical = false;
        if(rules.criticalRule(this.host.getCriticalChance())){
            this.virtualCritical = true;
        }

        float outDamage = this.host.getDamage();




        if(virtualCritical) {
            // Critical hit!
            outDamage *= Finetuners.CRITICAL_DAMAGE_MULTIPLIER;
        }


        short rulecode = rules.checkRules();

        switch (rulecode) {
            case -2:
                outDamage = 0;
                break;
            case -1:
                outDamage = Float.POSITIVE_INFINITY;
                break;
            case 1:
                outDamage *= 1.25;
                break;
            case 2:
                outDamage /= 1.25;
                break;
        }

        return outDamage;
    }

    public void resetVirtualHealth() {
        this.setVirtualHealth(this.host.getHealth());
    }

    public float calculateVirtualHealth(float damage) {

        float tempVirtualHealth = this.getVirtualHealth();
        tempVirtualHealth -= damage;

        // ----- APPLY EFFECTS ----- //
        for (int i = 0; i < this.statusEffects.size(); i++) {
            StatusEffect se = this.statusEffects.get(i);
            tempVirtualHealth -= se.iterateValue();

            if(!se.isActive()){
                this.statusEffects.remove(se);
            }
        }


        return tempVirtualHealth;
    }

    public boolean isDead() {
        if (this.getVirtualHealth() <= 0 ){
            return true;
        }
        return false;
    }

    public void setVirtualHealth(float health) {
        this.virtualHealth = health;
    }

    public float getVirtualHealth() {
        return this.virtualHealth;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("This is a ");

        if (this.host.getMonster() == Monster.SPELL){
            sb.append("Spell Card ");
        }else{
            sb.append("Monster Card ");
        }

        sb.append("with the type ");
        sb.append(this.host.getElement().toString());

        sb.append(" and the damage ");
        sb.append(this.host.getDamage());

        return sb.toString();
    }


    public ArrayList<StatusEffect> getStatusEffects() {
        return statusEffects;
    }

    public void setStatusEffects(ArrayList<StatusEffect> statusEffects) {
        this.statusEffects = statusEffects;
    }
}
