import greenfoot.*;
import java.util.*;

/**
 * Card-combat screen. Turn based:
 *   1. You get 3 energy and draw 5 cards.
 *   2. Click a card. Single-target attacks then need a click on an enemy;
 *      everything else plays straight away.
 *   3. Click End Turn. Each enemy does what its intent said.
 * Win by killing every enemy, lose if your HP hits 0.
 *
 * To test on its own: right-click BattleWorld in Greenfoot > new BattleWorld().
 */
public class BattleWorld extends World
{
    private static final int HAND_SIZE = 5;
    private static final int MAX_HAND = 7;
    private static final int ENERGY_PER_TURN = 3;
    private static final int HEAL_AFTER_WIN = 8;
    private static final int HAND_Y = 505;
    private static final int RAISED_Y = 478;

    private enum State { PLAYER_TURN, ENEMY_TURN, WON, LOST }

    private World returnWorld;
    private Enemy overworldEnemy;
    private Player player;
    private Button endTurnButton;
    private GreenfootImage base;

    private ArrayList<BattleEnemy> enemies = new ArrayList<BattleEnemy>();
    private ArrayList<CardActor> hand = new ArrayList<CardActor>();
    private ArrayList<Card> drawPile;
    private ArrayList<Card> discardPile = new ArrayList<Card>();
    private CardActor selected = null;

    private State state;
    private int energy, block, turn, timer, enemyIndex;
    private int mouseX = -1, mouseY = -1;
    private boolean dirty = true;
    private String banner = "";
    private String rewardText = "";

    /** Quick test fight against all three enemy types. */
    public BattleWorld()
    {
        this(null, null, new EnemyType[] { EnemyType.SLIME, EnemyType.GOBLIN, EnemyType.SKELETON });
    }

    public BattleWorld(World returnWorld, Enemy overworldEnemy, EnemyType[] group)
    {
        super(800, 600, 1);
        setPaintOrder(FloatText.class, CardActor.class, Button.class, BattleEnemy.class, Player.class);

        this.returnWorld = returnWorld;
        this.overworldEnemy = overworldEnemy;
        GameState.ensureInit();

        buildBackground();

        player = new Player(true);
        addObject(player, 130, 270);

        spawnEnemies(group);

        drawPile = new ArrayList<Card>(GameState.deck);
        Collections.shuffle(drawPile);

        endTurnButton = new Button("End Turn", 100, 36);
        addObject(endTurnButton, 735, 385);

        startPlayerTurn();
        redrawHud();
    }

    // ------------------------------------------------------------------ setup

    private void buildBackground()
    {
        base = new GreenfootImage(800, 600);
        GreenfootImage tile = new GreenfootImage("wet-blue.jpg");
        for (int x = 0; x < 800; x += tile.getWidth())
        {
            for (int y = 0; y < 600; y += tile.getHeight())
            {
                base.drawImage(tile, x, y);
            }
        }
        base.setColor(new Color(0, 0, 20, 150));
        base.fillRect(0, 0, 800, 600);
        base.setColor(new Color(0, 0, 0, 110));
        base.fillRect(0, 350, 800, 250);
        base.setColor(new Color(160, 160, 170));
        base.drawLine(0, 350, 800, 350);
    }

    private void spawnEnemies(EnemyType[] group)
    {
        int count = Math.max(1, Math.min(group.length, 3));
        int[][] layouts = { { 560 }, { 490, 640 }, { 420, 560, 700 } };
        int[] xs = layouts[count - 1];
        for (int i = 0; i < count; i++)
        {
            BattleEnemy e = new BattleEnemy(group[i], Greenfoot.getRandomNumber(group[i].pattern.length()));
            enemies.add(e);
            addObject(e, xs[i], 250);
        }
    }

    // ------------------------------------------------------------- main loop

    public void act()
    {
        MouseInfo mi = Greenfoot.getMouseInfo();
        if (mi != null)
        {
            mouseX = mi.getX();
            mouseY = mi.getY();
        }

        if (Greenfoot.mouseClicked(null) && mi != null)
        {
            handleClick(mi.getX(), mi.getY());
        }

        updateHover();

        if (state == State.ENEMY_TURN)
        {
            runEnemyTurn();
        }

        if (dirty)
        {
            redrawHud();
            dirty = false;
        }
    }

