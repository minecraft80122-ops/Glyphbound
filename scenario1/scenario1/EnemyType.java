import greenfoot.*;

/**
 * The kinds of enemy. Each has stats and an attack pattern.
 * Pattern letters: A = attack, H = heavy attack (double damage), D = defend (gain block).
 * The pattern repeats forever, and the enemy shows what it will do next above its head.
 */
public enum EnemyType
{
    SLIME   ("Slime",    14, 4, 0, "AAH", new Color(80, 200, 90)),
    GOBLIN  ("Goblin",   20, 5, 5, "AAD", new Color(150, 175, 60)),
    SKELETON("Skeleton", 26, 6, 7, "ADH", new Color(225, 225, 205));

    public final String displayName;
    public final int maxHp;
    public final int attack;
    public final int defense;
    public final String pattern;
    public final Color color;

    private EnemyType(String displayName, int maxHp, int attack, int defense, String pattern, Color color)
    {
        this.displayName = displayName;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.pattern = pattern;
        this.color = color;
    }
}
