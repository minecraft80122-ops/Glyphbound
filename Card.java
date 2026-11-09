import greenfoot.*;
import java.util.*;

/**
 * Card - the DATA of one card (it is not an Actor; CardActor is what shows it on screen).
 *
 * Every card belongs to a hero (the "owner"):
 *   RADEN cards: sword and shield. Attacks MARK the enemy they hit.
 *   DYAH  cards: arrows and herbs. An attack on a MARKED enemy is a KOMBO (damage x2).
 *
 * To add a new card: write one more factory method below (a "static factory", like
 * strike() in the original prototype) and add it to REWARD_POOL if it should appear as a reward.
 */
public class Card {

    public enum Type { ATTACK, SKILL }
    public enum Owner { RADEN, DYAH }

    /** Damage multiplier when Dyah hits an enemy that Raden has marked. */
    public static final int COMBO_MULTIPLIER = 2;

    public final Owner owner;
    public final String name;
    public final int cost;
    public final int damage;
    public final int block;
    public final int heal;
    public final int draw;
    public final boolean allEnemies;
    public final boolean mark;          // marks the enemies it hits
    public final Type type;
    public final String[] lines;        // description text, one entry per line

    private Card(Owner owner, String name, int cost, int damage, int block, int heal, int draw,
                 boolean allEnemies, boolean mark) {
        this.owner = owner;
        this.name = name;
        this.cost = cost;
        this.damage = damage;
        this.block = block;
        this.heal = heal;
        this.draw = draw;
        this.allEnemies = allEnemies;
        this.mark = mark;
        this.type = damage > 0 ? Type.ATTACK : Type.SKILL;

        // Build the description automatically from the numbers.
        ArrayList<String> text = new ArrayList<String>();
        if (damage > 0) {
            text.add(allEnemies ? "Serang " + damage + " dmg" : "Serang " + damage + " dmg.");
            if (allEnemies) text.add("ke SEMUA musuh.");
        }
        if (mark) text.add("Tandai musuh.");
        if (damage > 0 && owner == Owner.DYAH) text.add("KOMBO x2 bila bertanda");
        if (block > 0) text.add("Dapat " + block + " Block.");
        if (heal > 0) text.add("Pulih " + heal + " HP.");
        if (draw > 0) text.add("Tarik " + draw + " kartu.");
        this.lines = text.toArray(new String[0]);
    }

    /** Single-target attacks need the player to click an enemy. */
    public boolean needsTarget() {
        return damage > 0 && !allEnemies;
    }

    //                                              owner         name               cost dmg blk heal draw all   mark
    // ---- Raden: sword & shield ----
    public static Card tebas()         { return new Card(Owner.RADEN, "Tebas",           1, 6,  0, 0, 0, false, true);  }
    public static Card tebasanCepat()  { return new Card(Owner.RADEN, "Tebasan Cepat",   0, 3,  0, 0, 0, false, true);  }
    public static Card hantamBerat()   { return new Card(Owner.RADEN, "Hantam Berat",    2, 12, 0, 0, 0, false, true);  }
    public static Card sapuanPedang()  { return new Card(Owner.RADEN, "Sapuan Pedang",   2, 5,  0, 0, 0, true,  true);  }
    public static Card tangkis()       { return new Card(Owner.RADEN, "Tangkis",         1, 0,  5, 0, 0, false, false); }
    public static Card bantingPerisai(){ return new Card(Owner.RADEN, "Banting Perisai", 1, 4,  4, 0, 0, false, false); }
    // ---- Dyah: bow & herbs ----
    public static Card panah()         { return new Card(Owner.DYAH,  "Panah",           1, 5,  0, 0, 0, false, false); }
    public static Card panahTepat()    { return new Card(Owner.DYAH,  "Panah Tepat",     2, 10, 0, 0, 0, false, false); }
    public static Card hujanPanah()    { return new Card(Owner.DYAH,  "Hujan Panah",     2, 4,  0, 0, 0, true,  false); }
    public static Card jamu()          { return new Card(Owner.DYAH,  "Jamu",            1, 0,  0, 6, 0, false, false); }
    public static Card bidik()         { return new Card(Owner.DYAH,  "Bidik",           1, 0,  0, 0, 2, false, false); }

    /** Three different cards offered after a victory (the player picks one). */
    public static ArrayList<Card> rewardChoices() {
        ArrayList<Card> pool = new ArrayList<Card>();
        pool.add(tebasanCepat()); pool.add(hantamBerat()); pool.add(sapuanPedang());
        pool.add(bantingPerisai()); pool.add(panahTepat()); pool.add(hujanPanah());
        pool.add(jamu()); pool.add(bidik()); pool.add(tebas()); pool.add(panah()); pool.add(tangkis());
        Collections.shuffle(pool);
        return new ArrayList<Card>(pool.subList(0, 3));
    }
}
