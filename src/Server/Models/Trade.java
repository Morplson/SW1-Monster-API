package Server.Models;

import Server.Models.Cards.Card;
import Server.Models.Cards.Element;
import Server.Models.Cards.Monster;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

/**
 * Alles Über den User.User ;)
 * @author Bingus
 * @version 0.0
 */
public class Trade implements Serializable {


    @JsonProperty(value = "Id")
    private String id = null;
    @JsonProperty(value = "CardToTrade")
    private String cardToTrade = null;
    @JsonProperty(value = "PostedBy")
    private String postedBy = null;

    @JsonProperty(value = "Type")
    private Monster type = null;
    @JsonProperty(value = "Element")
    private Element element = null;
    @JsonProperty(value = "MinimumDamage")
    private Integer minimumDamage = null;

    @JsonCreator
    public Trade(@JsonProperty(value = "Id") String id,
                 @JsonProperty(value = "CardToTrade") String cardToTrade,
                 @JsonProperty(value = "PostedBy") String postedBy,
                 @JsonProperty(value = "Type") String type,
                 @JsonProperty(value = "Element") String element,
                 @JsonProperty(value = "MinimumDamage") Integer minimumDamage) {
        this.id = id;
        this.cardToTrade = cardToTrade;
        this.postedBy = postedBy;
        if (type!= null) {
            type = (type.equalsIgnoreCase("monster")) ? null : type;
        }
        this.type = (type != null) ? Monster.valueOf(type) : null;
        this.element = (element != null) ? Element.valueOf(element) : null;
        this.minimumDamage = minimumDamage;
    }

    public static Trade fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Trade.class);
    }



    @Override
    public String toJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Trade{").append("Id: ").append(id).append(", ")
                .append("CardToTrade: ").append(cardToTrade).append(", ")
                .append("PostedBy: ").append(postedBy).append(", ")
                .append("Type: ").append(type).append(", ")
                .append("Element: ").append(element).append(", ")
                .append("MinimumDamage: ").append(minimumDamage);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toFancyString() {
        StringBuilder sb = new StringBuilder();
        sb.append("####################################\n");
        sb.append("#                                  #\n");
        sb.append("#     Trade Information             #\n");
        sb.append("#                                  #\n");
        sb.append("####################################\n");
        sb.append("Id: ").append(id).append("\n")
                .append("CardToTrade: ").append(cardToTrade).append("\n")
                .append("PostedBy: ").append(postedBy).append("\n")
                .append("Type: ").append(type).append("\n")
                .append("Element: ").append(element).append("\n")
                .append("MinimumDamage: ").append(minimumDamage);
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardToTrade() {
        return cardToTrade;
    }

    public void setCardToTrade(String cardToTrade) {
        this.cardToTrade = cardToTrade;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public Monster getType() {
        return type;
    }

    public void setType(Monster type) {
        this.type = type;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Integer getMinimumDamage() {
        return minimumDamage;
    }

    public void setMinimumDamage(Integer minimumDamage) {
        this.minimumDamage = minimumDamage;
    }
}
