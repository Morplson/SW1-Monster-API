package Server.Models;

import Server.Models.Cards.Card;
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
public class User implements Serializable {


    @JsonProperty(value = "Username", required = true)
    private String username = null;
    @JsonProperty(value = "Password", required = true)
    private String password = null;

    @JsonProperty(value = "Coins")
    private Integer coins = null;
    @JsonProperty(value = "Name")
    private String name = null;
    @JsonProperty(value = "Bio")
    private String bio = null;
    @JsonProperty(value = "Image")
    private String image = null;
    @JsonProperty(value = "EloValue")
    private Integer eloValue = null;

    @JsonProperty(value = "Stack")
    ArrayList<Card> stack = new ArrayList<Card>();
    @JsonProperty(value = "Deck")
    ArrayList<Card> deck = new ArrayList<Card>();

    @JsonCreator
    public User(@JsonProperty(value = "Username") String username,
                @JsonProperty(value = "Password") String password,
                @JsonProperty(value = "Coins") Integer coins,
                @JsonProperty(value = "Name") String name,
                @JsonProperty(value = "Bio") String bio,
                @JsonProperty(value = "Image") String image,
                @JsonProperty(value = "EloValue") Integer elo_value,
                @JsonProperty(value = "Stack") ArrayList<Card> stack,
                @JsonProperty(value = "Deck") ArrayList<Card> deck) {
        this.username = username;
        this.password = password;
        this.coins = coins;
        this.name = name;
        this.bio = bio;
        this.image = image;
        this.eloValue = elo_value;
        this.stack = stack;
        this.deck = deck;
    }

    public static User fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, User.class);
    }



    @Override
    public String toJSON() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User{");
        sb.append("username='").append(username).append("'");
        sb.append(", password='").append(password).append("'");
        sb.append(", coins=").append(coins);
        sb.append(", name='").append(name).append("'");
        sb.append(", bio='").append(bio).append("'");
        sb.append(", image='").append(image).append("'");
        sb.append(", elo_value=").append(eloValue);
        sb.append(", stack=").append(stack.toString());
        sb.append(", deck=").append(deck.toString());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toFancyString() {
        StringBuilder sb = new StringBuilder();
        sb.append("####################################\n");
        sb.append("#                                  #\n");
        sb.append("#     User Information             #\n");
        sb.append("#                                  #\n");
        sb.append("####################################\n");
        sb.append("Username: " + username + "\n");
        sb.append("Password: " + password + "\n");
        sb.append("Coins: " + coins + "\n");
        sb.append("Name: " + name + "\n");
        sb.append("Bio: " + bio + "\n");
        sb.append("Image: " + image + "\n");
        sb.append("eloValue: " + eloValue + "\n");
        sb.append("Stack: \n");
        sb.append("[\n");
        for (Card card : stack) {
            sb.append("    " + card.toString() + "\n");
        }
        sb.append("]\n");
        sb.append("Deck: \n");
        sb.append("[\n");
        for (Card card : deck) {
            sb.append("    " + card.toString() + "\n");
        }
        sb.append("]\n");
        return sb.toString();
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public void setEloValue(Integer eloValue) {
        this.eloValue = eloValue;
    }

    public ArrayList<Card> getStack() {
        return stack;
    }

    public void setStack(ArrayList<Card> stack) {
        this.stack = stack;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getEloValue() {
        return eloValue;
    }

    public void setEloValue(int elo_value) {
        this.eloValue = elo_value;
    }
}
