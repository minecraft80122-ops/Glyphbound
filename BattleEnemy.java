import greenfoot.*;

/**
 * BattleEnemy - an enemy on the card-battle screen. It draws its own body, its next
 * move (the "intent"), health, block, boss shield and the yellow MARK badge, and
 * follows the attack pattern given by its EnemyType.
 *
 * Damage goes through three layers, in this order:
 *   1. BLOCK  (from its own Defend moves)  2. SHIELD (bosses only)  3. HEALTH
 */
public class BattleEnemy extends Actor {

    private final EnemyType type;
    private final int W, H;                 // bosses get a bigger picture

    private int hp;
    private int block = 0;
    private int shield;
    private int patternIndex;
    private boolean marked = false;         // marked by Raden: Dyah's next arrow becomes a KOMBO
    private boolean stunned = false;        // skips its next action
    private int lastShieldDamage = 0;       // lets BattleWorld show the right floating text

    private int flash = 0;      // frames left of the red "hit" flash
    private int lunge = 0;      // frames left of the attack lunge
    private int dying = 0;      // frames left of the fade-out
    private int homeX = -1;
    private boolean highlight = false;
    private boolean dirty = true;

    public BattleEnemy(EnemyType type, int startIndex) {
        this.type = type;
        this.W = type.boss ? 170 : 120;
        this.H = type.boss ? 200 : 160;
        this.hp = type.maxHp;
        this.shield = type.shield;
        this.patternIndex = startIndex;
        redraw();
    }

    public void act() {
        if (homeX < 0) homeX = getX();

        if (lunge > 0) {
            lunge--;
            int step = lunge > 6 ? 12 - lunge : lunge;
            setLocation(homeX - step * 6, getY());
        }
        if (flash > 0) {
            flash--;
            dirty = true;
        }
        if (dirty) redraw();

        if (dying > 0) {
            dying--;
            getImage().setTransparency(dying * 255 / 20);
            if (dying == 0) {
                getWorld().removeObject(this);
            }
        }
    }

    // ------------------------------------------------------------------ queries
    public boolean isDead() { return hp <= 0; }

    public boolean isMarked() { return marked; }

    public int getLastShieldDamage() { return lastShieldDamage; }

    public boolean isBoss() { return type.boss; }

    public boolean hit(int mx, int my) {
        return Math.abs(mx - getX()) <= W / 2 && Math.abs(my - getY()) <= H / 2;
    }

    // ------------------------------------------------------------------ changes
    public void setHighlight(boolean highlight) {
        if (this.highlight != highlight) {
            this.highlight = highlight;
            dirty = true;
        }
    }

    public void setMarked(boolean marked) {
        if (this.marked != marked) {
            this.marked = marked;
            dirty = true;
        }
    }

    /** Make the enemy skip its next action (used by the Bayangan glyph). */
    public void stun() {
        stunned = true;
        dirty = true;
    }

    /**
     * @param strong true for KOMBO hits and glyph powers: they hit a boss shield at full strength
     * @return the damage that actually reached the HEALTH (after block and shield)
     */
    public int takeDamage(int amount, boolean strong) {
        lastShieldDamage = 0;

        int absorbed = Math.min(block, amount);              // layer 1: block
        block -= absorbed;
        int remaining = amount - absorbed;

        if (shield > 0 && remaining > 0) {                   // layer 2: boss shield
            int shieldDamage = strong ? remaining : Math.max(1, remaining / 4);
            shield -= shieldDamage;
            lastShieldDamage = shieldDamage;
            remaining = 0;
            flash = 8;
            if (shield <= 0) {
                shield = 0;
                stunned = true;                              // a broken shield leaves the boss dazed
                ((BattleWorld) getWorld()).spawnText("PERISAI HANCUR!", getX(), getY() - 100, new Color(255, 230, 80), 28);
            }
        }

        hp -= remaining;                                     // layer 3: health
        if (remaining > 0) flash = 8;
        if (hp <= 0 && dying == 0) dying = 20;
        dirty = true;
        return remaining;
    }

