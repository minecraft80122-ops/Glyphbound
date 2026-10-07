import greenfoot.*;

public class Player extends Actor
{
    private boolean battleMode = false;
    private int hurtFrames = 0;

    public Player()
    {
        battleMode = false;
    }

    public Player(boolean battleMode)
    {
        this.battleMode = battleMode;
        if (battleMode)
        {
            // bigger sprite for the battle screen
            GreenfootImage img = new GreenfootImage("ppl1.png");
            img.scale(107, 127);
            setImage(img);
        }
    }

    public void act()
    {
        if (battleMode)
        {
            if (hurtFrames > 0)
            {
                hurtFrames--;
                getImage().setTransparency(hurtFrames % 4 < 2 ? 90 : 255);
            }
            return;
        }

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

        Enemy touched = (Enemy) getOneIntersectingObject(Enemy.class);
        if (touched != null)
        {
            Greenfoot.setWorld(new BattleWorld(getWorld(), touched, touched.getGroup()));
        }
    }

    /** Flicker briefly (battle mode) when damaged. */
    public void hurt()
    {
        hurtFrames = 12;
    }
}
