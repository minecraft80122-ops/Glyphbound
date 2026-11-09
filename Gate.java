import greenfoot.*;

/** Gate - a sealed stone door. It opens when its linked Target is hit by an arrow. */
public class Gate extends Obstacle {

    public Gate(int w, int h, Color wallColor) {
        GreenfootImage g = new GreenfootImage(w, h);
        g.setColor(wallColor.darker());
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(220, 180, 70));                   // golden bars
        for (int y = 6; y < h; y += 14) {
            g.fillRect(2, y, w - 4, 4);
        }
        g.setColor(new Color(255, 80, 60));                    // red "locked" light
        g.fillOval(w / 2 - 5, h / 2 - 5, 10, 10);
        setImage(g);
    }

    /** Open the gate: show a message and remove myself. */
    public void open() {
        World world = getWorld();
        world.addObject(new FloatingText("Gerbang terbuka!", 22, new Color(255, 230, 120)), getX(), getY() - 30);
        world.removeObject(this);
    }
}
