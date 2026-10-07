import greenfoot.*;

/**
 * An enemy walking around the dungeon. Touching it starts a card battle
 * against its whole group (1 to 3 enemies).
 */
public class Enemy extends Actor
{
    private EnemyType[] group;

    public Enemy()
    {
        this(EnemyType.SLIME);
    }

    public Enemy(EnemyType... group)
    {
        this.group = group;

        // tint the sprite with the colour of the first enemy in the group
        GreenfootImage img = new GreenfootImage("ppl3.png");
        Color t = group[0].color;
        for (int x = 0; x < img.getWidth(); x++)
        {
            for (int y = 0; y < img.getHeight(); y++)
            {
                Color c = img.getColorAt(x, y);
                if (c.getAlpha() > 0)
                {
                    img.setColorAt(x, y, new Color((c.getRed() + t.getRed()) / 2,
                                                   (c.getGreen() + t.getGreen()) / 2,
                                                   (c.getBlue() + t.getBlue()) / 2,
                                                   c.getAlpha()));
                }
            }
        }
        setImage(img);
    }

    public EnemyType[] getGroup()
    {
        return group;
    }

    public void act()
    {
        // TODO: patrol / chase the player
    }
}
