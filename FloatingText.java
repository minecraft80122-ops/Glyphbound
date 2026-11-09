import greenfoot.*;

/**
 * FloatingText - short text that rises and fades ("-10", "KOMBO x2!", ...).
 * It deletes itself when finished, so nobody has to clean it up.
 */
public class FloatingText extends Actor {

    private final int maxLife = 50;     // frames the text lives
    private int life = maxLife;

    public FloatingText(String text, int size, Color color) {
        setImage(new GreenfootImage(text, size, color, new Color(0, 0, 0, 0)));
    }

    public void act() {
        setLocation(getX(), getY() - 1);                          // float upwards
        life--;
        getImage().setTransparency(Math.max(0, life * 255 / maxLife));   // fade out
        if (life <= 0) {
            getWorld().removeObject(this);                        // remove myself from the world
        }
    }
}
