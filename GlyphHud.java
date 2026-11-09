import greenfoot.*;

/** GlyphHud - three diamond slots that fill with colour as you collect the glyphs. */
public class GlyphHud extends Actor {

    private int lastCount = -1;

    public GlyphHud() {
        refresh();
    }

    public void act() {
        if (GameState.glyphCount() != lastCount) {         // only redraw when something changed
            refresh();
        }
    }

    private void refresh() {
        lastCount = GameState.glyphCount();
        GreenfootImage g = new GreenfootImage(190, 46);
        g.setColor(new Color(10, 10, 20, 190));
        g.fillRect(0, 0, 190, 46);
        g.setColor(new Color(220, 180, 80));
        g.drawRect(0, 0, 189, 45);
        g.drawImage(new GreenfootImage("Glif", 16, Color.WHITE, new Color(0, 0, 0, 0)), 10, 13);

        Element[] all = Element.values();                  // [AIR, SURYA, BAYANGAN]
        for (int i = 0; i < all.length; i++) {
            int cx = 78 + i * 38;
            int[] xs = {cx, cx + 13, cx, cx - 13};
            int[] ys = {7, 23, 39, 23};
            if (GameState.hasGlyph(all[i])) {
                g.setColor(all[i].getColor());
                g.fillPolygon(xs, ys, 4);
            } else {
                g.setColor(new Color(90, 90, 100));
                g.drawLine(cx, 7, cx + 13, 23); g.drawLine(cx + 13, 23, cx, 39);
                g.drawLine(cx, 39, cx - 13, 23); g.drawLine(cx - 13, 23, cx, 7);
            }
        }
        setImage(g);
    }
}
