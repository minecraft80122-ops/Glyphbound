import greenfoot.*;

/** Glyph - a floating elemental crystal. Touch it with the hero to collect it. */
public class Glyph extends Actor {

    private Element element;
    private int baseY;            // the height it hovers around
    private int tick = 0;

    public Glyph(Element element) {
        this.element = element;
        Color c = element.getColor();
        GreenfootImage g = new GreenfootImage(52, 52);
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 70));
        g.fillOval(0, 0, 51, 51);                                             // soft glow
        g.setColor(c);
        g.fillPolygon(new int[]{26, 42, 26, 10}, new int[]{6, 26, 46, 26}, 4); // diamond
        g.setColor(Color.WHITE);
        g.drawLine(26, 6, 42, 26); g.drawLine(42, 26, 26, 46);
        g.drawLine(26, 46, 10, 26); g.drawLine(10, 26, 26, 6);
        setImage(g);
    }

    @Override
    protected void addedToWorld(World world) {
        baseY = getY();
    }

    public void act() {
        tick++;
        setLocation(getX(), baseY + (int) (6 * Math.sin(tick * 0.08)));      // gentle up/down bobbing

        Explorer hero = (Explorer) getOneIntersectingObject(Explorer.class);
        if (hero != null) {
            ExplorationWorld world = (ExplorationWorld) getWorld();
            world.glyphCollected(element);
            world.removeObject(this);
        }
    }
}