    /** Carry out the current intent, then move on to the next one in the pattern. */
    public void takeTurn(BattleWorld world) {
        block = 0;                                           // block from last round runs out now
        if (stunned) {
            stunned = false;
            world.spawnText("Pingsan!", getX(), getY() - 70, new Color(200, 200, 255));
            dirty = true;
            return;                                          // turn skipped, pattern does NOT advance
        }
        char c = currentIntent();
        if (c == 'D') {
            block += type.defense;
            world.spawnText("+" + type.defense + " Block", getX(), getY() - 70, new Color(120, 190, 255));
        } else {
            lunge = 12;
            world.damagePlayer(intentDamage());
        }
        patternIndex++;
        dirty = true;
    }

    private char currentIntent() {
        return type.pattern.charAt(patternIndex % type.pattern.length());
    }

    private int intentDamage() {
        char c = currentIntent();
        if (c == 'A') return type.attack;
        if (c == 'H') return type.attack * 2;
        return 0;
    }

    // ------------------------------------------------------------------ drawing
    private void redraw() {
        dirty = false;
        GreenfootImage img = new GreenfootImage(W, H);

        if (highlight) {
            img.setColor(new Color(255, 220, 60, 80));
            img.fillRect(0, 0, W, H);
            img.setColor(new Color(255, 220, 60));
            img.drawRect(0, 0, W - 1, H - 1);
            img.drawRect(1, 1, W - 3, H - 3);
        }

        // intent (what it will do next turn)
        if (hp > 0) {
            if (stunned) {
                Gfx.text(img, "PINGSAN", 18, new Color(200, 200, 255), W / 2, 5);
            } else if (currentIntent() == 'D') {
                Gfx.text(img, "DEF " + type.defense, 18, new Color(120, 190, 255), W / 2, 5);
            } else {
                String s = "ATK " + intentDamage() + (currentIntent() == 'H' ? "!" : "");
                Gfx.text(img, s, 18, new Color(255, 100, 100), W / 2, 5);
            }
        }

        drawBody(img);

        if (marked) {                                        // yellow crosshair badge = "marked"
            img.setColor(new Color(255, 230, 60));
            img.drawOval(W - 30, 30, 22, 22);
            img.drawOval(W - 29, 31, 20, 20);
            img.drawLine(W - 19, 26, W - 19, 56);
            img.drawLine(W - 34, 41, W - 4, 41);
            Gfx.text(img, "TANDA", 11, new Color(255, 230, 60), W - 19, 56);
        }

        // block badge, boss shield, health bar, name
        if (block > 0) {
            img.setColor(new Color(90, 150, 230));
            img.fillOval(2, H - 37, 22, 22);
            img.setColor(Color.WHITE);
            img.drawOval(2, H - 37, 22, 22);
            Gfx.text(img, "" + block, 12, Color.WHITE, 13, H - 33);
        }
        int barW = W - 32;
        if (type.shield > 0 && shield > 0) {
            Gfx.bar(img, 16, H - 45, barW, 8, shield, type.shield, new Color(110, 190, 255));
        }
        Gfx.bar(img, 16, H - 32, barW, 10, Math.max(hp, 0), type.maxHp, new Color(220, 60, 60));
        Gfx.text(img, type.displayName + "  " + Math.max(hp, 0) + "/" + type.maxHp, 13, Color.WHITE, W / 2, H - 18);

        setImage(img);
    }

    /** Draw the enemy picture (the code-drawn sprites from Sprites.java, enlarged). */
    private void drawBody(GreenfootImage img) {
        GreenfootImage sprite;
        double scale;
        switch (type) {
            case PRAJURIT:   sprite = Sprites.soldier();   scale = 1.7; break;
            case PENEMBAK:   sprite = Sprites.musketeer(); scale = 1.7; break;
            case NAGA_TIRTA: sprite = Sprites.naga();      scale = 1.4; break;
            case GARUDA:     sprite = Sprites.garuda();    scale = 1.45; break;
            default:         sprite = Sprites.macan();     scale = 1.55; break;
        }
        int w = (int) (sprite.getWidth() * scale);
        int h = (int) (sprite.getHeight() * scale);
        sprite.scale(w, h);
        int x = (W - w) / 2;
        int y = 32 + (H - 32 - 50 - h) / 2;                 // centred between the intent text and the bars
        img.drawImage(sprite, x, y);

        if (flash > 0) {                                     // red "I was hit" flash over the body
            img.setColor(new Color(255, 60, 60, 110));
            img.fillOval(x, y, w, h);
        }
    }
}
