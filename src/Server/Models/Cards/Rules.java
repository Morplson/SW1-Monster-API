package Server.Models.Cards;

import Server.Misc.Finetuners;

import java.util.Random;

public class Rules {

    Random rand;

    Card aCard;
    Card bCard;

    Element aElement;
    Element bElement;
    Monster aMonster;
    Monster bMonster;


    /**
     * Returns rules on cases for "A attacks B"
     * @param a OldActor A
     * @param b OldActor B
     */
    public Rules(Card a, Card b) {
        aCard = a;
        bCard = b;
        aElement = a.getElement();
        bElement = b.getElement();
        aMonster = a.getMonster();
        bMonster = b.getMonster();

        rand = new Random();
    }

    /**
     * @return -2 for failure, -1 for instant kill, 0 for neutral exchange, 1 for advantage, 2 for mallus.
     */
    public short checkRules(){


        // Calculate Special-Cases

        if ( // Special DragonFireElfRule: if Dragon attacks Fire Elf, attack fails
                checkSpecialDragonFireElfRule()
        ) {
            return -2;
        }

        if ( // Special GoblinDragonRule: if goblin attacks dragon, attack fails
                checkSpecialGoblinDragonRule()
        ) {
            return -2; //Goblins are too affraid of dragons
        }

        if ( // Special KnightWaterRule: if Waterspell attacks knight, knight dies
                checkSpecialKnightWaterRule()
        ) {
            return -1;
        }

        if ( // Special KrakenSpellRule: if any spell attacks kraken, attack fails
                checkSpecialKrakenSpellRule()
        ) {
            return -2; //Krakens resist all spells
        }



        // Calculate RPC-Cases

        if ( rpcDisadvantageRule() ) {
            return 2;
        }
        if ( rpcAdvantageRule() ) {
            return 1;
        }

        return 0;
    }

    /**
     * Rpc advantage
     * @return true if element is rock paper scisers advantaged
     */
    public boolean rpcAdvantageRule(){
        if (aMonster == Monster.SPELL || bMonster == Monster.SPELL){
            if ( // RPC advantaged
                    aElement == Element.FIRE && bElement == Element.NORMAL ||
                            aElement == Element.WATER && bElement == Element.FIRE ||
                            aElement == Element.NORMAL && bElement == Element.WATER
            ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rpc disadvantage
     * @return true if element is rock paper scisers disadvantaged
     */
    public boolean rpcDisadvantageRule(){
        if (aMonster == Monster.SPELL || bMonster == Monster.SPELL) {
            if ( // RPC advantaged
                    aElement == Element.FIRE && bElement == Element.WATER ||
                            aElement == Element.WATER && bElement == Element.NORMAL ||
                            aElement == Element.NORMAL && bElement == Element.FIRE
            ) {
                return true;
            }
        }
        return false;
    }

    public boolean checkSpecialDragonFireElfRule() {
        return aMonster == Monster.DRAGON && bElement == Element.FIRE && bMonster == Monster.ELVE;
    }

    public boolean checkSpecialGoblinDragonRule() {
        return aMonster == Monster.GOBLIN && bMonster == Monster.DRAGON;
    }

    public boolean checkSpecialKnightWaterRule() {
        return aMonster == Monster.SPELL && aElement == Element.WATER && bMonster == Monster.KNIGHT;
    }

    public boolean checkSpecialKrakenSpellRule() {
        return aMonster == Monster.SPELL && bMonster == Monster.KRAKEN;
    }


    public boolean burningRule(float chance) {
        if(bElement == Element.FIRE) {
            chance /= 2;
        }
        if (rand.nextFloat() < chance) {
            if (
                aElement == Element.FIRE && !(bElement == Element.WATER) &&
                (aMonster == Monster.SPELL || aMonster == Monster.DRAGON || aMonster == Monster.WIZARD) &&
                !(bMonster == Monster.SPELL || bMonster == Monster.DRAGON)
            ) {
                return true;
            }
        }
        return false;
    }

    public boolean healingRule(float chance) {
        if (aMonster == Monster.ELVE && aElement == Element.WATER){
            chance *= 2.5;
        }
        if (aMonster == Monster.KNIGHT) {
            chance *= 0.75;
        }


        if (rand.nextFloat() < chance) {
            if (
                aMonster == Monster.ELVE || aMonster == Monster.WIZARD || aMonster == Monster.KNIGHT
            ){
                return true;
            }
        }

        return false;
    }

    public boolean bleedingRule(float chance) {
        if (aMonster == Monster.DRAGON){
            chance *= 2.5;
        }
        if (bMonster == Monster.DRAGON || bMonster == Monster.WIZARD){
            chance *= 0.5;
        }
        if (aMonster == Monster.DRAGON && bMonster == Monster.DRAGON){
            chance = Finetuners.DRAGON_HATRED_CHANCE;
        }

        if (rand.nextFloat() < chance) {
            if (
                (aMonster == Monster.DRAGON || aMonster == Monster.KNIGHT || aMonster == Monster.ORK || aMonster == Monster.TROLL) &&
                !(bMonster == Monster.ELVE || bMonster == Monster.SPELL)
            ) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     *
     * Combinations in which a higher chance of striking a critical hit is fitting:
     *     Knight vs Ork: A skilled and experienced knight fighting against a brutish and unrefined Ork would give the knight a higher chance of striking a critical hit.
     *     Elve vs Troll: An agile and swift Elve fighting against a slow and clumsy Troll would give the Elve a higher chance of striking a critical hit.
     *     Goblin vs Dragon: A cunning and resourceful Goblin using their tricks and traps against a powerful but overconfident Dragon would give the Goblin a higher chance of striking a critical hit.
     * Pairings in which it is less likely a critical hit can be achieved:
     *     Spell vs Kraken: A spell would have a difficult time landing a critical hit on a giant and powerful Kraken, which is known to resist all spells.
     *     Dragon vs Knight: A dragon's power and size would be a formidable obstacle for a knight to land a critical hit, as they would be well-trained and equipped to fight against dragons.
     *     Wizard vs Troll: A wizard would have a difficult time landing a critical hit on a large and durable troll with its spells, as they are not physically built to do so.
     *
     */
    public boolean criticalRule(float chance) {
        if (aMonster == Monster.GOBLIN || aMonster == Monster.ELVE){
            chance *= 1.12;
        }
        if (
            (aMonster == Monster.KNIGHT && bMonster == Monster.ORK) ||
            (aMonster == Monster.ELVE && bMonster == Monster.TROLL) ||
            (aMonster == Monster.GOBLIN && bMonster == Monster.DRAGON)
        ){
            chance *= 1.25;
        }

        if (
            (aMonster == Monster.SPELL && bMonster == Monster.KRAKEN) ||
            (aMonster == Monster.DRAGON && bMonster == Monster.KNIGHT) ||
            (aMonster == Monster.WIZARD && bMonster == Monster.TROLL)
        ){
            chance *= 0.8;
        }

        if (rand.nextFloat() < chance){
            return true;
        }
        return false;
    }
}