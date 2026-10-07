import greenfoot.*;
import java.util.*;

/**
 * A card's data (not an Actor). CardActor is what actually shows it on screen.
 * To add a new card: add a factory method below and (optionally) add it to the reward pool.
 */
public class Card
{
    public enum Type { ATTACK, SKILL }

    public final String name;
    public final int cost;
    public final int damage;
    public final int block;
    public final int heal;
    public final int draw;
    public final boolean allEnemies;
    public final Type type;
    public final String[] lines;   // description text, one entry per line

    private Card(String name, int cost, int damage, int block, int heal, int draw, boolean allEnemies)
    {
        this.name = name;
        this.cost = cost;
        this.damage = damage;
        this.block = block;
        this.heal = heal;
        this.draw = draw;
        this.allEnemies = allEnemies;
        this.type = damage > 0 ? Type.ATTACK : Type.SKILL;

        ArrayList<String> text = new ArrayList<String>();
        if (damage > 0)
        {
            if (allEnemies)
            {
                text.add("Deal " + damage + " damage");
                text.add("to ALL enemies.");
            }
            else
            {
                text.add("Deal " + damage + " damage.");
            }
        }
        if (block > 0) text.add("Gain " + block + " Block.");
        if (heal > 0)  text.add("Heal " + heal + " HP.");
        if (draw > 0)  text.add("Draw " + draw + " cards.");
        this.lines = text.toArray(new String[0]);
    }

    /** Attack cards that hit one enemy need the player to pick a target. */
    public boolean needsTarget()
    {
        return damage > 0 && !allEnemies;
    }

    //                                            name          cost dmg blk heal draw all
    public static Card strike()      { return new Card("Strike",       1, 6,  0, 0, 0, false); }
    public static Card defend()      { return new Card("Defend",       1, 0,  5, 0, 0, false); }
    public static Card heavyBlow()   { return new Card("Heavy Blow",   2, 12, 0, 0, 0, false); }
    public static Card quickSlash()  { return new Card("Quick Slash",  0, 3,  0, 0, 0, false); }
    public static Card fireball()    { return new Card("Fireball",     2, 7,  0, 0, 0, true);  }
    public static Card shieldBash()  { return new Card("Shield Bash",  1, 4,  4, 0, 0, false); }
    public static Card bandage()     { return new Card("Bandage",      1, 0,  0, 6, 0, false); }
    public static Card insight()     { return new Card("Insight",      1, 0,  0, 0, 2, false); }

    /** A random card to give the player after winning a fight. */
    public static Card randomReward()
    {
        Card[] pool = { quickSlash(), heavyBlow(), fireball(), shieldBash(), bandage(), insight(), strike(), defend() };
        return pool[Greenfoot.getRandomNumber(pool.length)];
    }
}
