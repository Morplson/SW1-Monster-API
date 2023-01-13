package Server.Models.Cards;

import Server.Models.Cards.Actors.OldCard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class CardFactory {

    private Random random;

    private float spellChance;

     public CardFactory(){
         this.random = new Random();
         this.spellChance = 0.3f;
     }

    public OldCard guessCard(HashMap<String, Object> cardRepresentation){
        OldCard resultCard = this.randomCard();

        String name = (String)cardRepresentation.get("name");
        String type = (String)cardRepresentation.get("type");
        String element = (String)cardRepresentation.get("element");
        Integer damage = (Integer)cardRepresentation.get("damage");
        Integer health = (Integer)cardRepresentation.get("health");
        Double criticalChance = (Double)cardRepresentation.get("criticalChance");


        // guess type and such by name. (For curl)
        if (
                !(name == null || name.isEmpty()) &&
                (type == null || type.isEmpty() || element == null || element.isEmpty())
        ){
            String[] nameParts = name.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
            for (int i = 0; i < nameParts.length; i++) {
                String currentNamePart = nameParts[i];
                if(
                    Arrays.stream(Element.values()).anyMatch(e -> e.name().equals(currentNamePart)) &&
                    (element == null || element.isEmpty())
                ){
                    element = nameParts[i];
                }
                if(
                    Arrays.stream(Monster.values()).anyMatch(e -> e.name().equals(currentNamePart)) &&
                    (type == null || type.isEmpty())
                ){
                    type = nameParts[i];
                }
            }

            //if it's still empty just fill it with random shit.
            if (
                (type == null || type.isEmpty() || element == null || element.isEmpty())
            ){
                OldCard card = this.randomCard();
                type = card.getMonster().name();
                element = card.getElement().name();
            }
        }


        if ( !(name == null || name.isEmpty())){
            resultCard.setName(name);
        }

        if ( !(type == null || type.isEmpty())){
            resultCard.setMonster(Monster.valueOf(type));
        }

        if ( !(type == null || type.isEmpty())){
            resultCard.setMonster(Monster.valueOf(type));
        }

        if (damage!= null){
            resultCard.setDamage(damage);
        }
        if (health!= null) {
            resultCard.setHealth(health);
        }



         return resultCard;
    }
    public OldCard getCard(String id, String name, String type, String element, int damage, int health, double criticalChance) {
        Monster m = null;
        Element e = null;

        switch (type.toLowerCase()) {
            case "spell":
                m = Monster.SPELL;
                break;
            case "goblin":
                m = Monster.GOBLIN;
                break;
            case "dragon":
                m = Monster.DRAGON;
                break;
            case "wizard":
                m = Monster.WIZARD;
                break;
            case "ork":
                m = Monster.ORK;
                break;
            case "knight":
                m = Monster.KNIGHT;
                break;
            case "kraken":
                m = Monster.KRAKEN;
                break;
            case "elve":
                m = Monster.ELVE;
                break;
            case "troll":
                m = Monster.TROLL;
                break;
            default:
                throw new IllegalArgumentException("Unknown type of Monster" + type);
        }

        switch (element.toLowerCase()) {
            case "normal":
                e = Element.NORMAL;
                break;
            case "water":
                e = Element.WATER;
                break;
            case "fire":
                e = Element.FIRE;
                break;
            default:
                throw new IllegalArgumentException("Unknown type of Element" + element);
        }

        return new OldCard(id, name, damage, health, e, m, criticalChance);
    }

    public OldCard randomSpell() {
        StringBuilder name = new StringBuilder();

        int damage = this.random.nextInt(2,6);
        int health = this.random.nextInt(1,12);

        if(damage > health && damage > 5) {
            name.append("glass cannon ");
        }

        name.append("Spell of ");

        Element e = null;
        switch (this.random.nextInt(1,5)) {
            case 1:
                e = Element.WATER;
                name.append("refreshment");
                break;
            case 2:
                e = Element.FIRE;
                name.append("burning");
                break;
            default:
                e = Element.NORMAL;
                name.append("hurting");
        }

        double criticalChance = this.random.nextDouble(0,1);

        return new OldCard(name.toString(), damage, health, e, Monster.SPELL, criticalChance);
    }

    public OldCard randomMonster() {
        StringBuilder name = new StringBuilder();

        int damage = this.random.nextInt(4,10);
        int health = this.random.nextInt(6,26);

        if (damage < 9 && health < 20) {
            name.append("Worthy ");
        }

        Element e = null;
        switch (this.random.nextInt(1,5)) {
            case 1:
                e = Element.WATER;
                name.append("Water");
                break;
            case 2:
                e = Element.FIRE;
                name.append("Fire");
                break;
            default:
                e = Element.NORMAL;
        }


        Monster m = null;
        switch (new Random().nextInt(1,12)) {
            case 1: case 8:
                m = Monster.DRAGON;
                name.append("Dragon");
                break;
            case 2:
                m = Monster.WIZARD;
                name.append("Wizard");
                break;
            case 3:
                m = Monster.ORK;
                name.append("Ork");
                break;
            case 4: case 10:
                m = Monster.KNIGHT;
                name.append("Knight");
                break;
            case 5:
                m = Monster.KRAKEN;
                name.append("Kraken");
                break;
            case 6:
                m = Monster.ELVE;
                name.append("Elve");
                break;
            case 7: case 9:
                m = Monster.GOBLIN;
                name.append("Goblin");
                break;
            default:
                m = Monster.TROLL;
                name.append("Troll");
                break;
        }


        double criticalChance = this.random.nextDouble(0,1);

        return new OldCard(name.toString(), damage, health, e, m, criticalChance);
    }

    public OldCard randomCard() {
        if(this.random.nextDouble(0,1) > this.spellChance){
            return this.randomMonster();
        }

        return this.randomSpell();
    }
}
