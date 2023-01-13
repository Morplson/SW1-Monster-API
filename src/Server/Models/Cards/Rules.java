package Server.Models.Cards;

import Server.Models.Cards.Actors.Actor;

public class Rules {

    Actor aCard;
    Actor bCard;

    Element aElement;
    Element bElement;
    Monster aMonster;
    Monster bMonster;


    /**
     * Returns rules on cases for "A attacks B"
     * @param a Actor A
     * @param b Actor B
     */
    public Rules(Actor a, Actor b) {
        aCard = a;
        bCard = b;
        aElement = a.getElement();
        bElement = b.getElement();
        aMonster = a.getMonster();
        bMonster = b.getMonster();
    }

    /**
     * @return -2 for failure, -1 for instant kill, 0 for neutral exchange, 1 for advantage, 2 for mallus.
     */
    public short checkRules(){


        // Calculate Special-Cases

        if ( // Special DragonFireElfRule: if Dragon attacks Fire Elf, attack fails
                aMonster == Monster.DRAGON &&
                bElement == Element.FIRE &&
                bMonster == Monster.ELVE
        ) {
            return -2;
        }

        if ( // Special GoblinDragonRule: if goblin attacks dragon, attack fails
                aMonster == Monster.GOBLIN &&
                bMonster == Monster.DRAGON
        ) {
            return -2; //Goblins are too affraid of dragons
        }

        if ( // Special KnightWaterRule: if Waterspell attacks knight, knight dies
                aMonster == Monster.SPELL &&
                aElement == Element.WATER &&
                bMonster == Monster.KNIGHT
        ) {
            return -1;
        }

        if ( // Special KrakenSpellRule: if any spell attacks kraken, attack fails
                aMonster == Monster.SPELL &&
                bMonster == Monster.KRAKEN
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
        if ( // RPC advantaged
                aElement == Element.FIRE && bElement == Element.NORMAL ||
                aElement == Element.WATER && bElement == Element.FIRE ||
                aElement == Element.NORMAL && bElement == Element.WATER
        ) {
            return true;
        }
        return false;
    }

    /**
     * Rpc disadvantage
     * @return true if element is rock paper scisers disadvantaged
     */
    public boolean rpcDisadvantageRule(){
        if ( // RPC advantaged
                aElement == Element.FIRE && bElement == Element.WATER ||
                aElement == Element.WATER && bElement == Element.NORMAL ||
                aElement == Element.NORMAL && bElement == Element.FIRE
        ) {
            return true;
        }
        return false;
    }
}