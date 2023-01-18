import Server.Misc.Finetuners;
import Server.Models.Cards.Card;
import Server.Models.Cards.Element;
import Server.Models.Cards.Monster;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CardTests {

    @Test
    public void testInferElementFromName() {
        Card card = new Card("2", "Water Elemental", 5f, null, null, null, null);
        assertEquals(Element.WATER, card.getElement());

        card = new Card("4", "Normal Card", 6f, null, null, null, null);
        assertEquals(Element.NORMAL, card.getElement());

        card = new Card("3", "Lightning Golem", 8f, null, null, null, null);
        assertEquals(Element.NORMAL, card.getElement());
    }

    @Test
    public void testInferMonsterFromName() {
        Card card = new Card("5", "Fire Dragon", 10f, 20f, null, null, null);
        assertEquals(Monster.DRAGON, card.getMonster());

        card = new Card("6", "Water Spell", 5f, null, null, null, null);
        assertEquals(Monster.SPELL, card.getMonster());

        card = new Card("7", "Earth Golem", 8f, null, null, null, null);
        assertEquals(Monster.SPELL, card.getMonster());
    }

    @Test
    public void testHealthCalculation() {
        float damage = 10f;

        Card card = new Card("8", "Fire Dragon", damage, null, null, null, null);
        assertEquals(damage * Finetuners.UNSET_HEALTH_MULTIPLIER, card.getHealth(), 0);
    }

    @Test
    public void testCriticalChanceCalculation() {
        float damage = 10f;

        Card card = new Card("9", "Fire Dragon", damage, 20f, null, null, null);
        assertEquals(damage / (1 + damage * Finetuners.UNSET_CRITICAL_MULTIPLIER), card.getCriticalChance(), 0);
    }

    @Test
    public void testFromJson() throws Exception {
        String json = "{\"Id\":\"10\",\"Name\":\"Ice Wizard\",\"Damage\":12,\"Health\":24,\"Element\":\"NORMAL\",\"Monster\":\"WIZARD\",\"CriticalChance\":0.18}";
        Card card = Card.fromJson(json);
        assertEquals("10", card.getId());
        assertEquals("Ice Wizard", card.getName());
        assertEquals(12, card.getDamage(), 0);
        assertEquals(24, card.getHealth(), 0);
        assertEquals(Element.NORMAL, card.getElement());
        assertEquals(Monster.WIZARD, card.getMonster());
        assertEquals(0.18f, card.getCriticalChance(), 0);
    }

    @Test
    public void testToJSON() throws JsonProcessingException {
        Card testCard = new Card("123", "Test Card", 10f, 20f, Element.WATER, Monster.DRAGON, 15f);
        String expectedJSON = "{\"Id\":\"123\",\"Name\":\"Test Card\",\"Damage\":10.0,\"Health\":20.0,\"Element\":\"WATER\",\"Monster\":\"DRAGON\",\"CriticalChance\":15.0,\"spell\":false}";
        assertEquals(expectedJSON, testCard.toJSON());
    }

    @Test
    public void testToFancyString() {
        Card testCard = new Card("123", "Test Card", 10f, 20f, Element.WATER, Monster.DRAGON, 15f);
        String expectedFancyString =
                "####################################\n" +
                "#                                  #\n" +
                "#     Card Information             #\n" +
                "#                                  #\n" +
                "####################################\n" +
                "Id: 123\n" +
                "Name: Test Card\n" +
                "Damage: 10.0\n" +
                "Health: 20.0\n" +
                "Element: WATER\n" +
                "Monster: DRAGON\n" +
                "CriticalChance: 15.0\n"
        ;

        assertEquals(expectedFancyString, testCard.toFancyString());
    }

}
