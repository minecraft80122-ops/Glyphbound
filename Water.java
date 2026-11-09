import greenfoot.*;

/**
 * Water - too deep to walk through, but arrows fly right over it!
 * Shows OVERRIDING: we replace the parent's blocksArrows() with our own version.
 */
public class Water extends Obstacle {

    public Water(int w, int h) {
        GreenfootImage g = new GreenfootImage(w, h);
        g.setColor(new Color(35, 105, 190));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(120, 190, 240, 160));            // light ripple lines
        for (int y = 8; y < h; y += 14) {
            for (int x = (y % 28 == 8 ? 0 : 12); x < w - 12; x += 28) {
                g.drawLine(x, y, x + 12, y);
            }
        }
        setImage(g);
    }

    @Override                       // @Override tells the compiler "I'm replacing a parent method"
    public boolean blocksArrows() {
        return false;
    }
}
