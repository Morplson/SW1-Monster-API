package Server.Models;

import Server.Models.Cards.Element;
import Server.Models.Cards.Monster;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Card implements Serializable {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Damage")
    private Float damage = 0f;
    @JsonProperty("Health")
    private Float health = 0f;

    @JsonProperty("Element")
    private Element element;
    @JsonProperty("Monster")
    private Monster monster;
    @JsonProperty(value = "CriticalChance", defaultValue = "10.0")
    private Double criticalChance;

    @JsonCreator
    public Card(@JsonProperty(value = "Id", required = true) String id,
                @JsonProperty(value = "Name", required = true) String name,
                @JsonProperty(value = "Damage", required = true) Float damage,
                @JsonProperty(value = "Health") Float health,
                @JsonProperty(value = "Element") Element element,
                @JsonProperty(value = "Monster") Monster monster,
                @JsonProperty(value = "CriticalChance") Double criticalChance) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.health = health != null ? health : damage*2;

        this.element = element != null ? element : inferElementFromName(name);
        this.monster = monster != null ? monster : inferMonsterFromName(name);

        this.criticalChance = criticalChance;
    }

    private Element inferElementFromName(String name) {
        for (Element e : Element.values()) {
            if (name.contains(e.name())) {
                return e;
            }
        }
        return Element.NORMAL;
    }

    private Monster inferMonsterFromName(String name) {
        for (Monster m : Monster.values()) {
            if (name.contains(m.name())) {
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

    public Double getCriticalChance() {
        return criticalChance;
    }

    public void setCriticalChance(Double criticalChance) {
        this.criticalChance = criticalChance;
    }
}

