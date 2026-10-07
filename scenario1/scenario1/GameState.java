import java.util.*;

/**
 * Things that must survive between the dungeon and the battle screen:
 * the player's health and the deck.
 */
public class GameState
{
    public static int maxHp = 50;
    public static int hp = 50;
    public static ArrayList<Card> deck = new ArrayList<Card>();

    public static void reset()
    {
        maxHp = 50;
        hp = 50;
        deck.clear();
        for (int i = 0; i < 4; i++) deck.add(Card.strike());
        for (int i = 0; i < 3; i++) deck.add(Card.defend());
        deck.add(Card.heavyBlow());
        deck.add(Card.fireball());
        deck.add(Card.bandage());
    }

    /** Lets you start BattleWorld directly (right-click > new BattleWorld()) for testing. */
    public static void ensureInit()
    {
        if (deck.isEmpty()) reset();
    }
}
