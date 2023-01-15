package Server.Models.Cards.Effects;


public class Bleeding extends StatusEffect{


    public Bleeding(Float value, int lifetime) {
        super(value, lifetime);
    }

    @Override
    public float iterateValue(){
        float tempValue = super.iterateValue();

        tempValue = (float) (tempValue+(Math.pow(tempValue, Math.E)*Math.pow(Math.E, -super.getLifetime()) ));

        return tempValue;
    }
}
