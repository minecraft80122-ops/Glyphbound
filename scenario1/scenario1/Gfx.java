import greenfoot.*;

/**
 * Tiny drawing helpers shared by cards, enemies and the battle HUD.
 */
public class Gfx
{
    /** Draw text centred horizontally on centerX, with its top edge at 'top'. */
    public static void text(GreenfootImage target, String s, int size, Color c, int centerX, int top)
    {
        if (s == null || s.length() == 0) return;
        GreenfootImage t = new GreenfootImage(s, size, c, new Color(0, 0, 0, 0));
        target.drawImage(t, centerX - t.getWidth() / 2, top);
    }

    /** Draw a simple horizontal bar (health etc). */
    public static void bar(GreenfootImage target, int x, int y, int w, int h, int value, int max, Color fill)
    {
        target.setColor(new Color(30, 30, 30));
        target.fillRect(x, y, w, h);
        if (max > 0 && value > 0)
        {
            target.setColor(fill);
            target.fillRect(x, y, Math.max(1, w * Math.min(value, max) / max), h);
        }
        target.setColor(new Color(230, 230, 230));
        target.drawRect(x, y, w - 1, h - 1);
    }
}
