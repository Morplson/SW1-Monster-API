package Server.Models.Cards.Effects;

public abstract class StatusEffect {
    public float value;
    private int lifetime;


    public StatusEffect(Float value, int lifetime){
        this.value = value;
        this.lifetime = lifetime;
    }

    public boolean isActive(){
        if(this.lifetime>0){
            return true;
        }
        return false;
    }

    public float iterateValue(){
        if(!this.isActive()){
            return 0;
        }
        lifetime--;

        return this.value;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }
}
