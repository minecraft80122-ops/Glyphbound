import greenfoot.*;

/** Barricade - wooden planks that only RADEN can cut down (2 slashes). */
public class Barricade extends Obstacle {

    private int hp = 2;
    private int w, h;

    public Barricade(int w, int h) {
        this.w = w;
        this.h = h;
        draw();
    }

    /** Called by Raden's sword. */
    public void slash() {
        hp--;
        World world = getWorld();
        if (hp <= 0) {
            world.addObject(new FloatingText("Krak!", 20, Color.WHITE), getX(), getY() - 20);
            world.removeObject(this);          // the barricade disappears
        } else {
            draw();                            // redraw with a crack
        }
    }

    private void draw() {
        GreenfootImage g = new GreenfootImage(w, h);
        g.setColor(new Color(150, 100, 50));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(90, 55, 25));
        for (int x = 0; x < w; x += 10) {                      // vertical planks
            g.drawLine(x, 0, x, h);
        }
        g.fillRect(0, h / 4, w, 6);                            // two cross beams
        g.fillRect(0, h * 3 / 4, w, 6);
        if (hp < 2) {                                          // damaged: draw cracks
            g.setColor(Color.BLACK);
            g.drawLine(2, h / 3, w - 2, h / 2);
            g.drawLine(w / 2, h / 2, 4, h * 2 / 3);
        }
        setImage(g);
    }
}
