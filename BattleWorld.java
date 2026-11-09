import greenfoot.*;
import java.util.*;

/**
 * BattleWorld - the TURN-BASED CARD BATTLE (based on the Glyphbound card prototype).
 *
 * How a turn works:
 *   1. You get 3 energy and draw 5 cards from your deck.
 *   2. Click a card. Single-target attacks then need a click on an enemy;
 *      other cards play immediately.
 *   3. Click "End Turn". Every enemy does the move shown above its head.
 *
 * What is special in Legenda Demak:
 *   - RADEN's attack cards MARK the enemy they hit (yellow crosshair).
 *   - DYAH's attack cards on a MARKED enemy are a KOMBO: damage x2, and extra glyph charge.
 *   - Every card you play charges the GLYPH meter. When it is full, press the Glif button
 *     to solve a quick, timed puzzle (water flow / light mirror / shadow memory).
 *     Success unleashes the glyph power of this area.
 *   - Bosses have a SHIELD: plain hits only chip it. KOMBO hits and glyph powers break it.
 *   - Win = every enemy dead -> heal fully, pick 1 of 3 new cards for your deck.
 *   - Lose = party HP reaches 0.
 */
public class BattleWorld extends World {

    private static final int HAND_SIZE = 5;
    private static final int MAX_HAND = 7;
    private static final int ENERGY_PER_TURN = 3;
    private static final int GLYPH_MAX = 6;           // cards needed to fill the glyph meter
    private static final int HAND_Y = 505;
    private static final int RAISED_Y = 478;

    // The game is always in exactly one of these states. Using an enum makes the code
    // easy to read: "if (state == State.PLAYER_TURN)".
    private enum State { PLAYER_TURN, ENEMY_TURN, PUZZLE, REWARD, LOST }

    private int stageId;                  // used to retry this battle after a defeat
    private Element area;
    private int nextStage;

    private BattleHero raden, dyah;
    private Button endTurnButton, glyphButton;
    private GreenfootImage base;          // the finished background, copied again every time the HUD is redrawn

    private ArrayList<BattleEnemy> enemies = new ArrayList<BattleEnemy>();
    private ArrayList<CardActor> hand = new ArrayList<CardActor>();
    private ArrayList<CardActor> rewardCards = new ArrayList<CardActor>();
    private ArrayList<Card> drawPile;
    private ArrayList<Card> discardPile = new ArrayList<Card>();
    private CardActor selected = null;
    private GlyphPuzzle puzzle = null;

    private State state;
    private int energy, block, turn, timer, enemyIndex;
    private int glyphCharge = 0;
    private int comboCount = 0;
    private int mouseX = -1, mouseY = -1;
    private boolean dirty = true;
    private String banner = "";

    /** Quick test: right-click BattleWorld in Greenfoot > new BattleWorld(). */
    public BattleWorld() {
        this(GameState.STAGE_TIRTA_BATTLE, Element.AIR, GameState.STAGE_STORY_1);
    }

    public BattleWorld(int stageId, Element area, int nextStage) {
        super(800, 600, 1);                // this screen is taller than the others (cards need room)
        this.stageId = stageId;
        this.area = area;
        this.nextStage = nextStage;
        GameState.ensureDeck();

        setPaintOrder(FloatingText.class, GlyphPuzzle.class, CardActor.class, Button.class,
                GlyphBlast.class, BattleEnemy.class, BattleHero.class);

        buildBackground();

        raden = new BattleHero(true);
        addObject(raden, 110, 245);
        dyah = new BattleHero(false);
        addObject(dyah, 225, 262);

        spawnEnemies(groupFor(area));

        drawPile = new ArrayList<Card>(GameState.deck);
        Collections.shuffle(drawPile);

        endTurnButton = new Button("End Turn", 100, 36);
        addObject(endTurnButton, 735, 385);
        glyphButton = new Button("Glif 0/" + GLYPH_MAX, 100, 32);
        glyphButton.setAccent(area.getColor());
        glyphButton.setEnabled(false);
        addObject(glyphButton, 735, 340);

        startPlayerTurn();
        redrawHud();
    }

    // ------------------------------------------------------------------ setup
    private void buildBackground() {
        base = Scenery.create(area, 800, 600);                  // same scenery as the temple levels
        base.setColor(new Color(0, 0, 20, 90));
        base.fillRect(0, 0, 800, 600);
        base.setColor(new Color(0, 0, 0, 120));
        base.fillRect(0, 350, 800, 250);                        // dark band behind the cards
        base.setColor(new Color(160, 160, 170));
        base.drawLine(0, 350, 800, 350);
    }

