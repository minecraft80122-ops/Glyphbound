import greenfoot.*;

/**
 * Arrow - Dyah's arrow in the temple puzzle levels. It flies in a straight line,
 * triggers Targets (via Obstacle.onArrowHit) and stops at walls.
 * (In the card battle, arrows are just cards - see Card.panah().)
 *
 * Greenfoot positions are whole numbers (int), but we want smooth diagonal
 * flight, so we keep our own precise 'double' coordinates x and y.
 */
public class Arrow extends Actor {

    private double x, y;          // precise position
    private double vx, vy;        // velocity (pixels per frame)
    private int damage;

    public Arrow(double dirX, double dirY, int damage) {
        this.damage = damage;
        double len = Math.sqrt(dirX * dirX + dirY * dirY);
        if (len == 0) {           // safety: never divide by zero
            dirX = 1;
            len = 1;
        }
        vx = dirX / len * 9;      // normalise the direction, then multiply by speed 9
        vy = dirY / len * 9;

        GreenfootImage g = new GreenfootImage(26, 8);
        g.setColor(new Color(130, 90, 40));
        g.fillRect(0, 3, 22, 2);                                       // shaft
        g.setColor(Color.LIGHT_GRAY);
        g.fillPolygon(new int[]{20, 26, 20}, new int[]{0, 4, 8}, 3);   // arrow head
        setImage(g);
        setRotation((int) Math.toDegrees(Math.atan2(vy, vx)));         // face the flight direction
    }

    /** Greenfoot calls this right after the arrow is added to a world. */
    @Override
    protected void addedToWorld(World world) {
        x = getX();
        y = getY();
    }

    public void act() {
        x += vx;
        y += vy;
        setLocation((int) x, (int) y);
        World world = getWorld();

        // 1) Did we hit an obstacle?
        Obstacle o = (Obstacle) getOneIntersectingObject(Obstacle.class);
        if (o != null && o.blocksArrows()) {
            o.onArrowHit();                        // POLYMORPHISM: Target opens its gate, Wall does nothing
            world.removeObject(this);
            return;
        }

        // 2) Left the screen?
        if (isAtEdge()) {
            world.removeObject(this);
        }
    }
}
