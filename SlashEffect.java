import greenfoot.*;

/**
 * SlashEffect - a short-lived crescent that shows a sword swing.
 * It is purely visual: the damage is handled by whoever creates it.
 */
public class SlashEffect extends Actor {

    private int life = 10;

    public SlashEffect(int rotationDegrees, Color color) {
        int size = 70;
        GreenfootImage g = new GreenfootImage(size, size);
        int c = size / 2;
        int n = 9;                                   // points along each arc
        int[] xs = new int[2 * n];
        int[] ys = new int[2 * n];
        for (int i = 0; i < n; i++) {
            double a = Math.toRadians(-60 + 120.0 * i / (n - 1));   // sweep from -60 to +60 degrees
            xs[i] = (int) (c + Math.cos(a) * 30);                    // outer edge of the crescent
            ys[i] = (int) (c + Math.sin(a) * 30);
            xs[2 * n - 1 - i] = (int) (c + Math.cos(a) * 20);        // inner edge (walked backwards)
            ys[2 * n - 1 - i] = (int) (c + Math.sin(a) * 20);
        }
        g.setColor(color);
        g.fillPolygon(xs, ys, 2 * n);
        setImage(g);
        setRotation(rotationDegrees);               // point the crescent in the attack direction
    }

    public void act() {
        life--;
        getImage().setTransparency(Math.max(0, life * 25));
        if (life <= 0) {
            getWorld().removeObject(this);
        }
    }
}
