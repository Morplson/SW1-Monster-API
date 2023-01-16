package Server.Models.Cards.Effects;

import java.util.Random;

public class Burning extends StatusEffect{


    public Burning(Float value, int lifetime) {
        super(value, lifetime);
    }

    @Override
    public float iterateValue(){
        float tempValue = super.iterateValue();

        tempValue = tempValue - tempValue * ( 1 / ( (super.getLifetime() > 0) ? super.getLifetime() : 1) );

        return tempValue;
    }
}