    private void handleClick(int mx, int my)
    {
        if (state == State.WON)
        {
            finishVictory();
            return;
        }
        if (state == State.LOST)
        {
            Greenfoot.setWorld(new gameWorld());
            return;
        }
        if (state != State.PLAYER_TURN) return;

        if (endTurnButton.hit(mx, my))
        {
            endPlayerTurn();
            return;
        }

        CardActor clicked = cardAt(mx, my);
        if (clicked != null)
        {
            Card c = clicked.getCard();
            if (clicked == selected)
            {
                deselect();
            }
            else if (c.cost > energy)
            {
                spawnText("Not enough energy", clicked.getX(), HAND_Y - 90, new Color(255, 200, 80));
            }
            else if (c.needsTarget())
            {
                select(clicked);
            }
            else
            {
                playCard(clicked, null);
            }
            return;
        }

        if (selected != null)
        {
            BattleEnemy target = enemyAt(mx, my);
            if (target != null)
            {
                playCard(selected, target);
            }
            else
            {
                deselect();
            }
        }
    }

    private void updateHover()
    {
        BattleEnemy hovered = null;
        if (state == State.PLAYER_TURN && selected != null)
        {
            hovered = enemyAt(mouseX, mouseY);
        }
        for (BattleEnemy e : enemies)
        {
            e.setHighlight(e == hovered);
        }
    }

    // ------------------------------------------------------------ hit testing

    private CardActor cardAt(int mx, int my)
    {
        CardActor best = null;
        for (CardActor ca : hand)
        {
            if (ca.hit(mx, my) && (best == null || Math.abs(mx - ca.getX()) < Math.abs(mx - best.getX())))
            {
                best = ca;
            }
        }
        return best;
    }

    private BattleEnemy enemyAt(int mx, int my)
    {
        for (BattleEnemy e : enemies)
        {
            if (!e.isDead() && e.hit(mx, my)) return e;
        }
        return null;
    }

    // ------------------------------------------------------------------ turns

    private void startPlayerTurn()
    {
        state = State.PLAYER_TURN;
        turn++;
        energy = ENERGY_PER_TURN;
        block = 0;
        banner = "Your turn";
        endTurnButton.setEnabled(true);
        for (int i = 0; i < HAND_SIZE; i++) drawCard();
        layoutHand();
        refreshCards();
        dirty = true;
    }

    private void endPlayerTurn()
    {
        deselect();
        discardHand();
        state = State.ENEMY_TURN;
        banner = "Enemy turn";
        endTurnButton.setEnabled(false);
        enemyIndex = 0;
        timer = 25;
        dirty = true;
    }

    private void runEnemyTurn()
    {
        timer--;
        if (timer > 0) return;

        while (enemyIndex < enemies.size() && enemies.get(enemyIndex).isDead())
        {
            enemyIndex++;
        }
        if (enemyIndex >= enemies.size())
        {
            startPlayerTurn();
            return;
        }
        enemies.get(enemyIndex).takeTurn(this);
        enemyIndex++;
        timer = 35;
    }

    // ------------------------------------------------------------------ cards

