import greenfoot.*;

/** Crate - a heavy wooden box. Only RADEN can push it (see Explorer.tryMove). */
public class Crate extends Obstacle {

    public static final int SIZE = 50;

    public Crate() {
        GreenfootImage g = new GreenfootImage(SIZE, SIZE);
        g.setColor(new Color(165, 115, 55));
        g.fillRect(0, 0, SIZE, SIZE);
        g.setColor(new Color(95, 60, 25));
        g.drawRect(0, 0, SIZE - 1, SIZE - 1);
        g.drawRect(5, 5, SIZE - 11, SIZE - 11);
        g.drawLine(5, 5, SIZE - 6, SIZE - 6);                  // the "X" on the crate
        g.drawLine(SIZE - 6, 5, 5, SIZE - 6);
        setImage(g);
    }

    /**
     * Try to slide the crate by (dx, dy). Returns true if it moved.
     * The crate refuses if it would hit another obstacle or leave the floor area.
     */
    public boolean tryPush(int dx, int dy) {
        int oldX = getX();
        int oldY = getY();
        setLocation(oldX + dx, oldY + dy);                     // move first, then check

        int half = SIZE / 2;
        boolean outside = getX() - half < 0
                || getX() + half >= getWorld().getWidth()
                || getY() - half < Scenery.HORIZON
                || getY() + half >= getWorld().getHeight();

        if (outside || isTouching(Obstacle.class)) {           // blocked!
            setLocation(oldX, oldY);                           // undo the move
            return false;
        }
        return true;
    }
}