    /** Which enemies appear in each area (minions first, the boss is always last = rightmost). */
    private EnemyType[] groupFor(Element e) {
        switch (e) {
            case AIR:   return new EnemyType[]{EnemyType.PRAJURIT, EnemyType.NAGA_TIRTA};
            case SURYA: return new EnemyType[]{EnemyType.PRAJURIT, EnemyType.PENEMBAK, EnemyType.GARUDA};
            default:    return new EnemyType[]{EnemyType.PENEMBAK, EnemyType.PENEMBAK, EnemyType.MACAN};
        }
    }

    private void spawnEnemies(EnemyType[] group) {
        int count = Math.max(1, Math.min(group.length, 3));
        int[][] layouts = {{600}, {440, 650}, {385, 505, 670}};
        int[] xs = layouts[count - 1];
        for (int i = 0; i < count; i++) {
            BattleEnemy e = new BattleEnemy(group[i], Greenfoot.getRandomNumber(group[i].pattern.length()));
            enemies.add(e);
            addObject(e, xs[i], group[i].boss ? 255 : 250);
        }
    }

    // ------------------------------------------------------------- main loop
    public void act() {
        MouseInfo mi = Greenfoot.getMouseInfo();
        if (mi != null) {
            mouseX = mi.getX();
            mouseY = mi.getY();
        }
        if (Greenfoot.mouseClicked(null) && mi != null) {
            handleClick(mi.getX(), mi.getY());
        }

        updateHover();

        if (state == State.ENEMY_TURN) {
            runEnemyTurn();
        }
        if (dirty) {
            redrawHud();
            dirty = false;
        }
    }

    private void handleClick(int mx, int my) {
        if (state == State.REWARD) {
            pickReward(mx, my);
            return;
        }
        if (state == State.LOST) {
            GameState.hp = GameState.maxHp;                     // revive for the retry
            Greenfoot.setWorld(new GameOverWorld(stageId));
            return;
        }
        if (state != State.PLAYER_TURN) return;                 // ignore clicks during enemy turn / puzzle

        if (endTurnButton.hit(mx, my)) {
            endPlayerTurn();
            return;
        }
        if (glyphButton.hit(mx, my) && glyphCharge >= GLYPH_MAX) {
            startPuzzle();
            return;
        }

        CardActor clicked = cardAt(mx, my);
        if (clicked != null) {
            Card c = clicked.getCard();
            if (clicked == selected) {
                deselect();
            } else if (c.cost > energy) {
                spawnText("Energi kurang", clicked.getX(), HAND_Y - 90, new Color(255, 200, 80));
            } else if (c.needsTarget()) {
                select(clicked);
            } else {
                playCard(clicked, null);
            }
            return;
        }

        if (selected != null) {
            BattleEnemy target = enemyAt(mx, my);
            if (target != null) {
                playCard(selected, target);
            } else {
                deselect();
            }
        }
    }

    private void updateHover() {
        if (state == State.REWARD) {                            // highlight the reward card under the mouse
            for (CardActor ca : rewardCards) {
                ca.setSelected(ca.hit(mouseX, mouseY));
            }
            return;
        }
        BattleEnemy hovered = null;
        if (state == State.PLAYER_TURN && selected != null) {
            hovered = enemyAt(mouseX, mouseY);
        }
        for (BattleEnemy e : enemies) {
            e.setHighlight(e == hovered);
        }
    }

    // ------------------------------------------------------------ hit testing
    private CardActor cardAt(int mx, int my) {
        CardActor best = null;
        for (CardActor ca : hand) {
            if (ca.hit(mx, my) && (best == null || Math.abs(mx - ca.getX()) < Math.abs(mx - best.getX()))) {
                best = ca;
            }
        }
        return best;
    }

    private BattleEnemy enemyAt(int mx, int my) {
        for (BattleEnemy e : enemies) {
            if (!e.isDead() && e.hit(mx, my)) return e;
        }
        return null;
    }

    // ------------------------------------------------------------------ turns
    private void startPlayerTurn() {
        state = State.PLAYER_TURN;
        turn++;
        energy = ENERGY_PER_TURN;
        block = 0;                                              // block does not carry over
        banner = "Giliranmu";
        endTurnButton.setEnabled(true);
        for (int i = 0; i < HAND_SIZE; i++) drawCard();
        layoutHand();
        refreshCards();
        updateGlyphButton();
        dirty = true;
    }

