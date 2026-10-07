import greenfoot.*;

public class BattleWorld extends World
{
    public BattleWorld()
    {    
        super(800, 600, 1);

        Player player = new Player(true);
        addObject(player, 200, 300);

        Enemy enemy = new Enemy();
        addObject(enemy, 600, 300);
    }
}