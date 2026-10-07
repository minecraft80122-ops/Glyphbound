import greenfoot.*;

/**
 * An enemy on the battle screen. Draws its own body, intent, health and block,
 * and follows the attack pattern given by its EnemyType.
 */
public class BattleEnemy extends Actor
{
    private static final int W = 120;
    private static final int H = 160;

    private final EnemyType type;
    private int hp;
    private int block = 0;
    private int patternIndex;

    private int flash = 0;      // frames left of the red "hit" flash
    private int lunge = 0;      // frames left of the attack lunge
    private int dying = 0;      // frames left of the fade-out
    private int homeX = -1;
    private boolean highlight = false;
    private boolean dirty = true;

    public BattleEnemy(EnemyType type, int startIndex)
    {
        this.type = type;
        this.hp = type.maxHp;
        this.patternIndex = startIndex;
        redraw();
    }

    public void act()
    {
        if (homeX < 0) homeX = getX();

        if (lunge > 0)
        {
            lunge--;
            int step = lunge > 6 ? 12 - lunge : lunge;
            setLocation(homeX - step * 6, getY());
        }
        if (flash > 0)
        {
            flash--;
            dirty = true;
        }
        if (dirty) redraw();

        if (dying > 0)
        {
            dying--;
            getImage().setTransparency(dying * 255 / 20);
            if (dying == 0)
            {
                getWorld().removeObject(this);
            }
        }
    }

    public boolean isDead()
    {
        return hp <= 0;
    }

    public boolean hit(int mx, int my)
    {
        return Math.abs(mx - getX()) <= W / 2 && Math.abs(my - getY()) <= H / 2;
    }

    public void setHighlight(boolean highlight)
    {
        if (this.highlight != highlight)
        {
            this.highlight = highlight;
            dirty = true;
        }
    }

    /** Returns the damage that actually got through the enemy's block. */
    public int takeDamage(int amount)
    {
        int absorbed = Math.min(block, amount);
        block -= absorbed;
        int dealt = amount - absorbed;
        hp -= dealt;
        if (dealt > 0) flash = 8;
        if (hp <= 0 && dying == 0) dying = 20;
        dirty = true;
        return dealt;
    }

    /** Carry out the current intent, then move on to the next one in the pattern. */
    public void takeTurn(BattleWorld world)
    {
        block = 0;   // block from last round runs out now
        char c = currentIntent();
        if (c == 'D')
        {
            block += type.defense;
            world.spawnText("+" + type.defense + " Block", getX(), getY() - 70, new Color(120, 190, 255));
        }
        else
        {
            lunge = 12;
            world.damagePlayer(intentDamage());
        }
        patternIndex++;
        dirty = true;
    }

    private char currentIntent()
    {
        return type.pattern.charAt(patternIndex % type.pattern.length());
    }

    private int intentDamage()
    {
        char c = currentIntent();
        if (c == 'A') return type.attack;
        if (c == 'H') return type.attack * 2;
        return 0;
    }

    private void redraw()
    {
        dirty = false;
        GreenfootImage img = new GreenfootImage(W, H);

        if (highlight)
        {
            img.setColor(new Color(255, 220, 60, 80));
            img.fillRect(0, 0, W, H);
            img.setColor(new Color(255, 220, 60));
            img.drawRect(0, 0, W - 1, H - 1);
            img.drawRect(1, 1, W - 3, H - 3);
        }

        // intent
        if (hp > 0)
        {
            if (currentIntent() == 'D')
            {
                Gfx.text(img, "DEF " + type.defense, 18, new Color(120, 190, 255), W / 2, 5);
            }
            else
            {
                String s = "ATK " + intentDamage() + (currentIntent() == 'H' ? "!" : "");
                Gfx.text(img, s, 18, new Color(255, 100, 100), W / 2, 5);
            }
        }

        drawBody(img);

        // block badge, health bar, name
        int barX = 28;
        if (block > 0)
        {
            img.setColor(new Color(90, 150, 230));
            img.fillOval(2, 123, 22, 22);
            img.setColor(Color.WHITE);
            img.drawOval(2, 123, 22, 22);
            Gfx.text(img, "" + block, 12, Color.WHITE, 13, 127);
        }
        Gfx.bar(img, barX, 128, 88, 10, Math.max(hp, 0), type.maxHp, new Color(220, 60, 60));
        Gfx.text(img, type.displayName + "  " + Math.max(hp, 0) + "/" + type.maxHp, 13, Color.WHITE, W / 2, 142);

        setImage(img);
    }

    private void drawBody(GreenfootImage img)
    {
        Color main = flash > 0 ? new Color(255, 110, 110) : type.color;

        switch (type)
        {
            case SLIME:
                img.setColor(main);
                img.fillOval(15, 62, 90, 60);
                img.fillOval(28, 46, 64, 52);
                img.setColor(Color.WHITE);
                img.fillOval(40, 66, 16, 16);
                img.fillOval(66, 66, 16, 16);
                img.setColor(Color.BLACK);
                img.fillOval(46, 71, 7, 7);
                img.fillOval(72, 71, 7, 7);
                break;

            case GOBLIN:
                img.setColor(main);
                img.fillPolygon(new int[] { 38, 8, 40 }, new int[] { 58, 44, 74 }, 3);      // left ear
                img.fillPolygon(new int[] { 82, 112, 80 }, new int[] { 58, 44, 74 }, 3);    // right ear
                img.fillOval(34, 36, 52, 48);                                                // head
                img.setColor(new Color(110, 70, 40));
                img.fillRect(38, 86, 44, 38);                                                // tunic
                img.setColor(new Color(250, 220, 60));
                img.fillOval(45, 54, 10, 10);
                img.fillOval(65, 54, 10, 10);
                img.setColor(Color.BLACK);
                img.fillRect(50, 72, 20, 3);
                break;

            case SKELETON:
                img.setColor(main);
                img.fillOval(36, 32, 48, 44);                                                // skull
                img.fillRect(46, 72, 28, 12);                                                // jaw
                img.fillRect(58, 86, 4, 38);                                                 // spine
                for (int i = 0; i < 3; i++) img.fillRect(42, 92 + i * 10, 36, 4);            // ribs
                img.setColor(Color.BLACK);
                img.fillOval(44, 48, 12, 14);
                img.fillOval(64, 48, 12, 14);
                for (int i = 0; i < 4; i++) img.fillRect(50 + i * 6, 74, 2, 9);              // teeth gaps
                break;
        }
    }
}