    private void endPlayerTurn() {
        deselect();
        discardHand();
        state = State.ENEMY_TURN;
        banner = "Giliran musuh";
        endTurnButton.setEnabled(false);
        glyphButton.setEnabled(false);
        enemyIndex = 0;
        timer = 25;
        dirty = true;
    }

    private void runEnemyTurn() {
        timer--;
        if (timer > 0) return;

        while (enemyIndex < enemies.size() && enemies.get(enemyIndex).isDead()) {
            enemyIndex++;
        }
        if (enemyIndex >= enemies.size()) {
            startPlayerTurn();
            return;
        }
        enemies.get(enemyIndex).takeTurn(this);
        enemyIndex++;
        timer = 35;
    }

    // ------------------------------------------------------------------ cards
    private void drawCard() {
        if (hand.size() >= MAX_HAND) return;
        if (drawPile.isEmpty()) {                               // reshuffle the discard pile into the draw pile
            drawPile.addAll(discardPile);
            discardPile.clear();
            Collections.shuffle(drawPile);
        }
        if (drawPile.isEmpty()) return;

        Card c = drawPile.remove(drawPile.size() - 1);
        CardActor ca = new CardActor(c);
        hand.add(ca);
        addObject(ca, 400, HAND_Y);
    }

    private void discardHand() {
        for (CardActor ca : hand) {
            discardPile.add(ca.getCard());
            removeObject(ca);
        }
        hand.clear();
        selected = null;
    }

    private void layoutHand() {
        int n = hand.size();
        if (n == 0) return;
        int spacing = Math.min(112, 700 / n);
        int startX = 400 - (n - 1) * spacing / 2;
        for (int i = 0; i < n; i++) {
            CardActor ca = hand.get(i);
            ca.setLocation(startX + i * spacing, ca == selected ? RAISED_Y : HAND_Y);
        }
    }

    private void refreshCards() {
        for (CardActor ca : hand) {
            ca.setPlayable(ca.getCard().cost <= energy);
        }
    }

    private void select(CardActor ca) {
        if (selected != null) selected.setSelected(false);
        selected = ca;
        ca.setSelected(true);
        banner = "Pilih target";
        layoutHand();
        dirty = true;
    }

    private void deselect() {
        if (selected != null) selected.setSelected(false);
        selected = null;
        if (state == State.PLAYER_TURN) banner = "Giliranmu";
        layoutHand();
        dirty = true;
    }

    /** The heart of the game: what happens when a card is played. */
    private void playCard(CardActor ca, BattleEnemy target) {
        Card c = ca.getCard();
        energy -= c.cost;

        // The hero who owns the card plays an animation.
        if (c.block > 0) raden.guard(70);
        else if (c.owner == Card.Owner.RADEN) raden.attack();
        else dyah.attack();

        if (c.damage > 0) {
            if (c.allEnemies) {
                for (BattleEnemy e : new ArrayList<BattleEnemy>(enemies)) {   // loop over a COPY of the list
                    if (!e.isDead()) strike(c, e);
                }
            } else {
                strike(c, target);
            }
        }
        if (c.block > 0) {
            block += c.block;
            spawnText("+" + c.block + " Block", 170, 150, new Color(120, 190, 255));
        }
        if (c.heal > 0) {
            int healed = Math.min(c.heal, GameState.maxHp - GameState.hp);
            GameState.hp += healed;
            spawnText("+" + healed + " HP", 170, 150, new Color(100, 230, 120));
        }

        hand.remove(ca);
        removeObject(ca);
        discardPile.add(c);
        selected = null;
        banner = "Giliranmu";

        addGlyphCharge(1);                                      // every card charges the glyph meter
        for (int i = 0; i < c.draw; i++) drawCard();

        layoutHand();
        refreshCards();
        updateGlyphButton();
        dirty = true;

        if (allEnemiesDead()) {
            win();
        }
    }

    /** One card hits one enemy: handles MARK and KOMBO. */
    private void strike(Card c, BattleEnemy e) {
        boolean combo = (c.owner == Card.Owner.DYAH) && e.isMarked();      // Dyah hitting a marked enemy
        int damage = combo ? c.damage * Card.COMBO_MULTIPLIER : c.damage;
        int x = e.getX();
        int y = e.getY();

        if (combo) {
            e.setMarked(false);                                 // the mark is used up
            comboCount++;
            addGlyphCharge(2);
            spawnText("KOMBO!", x, y - (e.isBoss() ? 120 : 90), new Color(255, 230, 60), 30);
        }
        dealDamage(e, damage, combo);

        if (c.mark && !e.isDead()) {                            // Raden's cards mark what they hit
            e.setMarked(true);
        }
    }

