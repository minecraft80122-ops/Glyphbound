import greenfoot.*;

/**
 * A simple clickable button. BattleWorld does the click detection.
 */
public class Button extends Actor
{
    private String label;
    private int w, h;
    private boolean enabled = true;

    public Button(String label, int w, int h)
    {
        this.label = label;
        this.w = w;
        this.h = h;
        redraw();
    }

    public void setEnabled(boolean enabled)
    {
        if (this.enabled != enabled)
        {
            this.enabled = enabled;
            redraw();
        }
    }

    public boolean hit(int mx, int my)
    {
        return Math.abs(mx - getX()) <= w / 2 && Math.abs(my - getY()) <= h / 2;
    }

    private void redraw()
    {
        GreenfootImage img = new GreenfootImage(w, h);
        img.setColor(enabled ? new Color(230, 230, 230) : new Color(110, 110, 110));
        img.fillRect(0, 0, w, h);
        img.setColor(enabled ? new Color(170, 60, 40) : new Color(70, 70, 70));
        img.fillRect(3, 3, w - 6, h - 6);
        Gfx.text(img, label, 16, Color.WHITE, w / 2, (h - 18) / 2);
        setImage(img);
    }
}
