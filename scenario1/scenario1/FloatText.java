import greenfoot.*;

/**
 * Floating damage / healing numbers that drift upward and fade out.
 */
public class FloatText extends Actor
{
    private int life = 45;

    public FloatText(String text, Color color, int size)
    {
        setImage(new GreenfootImage(text, size, color, new Color(0, 0, 0, 0)));
    }

    public void act()
    {
        setLocation(getX(), getY() - 1);
        life--;
        if (life < 20 && life > 0)
        {
            getImage().setTransparency(life * 255 / 20);
        }
        if (life <= 0)
        {
            getWorld().removeObject(this);
        }
    }
}
