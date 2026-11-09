import greenfoot.*;

/** GlyphBlast - an expanding coloured ring that plays when a glyph power is unleashed. */
public class GlyphBlast extends Actor {

    private Color color;
    private int diameter = 20;

    public GlyphBlast(Color color) {
        this.color = color;
        redraw();
    }

    public void act() {
        diameter += 22;                                   // grow every frame
        if (diameter > 560) {
            getWorld().removeObject(this);
            return;
        }
        redraw();
    }

    private void redraw() {
        GreenfootImage g = new GreenfootImage(diameter, diameter);
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        g.fillOval(0, 0, diameter - 1, diameter - 1);     // faint filled disc
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 220));
        g.drawOval(0, 0, diameter - 1, diameter - 1);     // bright outline
        g.drawOval(2, 2, diameter - 5, diameter - 5);
        setImage(g);
    }
}
