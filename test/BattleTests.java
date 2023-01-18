import Server.Models.Cards.*;
import Server.Models.Cards.Effects.Healing;
import Server.Models.Cards.Effects.StatusEffect;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BattleTests {

    Card testCard;
    CardWrapper wrapper;

    @Before
    public void setup() {
        this.testCard = new Card("testID", "TestCard", 25f, 100f, Element.NORMAL, Monster.KNIGHT, 0f);
        this.wrapper = new CardWrapper(testCard);
    }

    /**
     *     Test that the constructor correctly sets the host card and the virtual health
     */
    @Test
    public void testConstructor() {
        assertEquals(testCard, wrapper.getHost());
        assertEquals(100, wrapper.getVirtualHealth(), 0);
    }

    /**
     * Test that the addStatusEffect method correctly adds a status effect to the statusEffects list
     */
    @Test
    public void testAddStatusEffect() {
        StatusEffect testEffect = new Healing(5f, 3);
        wrapper.addStatusEffect(testEffect);
        assertTrue(wrapper.getStatusEffects().contains(testEffect));
    }

    @Test
    public void testAttackStringNormalHit() {
        Card opponent = new Card("opponentID", "OpponentCard", 50f, 200f, Element.NORMAL, Monster.DRAGON, 0.5f);

        Rules testRules = new Rules(wrapper.getHost(), opponent);
        String result = wrapper.attackString("Opponent", testRules);
        assertEquals("TestCard: Attack on Opponent", result);
    }

    @Test
    public void testAttackStringCriticalHit() {
        Card opponent = new Card("opponentID", "OpponentCard", 50f, 200f, Element.NORMAL, Monster.DRAGON, 0.5f);

        wrapper.setVirtualCritical(true);
        Rules testRules = new Rules(wrapper.getHost(), opponent);
        String result = wrapper.attackString("Opponent", testRules);
        assertEquals("TestCard: Critical hit on Opponent", result);
    }

    @Test
    public void testResetVirtualHealth() {
        wrapper.setVirtualHealth(50);
        wrapper.resetVirtualHealth();
        assertEquals(100, wrapper.getVirtualHealth(), 0);
    }

    @Test
    public void testCalculateVirtualHealth() {
        float result = wrapper.calculateVirtualHealth(10);
        assertEquals(90, result, 0);
    }

    @Test
    public void testCalculateVirtualHealthStatusEffects() {
        wrapper.addStatusEffect(new Healing(10f, 5));

        float result = wrapper.calculateVirtualHealth(90);
        assertEquals(20, result, 0);
    }

    @Test
    public void testStatusEffectsLifetime() {
        wrapper.addStatusEffect(new Healing(10f, 5));

        float result = wrapper.calculateVirtualHealth(0);
        assertEquals(110, result, 0);

        int lifetime = wrapper.getStatusEffects().get(0).getLifetime();
        assertEquals(4, lifetime, 0);
    }

    @Test
    public void testStatusEffectsIsActive() {

        wrapper.calculateVirtualHealth(0);

        wrapper.addStatusEffect(new Healing(10f, 5));

        float result = wrapper.calculateVirtualHealth(0);
        assertEquals(110, result, 0);

        wrapper.calculateVirtualHealth(0);
        wrapper.calculateVirtualHealth(0);
        wrapper.calculateVirtualHealth(0);
        assertEquals(1, wrapper.getStatusEffects().size());

        wrapper.calculateVirtualHealth(0);
        assertEquals(0, wrapper.getStatusEffects().size());
    }

    @Test
    public void testIsDead() {
        wrapper.setVirtualHealth(0);
        assertTrue(wrapper.isDead());
    }

    @Test
    public void testSetVirtualHealth() {
        wrapper.setVirtualHealth(50);
        assertEquals(50, wrapper.getVirtualHealth(), 0);
    }




}