    /** Damage + the floating number. 'strong' = combo / glyph (full damage to boss shields). */
    private void dealDamage(BattleEnemy e, int damage, boolean strong) {
        int dealt = e.takeDamage(damage, strong);
        int shieldHit = e.getLastShieldDamage();
        int y = e.getY() - (e.isBoss() ? 105 : 60);
        if (dealt > 0) {
            spawnText("-" + dealt, e.getX(), y, new Color(255, 90, 90));
        } else if (shieldHit > 0) {
            spawnText("-" + shieldHit + " Perisai", e.getX(), y, new Color(120, 190, 255));
        } else {
            spawnText("Blocked", e.getX(), y, new Color(190, 190, 190));
        }
    }

    private void addGlyphCharge(int amount) {
        glyphCharge = Math.min(GLYPH_MAX, glyphCharge + amount);
    }

    private void updateGlyphButton() {
        if (glyphCharge >= GLYPH_MAX && state == State.PLAYER_TURN) {
            glyphButton.setLabel("GLIF SIAP!");
            glyphButton.setEnabled(true);
        } else {
            glyphButton.setLabel("Glif " + glyphCharge + "/" + GLYPH_MAX);
            glyphButton.setEnabled(false);
        }
    }

    // ---------------------------------------------------------- glyph puzzle
    private void startPuzzle() {
        deselect();
        state = State.PUZZLE;
        switch (area) {
            case AIR:   puzzle = new WaterFlowPuzzle();     break;
            case SURYA: puzzle = new LightMirrorPuzzle();   break;
            default:    puzzle = new ShadowMemoryPuzzle();  break;
        }
        addObject(puzzle, 400, 100);
        banner = "";
        glyphButton.setEnabled(false);
        dirty = true;
    }

    /** Called by the puzzle itself when it is solved (true) or time runs out / fails (false). */
    public void puzzleFinished(boolean success) {
        removeObject(puzzle);
        puzzle = null;
        state = State.PLAYER_TURN;
        banner = "Giliranmu";
        if (success) {
            glyphCharge = 0;
            unleashGlyph();
        } else {
            glyphCharge = glyphCharge / 2;                      // failing costs half the charge
            spawnText("Gagal!", 400, 160, new Color(255, 100, 100), 28);
        }
        updateGlyphButton();
        dirty = true;
        if (success && allEnemiesDead()) {
            win();
        }
    }

    /** The reward for solving the puzzle: a power that depends on the area's element. */
    private void unleashGlyph() {
        addObject(new GlyphBlast(area.getColor()), 400, 260);
        ArrayList<BattleEnemy> alive = new ArrayList<BattleEnemy>();
        for (BattleEnemy e : enemies) {
            if (!e.isDead()) alive.add(e);
        }
        switch (area) {
            case AIR: {                                         // water heals and protects
                int healed = Math.min(12, GameState.maxHp - GameState.hp);
                GameState.hp += healed;
                block += 10;
                spawnText("Air menyembuhkan! +" + healed + " HP, +10 Block", 400, 190, area.getColor(), 26);
                for (BattleEnemy e : alive) dealDamage(e, 8, false);
                break;
            }
            case SURYA:                                         // sunlight burns everything and breaks shields
                spawnText("Cahaya Surya!", 400, 190, area.getColor(), 28);
                for (BattleEnemy e : alive) dealDamage(e, 14, true);
                break;
            default:                                            // shadows freeze the enemies
                spawnText("Bayangan membekukan musuh!", 400, 190, area.getColor(), 26);
                for (BattleEnemy e : alive) {
                    dealDamage(e, 8, true);
                    if (!e.isDead()) e.stun();
                }
                break;
        }
    }

    /** Called by GlyphPuzzle (kept so the puzzle classes work in this world). */
    public Element getArea() {
        return area;
    }

