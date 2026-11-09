import greenfoot.*;

/** Wall - a stone block. Nothing special: it just blocks walking and arrows. */
public class Wall extends Obstacle {

    public Wall(int w, int h, Color base) {
        GreenfootImage g = new GreenfootImage(w, h);
        g.setColor(base);
        g.fillRect(0, 0, w, h);
        // Brick pattern: horizontal lines every 20px and offset vertical lines.
        g.setColor(base.darker());
        for (int y = 0; y < h; y += 20) {
            g.drawLine(0, y, w, y);
            int offset = ((y / 20) % 2 == 0) ? 0 : 15;       // every other row is shifted
            for (int x = offset; x < w; x += 30) {
                g.drawLine(x, y, x, Math.min(h, y + 20));
            }
        }
        g.setColor(base.brighter());
        g.drawRect(0, 0, w - 1, h - 1);
        setImage(g);
    }
}
