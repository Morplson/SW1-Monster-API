package Server.Models.Cards;

import Server.Misc.Finetuners;
import Server.Models.Serializable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Card implements Serializable {
    @JsonProperty(value = "Id")
    private String id = null;
    @JsonProperty(value = "Name")
    private String name = null;

    @JsonProperty(value = "Damage")
    private Float damage = null;
    @JsonProperty(value = "Health")
    private Float health = null;

    @JsonProperty(value = "Element")
    private Element element = null;
    @JsonProperty(value = "Monster")
    private Monster monster = null;
    @JsonProperty(value = "CriticalChance")
    private Float criticalChance = null;

    @JsonCreator
    public Card(@JsonProperty(value = "Id", required = true) String id,
                @JsonProperty(value = "Name", required = true) String name,
                @JsonProperty(value = "Damage", required = true) Float damage,
                @JsonProperty(value = "Health") Float health,
                @JsonProperty(value = "Element") Element element,
                @JsonProperty(value = "Monster") Monster monster,
                @JsonProperty(value = "CriticalChance") Float criticalChance) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.health = health != null ? health : damage*Finetuners.UNSET_HEALTH_MULTIPLIER;

        this.element = element != null ? element : inferElementFromName(name);
        this.monster = monster != null ? monster : inferMonsterFromName(name);

        this.criticalChance = criticalChance != null ? criticalChance : 10/(1+damage*Finetuners.UNSET_CRITICAL_MULTIPLIER);
    }

    private Element inferElementFromName(String name) {
        name = name.toLowerCase();
        for (Element e : Element.values()) {
            if (name.contains(e.name().toLowerCase())) {
                return e;
            }
        }
        return Element.NORMAL;
    }

    private Monster inferMonsterFromName(String name) {
        name = name.toLowerCase();
        for (Monster m : Monster.values()) {
            if (name.contains(m.name().toLowerCase())) {
                return m;
            }
        }
        return Monster.SPELL;
    }


    public static Card fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Card.class);
    }

    @Override
    public String toJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @Override
    public String toFancyString() {
        StringBuilder sb = new StringBuilder();
        sb.append("####################################\n");
        sb.append("#                                  #\n");
        sb.append("#     Card Information             #\n");
        sb.append("#                                  #\n");
        sb.append("####################################\n");
        sb.append("Id: " + id + "\n");
        sb.append("Name: " + name + "\n");
        sb.append("Damage: " + damage + "\n");
        sb.append("Health: " + health + "\n");
        sb.append("Element: " + element + "\n");
        sb.append("Monster: " + monster + "\n");
        sb.append("CriticalChance: " + criticalChance + "\n");
        return sb.toString();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Card{");
        sb.append("Id: " + id + ", ");
        sb.append("Name: " + name + ", ");
        sb.append("Damage: " + damage + ", ");
        sb.append("Health: " + health + ", ");
        sb.append("Element: " + element + ", ");
        sb.append("Monster: " + monster + ", ");
        sb.append("CriticalChance: " + criticalChance);
        sb.append("}");
        return sb.toString();
    }

    public boolean isSpell() {
        if (monster == Monster.SPELL){
            return true;
        }
        return false;
    }


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

    public Float getDamage() {
        return damage;
    }

    public void setDamage(Float damage) {
        this.damage = damage;
    }

    public Float getHealth() {
        return health;
    }

    public void setHealth(Float health) {
        this.health = health;
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

    public Float getCriticalChance() {
        return criticalChance;
    }

    public void setCriticalChance(Float criticalChance) {
        this.criticalChance = criticalChance;
    }
}