    // ---------------------------------------------------------- damage & end
    /** Called by enemies when they attack the party. Block absorbs damage first. */
    public void damagePlayer(int damage) {
        int absorbed = Math.min(block, damage);
        block -= absorbed;
        int dealt = damage - absorbed;
        GameState.hp = Math.max(0, GameState.hp - dealt);

        if (dealt > 0) {
            raden.hurt();
            dyah.hurt();
            spawnText("-" + dealt, 170, 150, new Color(255, 90, 90));
        } else {
            spawnText("Blocked", 170, 150, new Color(190, 190, 190));
        }
        dirty = true;

        if (GameState.hp <= 0) {
            lose();
        }
    }

    private boolean allEnemiesDead() {
        for (BattleEnemy e : enemies) {
            if (!e.isDead()) return false;
        }
        return true;
    }

    private void win() {
        state = State.REWARD;
        discardHand();
        removeObject(endTurnButton);
        removeObject(glyphButton);
        GameState.addGlyph(area);                               // the glyph is yours!
        GameState.hp = GameState.maxHp;                         // the party rests

        ArrayList<Card> choices = Card.rewardChoices();
        int[] xs = {250, 400, 550};
        for (int i = 0; i < choices.size(); i++) {
            CardActor ca = new CardActor(choices.get(i));
            rewardCards.add(ca);
            addObject(ca, xs[i], 420);
        }
        banner = "";
        dirty = true;
    }

    private void pickReward(int mx, int my) {
        for (CardActor ca : rewardCards) {
            if (ca.hit(mx, my)) {
                GameState.deck.add(ca.getCard());               // the deck grows for the next battle
                Greenfoot.setWorld(GameState.buildStage(nextStage));
                return;
            }
        }
    }

    private void lose() {
        state = State.LOST;
        discardHand();
        removeObject(endTurnButton);
        removeObject(glyphButton);
        banner = "";
        dirty = true;
    }

    // -------------------------------------------------------------- drawing
    public void spawnText(String s, int x, int y, Color c) {
        spawnText(s, x, y, c, 22);
    }

    public void spawnText(String s, int x, int y, Color c, int size) {
        addObject(new FloatingText(s, size, c), x, y);
    }

    /** Repaint everything that is NOT an actor: bars, numbers, banners. Called only when 'dirty'. */
    private void redrawHud() {
        GreenfootImage bg = getBackground();
        bg.drawImage(base, 0, 0);

        // party health + block, under the two heroes
        Gfx.bar(bg, 90, 322, 160, 12, GameState.hp, GameState.maxHp, new Color(60, 200, 80));
        Gfx.text(bg, "HP " + GameState.hp + "/" + GameState.maxHp, 14, Color.WHITE, 170, 337);
        if (block > 0) {
            Gfx.text(bg, "Block " + block, 14, new Color(120, 190, 255), 170, 355 - 2);
        }

        // energy orb + piles
        bg.setColor(new Color(240, 150, 30));
        bg.fillOval(22, 367, 56, 56);
        bg.setColor(Color.BLACK);
        bg.drawOval(22, 367, 56, 56);
        Gfx.text(bg, energy + "/" + ENERGY_PER_TURN, 20, Color.BLACK, 50, 385);
        Gfx.text(bg, "Tarik " + drawPile.size() + "     Buang " + discardPile.size(), 14, new Color(200, 200, 210), 400, 580);

        // glyph charge bar (top left) + combo counter
        Gfx.text(bg, area.getDisplayName(), 14, area.getColor(), 80, 8);
        Gfx.bar(bg, 20, 26, 120, 10, glyphCharge, GLYPH_MAX, area.getColor());
        Gfx.text(bg, "Kombo: " + comboCount, 14, new Color(255, 230, 100), 80, 42);

        Gfx.text(bg, banner, 26, Color.WHITE, 400, 15);
        if (state == State.PLAYER_TURN) {
            Gfx.text(bg, "Raden menandai, Dyah menembak = KOMBO x2", 13, new Color(255, 230, 120, 200), 400, 44);
        }

        if (state == State.REWARD) {
            Gfx.text(bg, "KEMENANGAN!", 46, new Color(255, 220, 60), 400, 90);
            Gfx.text(bg, area.getDisplayName() + " menjadi milikmu! HP pulih sepenuhnya.", 20, Color.WHITE, 400, 150);
            Gfx.text(bg, "Pilih satu kartu untuk dek-mu", 22, new Color(200, 200, 210), 400, 190);
        } else if (state == State.LOST) {
            Gfx.text(bg, "KALAH", 46, new Color(255, 90, 90), 400, 140);
            Gfx.text(bg, "Klik untuk mencoba lagi", 18, new Color(200, 200, 210), 400, 205);
        }
    }
}
