import greenfoot.*;

/**
 * Puzzle for the AIR glyph: "channel the water".
 * Press W/A/S/D matching the four arrows, in order. A wrong key restarts the sequence.
 * (We use WASD here because Raden walks with the ARROW keys.)
 */
public class WaterFlowPuzzle extends GlyphPuzzle {

    private int[] sequence = new int[4];       // each entry: 0=up 1=right 2=down 3=left
    private int index = 0;                     // how many we have already answered

    // Which keyboard key matches each direction number.
    private static final String[] KEYS = {"w", "d", "s", "a"};
    private static final String[] LABELS = {"W", "D", "S", "A"};

    public WaterFlowPuzzle() {
        super(Element.AIR, "Alirkan air! Tekan W A S D sesuai panah", 480);
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = Greenfoot.getRandomNumber(4);        // random direction 0..3
        }
    }

    @Override
    protected void updatePuzzle(String key) {
        if (key == null) return;
        if (key.equals(KEYS[sequence[index]])) {               // correct key
            index++;
            if (index >= sequence.length) {
                finish(true);                                  // all four done!
            }
        } else if (key.equals("w") || key.equals("a") || key.equals("s") || key.equals("d")) {
            index = 0;                                         // wrong direction: start over
            penalty(30);
        }
    }

    @Override
    protected void drawContent(GreenfootImage img) {
        for (int i = 0; i < sequence.length; i++) {
            Color c;
            if (i < index) c = element.getColor();             // already done
            else if (i == index) c = Color.WHITE;              // the one to press now
            else c = new Color(110, 110, 120);                 // waiting
            int cx = 50 + i * 73;
            drawArrow(img, cx, 58, sequence[i], c);
            drawText(img, LABELS[sequence[i]], 14, c, cx, 78);
        }
    }

    /** Draw an arrow pointing in direction dir (0=up, 1=right, 2=down, 3=left). */
    private void drawArrow(GreenfootImage img, int cx, int cy, int dir, Color c) {
        // Arrow pointing UP, described as points relative to the centre: first the head, then the shaft.
        int[][] head = {{0, -16}, {-13, 2}, {13, 2}};
        int[][] shaft = {{-5, 2}, {5, 2}, {5, 16}, {-5, 16}};
        img.setColor(c);
        img.fillPolygon(rotated(head, cx, cy, dir, true), rotated(head, cx, cy, dir, false), head.length);
        img.fillPolygon(rotated(shaft, cx, cy, dir, true), rotated(shaft, cx, cy, dir, false), shaft.length);
    }

    /** Rotate points by dir * 90 degrees clockwise and return the x (or y) coordinates. */
    private int[] rotated(int[][] pts, int cx, int cy, int dir, boolean wantX) {
        int[] out = new int[pts.length];
        for (int i = 0; i < pts.length; i++) {
            int x = pts[i][0];
            int y = pts[i][1];
            for (int r = 0; r < dir; r++) {        // each loop turns the point 90 degrees clockwise
                int nx = -y;
                int ny = x;
                x = nx;
                y = ny;
            }
            out[i] = wantX ? cx + x : cy + y;
        }
        return out;
    }
}
