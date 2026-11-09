import greenfoot.*;

/**
 * Puzzle for the SURYA glyph: "align the light mirror".
 * A mirror slides left and right. Press E when it sits inside the golden zone.
 * Align it 3 times in a row (it gets faster each time).
 */
public class LightMirrorPuzzle extends GlyphPuzzle {

    private double phase = 0;                  // drives the sine wave that moves the mirror
    private int hits = 0;
    private int zoneCenter = 160;              // x of the golden target zone
    private double mirrorX = 160;

    public LightMirrorPuzzle() {
        super(Element.SURYA, "Selaraskan cermin cahaya! Tekan E di zona emas", 480);
        zoneCenter = randomZone();
    }

    private int randomZone() {
        return 70 + Greenfoot.getRandomNumber(181);            // somewhere between 70 and 250
    }

    @Override
    protected void updatePuzzle(String key) {
        phase += 0.07 + 0.03 * hits;                           // faster after each success
        mirrorX = 160 + 125 * Math.sin(phase);                 // sine wave = smooth back-and-forth

        if (key != null && key.equals("e")) {
            if (Math.abs(mirrorX - zoneCenter) <= 20) {        // close enough? (abs = ignore +/- sign)
                hits++;
                if (hits >= 3) {
                    finish(true);
                    return;
                }
                zoneCenter = randomZone();                     // move the zone for the next round
            } else {
                penalty(30);
            }
        }
    }

    @Override
    protected void drawContent(GreenfootImage img) {
        img.setColor(new Color(70, 70, 80));
        img.fillRect(25, 56, 270, 8);                          // the rail
        Color z = element.getColor();
        img.setColor(new Color(z.getRed(), z.getGreen(), z.getBlue(), 150));
        img.fillRect(zoneCenter - 20, 46, 40, 28);             // golden zone
        img.setColor(Color.WHITE);                             // the mirror: a diamond shape
        int mx = (int) mirrorX;
        img.fillPolygon(new int[]{mx, mx + 10, mx, mx - 10}, new int[]{44, 60, 76, 60}, 4);
        drawText(img, "Cermin " + hits + "/3", 14, Color.WHITE, W / 2, 80);
    }
}
