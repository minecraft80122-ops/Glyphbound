import greenfoot.*;

/**
 * EnemyType - an ENUM holding the stats of every kind of enemy (data only).
 *
 * Attack pattern letters (repeats forever, the next move is shown above the enemy):
 *   A = attack     H = heavy attack (double damage)     D = defend (gain block)
 *
 * Bosses also have a SHIELD: normal hits only chip it (25%), but KOMBO hits and
 * glyph powers hit it at full strength. When it breaks, the boss is stunned for a turn.
 * (These numbers were tuned with a quick simulation: change them to make the game easier/harder.)
 */
public enum EnemyType {

    //          name              hp  atk def pattern  shield boss
    PRAJURIT   ("Prajurit",       24, 7,  5,  "AAD",   0,     false),
    PENEMBAK   ("Penembak",       16, 6,  0,  "AAH",   0,     false),
    NAGA_TIRTA ("Naga Tirta",     100, 14, 8, "AAHD",  28,    true),
    GARUDA     ("Garuda Surya",   84, 10, 5,  "AHAD",  24,    true),
    MACAN      ("Macan Bayangan", 110, 12, 8, "AAHD",  32,    true);

    public final String displayName;
    public final int maxHp;
    public final int attack;
    public final int defense;
    public final String pattern;
    public final int shield;
    public final boolean boss;

    private EnemyType(String displayName, int maxHp, int attack, int defense, String pattern,
                      int shield, boolean boss) {
        this.displayName = displayName;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.pattern = pattern;
        this.shield = shield;
        this.boss = boss;
    }
}
