import greenfoot.*;

/**
 * Portal - the exit of an exploration level. Sealed (grey) until the glyph is
 * collected, then it glows and takes the heroes to the boss battle.
 */
public class Portal extends Actor {

    private Element element;
    private boolean opened = false;
    private int tick = 0;

    public Portal(Element element) {
        this.element = element;
        draw(false);
    }

    public void act() {
        ExplorationWorld world = (ExplorationWorld) getWorld();
        if (!world.isGlyphCollected()) {
            return;                                   // still sealed
        }
        if (!opened) {
            opened = true;
            draw(true);                               // switch to the glowing look (once)
        }
        tick++;
        getImage().setTransparency(190 + (int) (60 * Math.sin(tick * 0.15)));   // pulsing glow

        if (isTouching(Explorer.class)) {
            Greenfoot.setWorld(GameState.buildStage(world.getNextStage()));
        }
    }

    private void draw(boolean open) {
        GreenfootImage g = new GreenfootImage(56, 76);
        Color c = open ? element.getColor() : new Color(110, 110, 110);
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 90));
        g.fillOval(0, 0, 55, 75);                                   // outer glow
        g.setColor(c);
        g.fillOval(8, 8, 39, 59);
        g.setColor(open ? Color.WHITE : new Color(70, 70, 70));
        g.fillOval(18, 20, 19, 35);
        setImage(g);
    }
}
