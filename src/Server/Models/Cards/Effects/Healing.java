package Server.Models.Cards.Effects;

public class Healing extends StatusEffect {

    public Healing(Float value, int lifetime) {
        super(-value, lifetime);
    }
}
