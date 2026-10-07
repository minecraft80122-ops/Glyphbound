import greenfoot.*;

public class Player extends Actor
{
    private boolean battleMode = false;

    public Player()
    {
        battleMode = false;
    }

    public Player(boolean battleMode)
    {
        this.battleMode = battleMode;
    }

    public void act()
    {
        if (!battleMode)
        {
            if (Greenfoot.isKeyDown("left"))
            {
                setLocation(getX() - 3, getY());
            }

            if (Greenfoot.isKeyDown("right"))
            {
                setLocation(getX() + 3, getY());
            }

            if (Greenfoot.isKeyDown("up"))
            {
                setLocation(getX(), getY() - 3);
            }

            if (Greenfoot.isKeyDown("down"))
            {
                setLocation(getX(), getY() + 3);
            }

            if (isTouching(Enemy.class))
            {
                Greenfoot.setWorld(new BattleWorld());
            }
        }
    }
}