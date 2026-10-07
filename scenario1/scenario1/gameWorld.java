import greenfoot.*;

/**
 * The dungeon room. Walk with the arrow keys; touch an enemy to fight it.
 */
public class gameWorld extends World
{
    public gameWorld()
    {
        super(600, 400, 1);

        GameState.reset();

        addObject(new Player(), 50, 200);

        // each Enemy is a group that gets fought together
        addObject(new Enemy(EnemyType.SLIME), 250, 110);
        addObject(new Enemy(EnemyType.GOBLIN, EnemyType.SLIME), 380, 290);
        addObject(new Enemy(EnemyType.SKELETON, EnemyType.GOBLIN, EnemyType.SLIME), 520, 130);

        updateHud();
    }

    /** Called by BattleWorld when the player wins. */
    public void battleWon(Enemy defeated)
    {
        if (defeated != null)
        {
            removeObject(defeated);
        }
        updateHud();
        if (getObjects(Enemy.class).isEmpty())
        {
            showText("Room cleared!  (more rooms coming soon)", 300, 200);
        }
    }

    private void updateHud()
    {
        showText("HP " + GameState.hp + "/" + GameState.maxHp + "    Deck: " + GameState.deck.size() + " cards", 120, 15);
    }
}