    private void drawCard()
    {
        if (hand.size() >= MAX_HAND) return;
        if (drawPile.isEmpty())
        {
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

    private void discardHand()
    {
        for (CardActor ca : hand)
        {
            discardPile.add(ca.getCard());
            removeObject(ca);
        }
        hand.clear();
        selected = null;
    }

    private void layoutHand()
    {
        int n = hand.size();
        if (n == 0) return;
        int spacing = Math.min(112, 700 / n);
        int startX = 400 - (n - 1) * spacing / 2;
        for (int i = 0; i < n; i++)
        {
            CardActor ca = hand.get(i);
            ca.setLocation(startX + i * spacing, ca == selected ? RAISED_Y : HAND_Y);
        }
    }

    private void refreshCards()
    {
        for (CardActor ca : hand)
        {
            ca.setPlayable(ca.getCard().cost <= energy);
        }
    }

    private void select(CardActor ca)
    {
        if (selected != null) selected.setSelected(false);
        selected = ca;
        ca.setSelected(true);
        banner = "Pick a target";
        layoutHand();
        dirty = true;
    }

    private void deselect()
    {
        if (selected != null) selected.setSelected(false);
        selected = null;
        if (state == State.PLAYER_TURN) banner = "Your turn";
        layoutHand();
        dirty = true;
    }

    private void playCard(CardActor ca, BattleEnemy target)
    {
        Card c = ca.getCard();
        energy -= c.cost;

        if (c.damage > 0)
        {
            if (c.allEnemies)
            {
                for (BattleEnemy e : enemies)
                {
                    if (!e.isDead())
                    {
                        hitEnemy(e, c.damage);
                    }
                }
            }
            else
            {
                hitEnemy(target, c.damage);
            }
        }
        if (c.block > 0)
        {
            block += c.block;
            spawnText("+" + c.block + " Block", player.getX(), player.getY() - 80, new Color(120, 190, 255));
        }
        if (c.heal > 0)
        {
            int healed = Math.min(c.heal, GameState.maxHp - GameState.hp);
            GameState.hp += healed;
            spawnText("+" + healed + " HP", player.getX(), player.getY() - 80, new Color(100, 230, 120));
        }

        hand.remove(ca);
        removeObject(ca);
        discardPile.add(c);
        selected = null;
        banner = "Your turn";

        for (int i = 0; i < c.draw; i++) drawCard();

        layoutHand();
        refreshCards();
        dirty = true;

        if (allEnemiesDead())
        {
            win();
        }
    }

    private void hitEnemy(BattleEnemy e, int damage)
    {
        int dealt = e.takeDamage(damage);
        if (dealt > 0)
        {
            spawnText("-" + dealt, e.getX(), e.getY() - 60, new Color(255, 90, 90));
        }
        else
        {
            spawnText("Blocked", e.getX(), e.getY() - 60, new Color(190, 190, 190));
        }
    }

    // ---------------------------------------------------------- damage & end

    /** Called by enemies when they attack. */
    public void damagePlayer(int damage)
    {
        int absorbed = Math.min(block, damage);
        block -= absorbed;
        int dealt = damage - absorbed;
        GameState.hp = Math.max(0, GameState.hp - dealt);

        if (dealt > 0)
        {
            player.hurt();
            spawnText("-" + dealt, player.getX(), player.getY() - 80, new Color(255, 90, 90));
        }
        else
        {
            spawnText("Blocked", player.getX(), player.getY() - 80, new Color(190, 190, 190));
        }
        dirty = true;

        if (GameState.hp <= 0)
        {
            lose();
        }
    }

    private boolean allEnemiesDead()
    {
        for (BattleEnemy e : enemies)
        {
            if (!e.isDead()) return false;
        }
        return true;
    }

    private void win()
    {
        state = State.WON;
        discardHand();
        removeObject(endTurnButton);

        Card reward = Card.randomReward();
        GameState.deck.add(reward);
        GameState.hp = Math.min(GameState.maxHp, GameState.hp + HEAL_AFTER_WIN);
        rewardText = "New card: " + reward.name + "     +" + HEAL_AFTER_WIN + " HP";
        banner = "";
        dirty = true;
    }

    private void lose()
    {
        state = State.LOST;
        discardHand();
        removeObject(endTurnButton);
        banner = "";
        dirty = true;
    }

    private void finishVictory()
    {
        if (returnWorld instanceof gameWorld)
        {
            ((gameWorld) returnWorld).battleWon(overworldEnemy);
            Greenfoot.setWorld(returnWorld);
        }
        else
        {
            Greenfoot.setWorld(new BattleWorld());
        }
    }

    // -------------------------------------------------------------- drawing

    public void spawnText(String s, int x, int y, Color c)
    {
        addObject(new FloatText(s, c, 22), x, y);
    }

    private void redrawHud()
    {
        GreenfootImage bg = getBackground();
        bg.drawImage(base, 0, 0);

        // player health / block
        int px = player.getX();
        int py = player.getY();
        Gfx.bar(bg, px - 50, py + 72, 100, 10, GameState.hp, GameState.maxHp, new Color(60, 200, 80));
        Gfx.text(bg, "HP " + GameState.hp + "/" + GameState.maxHp, 14, Color.WHITE, px, py + 86);
        if (block > 0)
        {
            Gfx.text(bg, "Block " + block, 14, new Color(120, 190, 255), px, py + 104);
        }

        // energy orb + piles
        bg.setColor(new Color(240, 150, 30));
        bg.fillOval(22, 367, 56, 56);
        bg.setColor(Color.BLACK);
        bg.drawOval(22, 367, 56, 56);
        Gfx.text(bg, energy + "/" + ENERGY_PER_TURN, 20, Color.BLACK, 50, 385);
        Gfx.text(bg, "Draw " + drawPile.size() + "     Discard " + discardPile.size(), 14, new Color(200, 200, 210), 400, 580);

        // banner
        Gfx.text(bg, banner, 26, Color.WHITE, 400, 15);

        if (state == State.WON)
        {
            Gfx.text(bg, "VICTORY!", 46, new Color(255, 220, 60), 400, 140);
            Gfx.text(bg, rewardText, 22, Color.WHITE, 400, 205);
            Gfx.text(bg, "Click to return to the dungeon", 18, new Color(200, 200, 210), 400, 245);
        }
        else if (state == State.LOST)
        {
            Gfx.text(bg, "DEFEATED", 46, new Color(255, 90, 90), 400, 140);
            Gfx.text(bg, "Click to try again", 18, new Color(200, 200, 210), 400, 205);
        }
    }
}
