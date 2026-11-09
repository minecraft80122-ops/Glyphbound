import greenfoot.*;

/**
 * Puzzle for the BAYANGAN glyph: "remember the shadows".
 * Three orbs light up in a sequence of 4. Watch, then repeat it with keys 1, 2, 3.
 * A mistake replays the sequence.
 *
 * It has two PHASES; the boolean 'showing' says which one we are in
 * (a tiny "state machine").
 */
public class ShadowMemoryPuzzle extends GlyphPuzzle {

    private int[] sequence = new int[4];       // orb numbers 0..2
    private boolean showing = true;            // true = demonstration, false = player's turn
    private int showFrame = 0;                 // frames since the demonstration began
    private int index = 0;                     // player's progress
    private int litOrb = -1;                   // which orb is bright right now (-1 = none)
    private int litTimer = 0;                  // how long a pressed orb stays bright

    public ShadowMemoryPuzzle() {
        super(Element.BAYANGAN, "Ingat urutan bayangan, lalu tekan 1 2 3", 600);
        for (int i = 0; i < sequence.length; i++) {
            sequence[i] = Greenfoot.getRandomNumber(3);
        }
    }

    @Override
    protected void updatePuzzle(String key) {
        if (litTimer > 0) {
            litTimer--;
            if (litTimer == 0) litOrb = -1;
        }

        if (showing) {
            showFrame++;
            int step = showFrame / 45;                         // each orb is shown for 45 frames
            int within = showFrame % 45;
            if (step >= sequence.length) {
                showing = false;                               // demonstration over: your turn
                litOrb = -1;
            } else {
                litOrb = (within < 32) ? sequence[step] : -1;  // lit for 32 frames, dark for 13
            }
            return;                                            // ignore keys while demonstrating
        }

        if (key == null) return;
        int pressed = -1;
        if (key.equals("1")) pressed = 0;
        else if (key.equals("2")) pressed = 1;
        else if (key.equals("3")) pressed = 2;
        if (pressed == -1) return;                             // some other key: ignore

        litOrb = pressed;                                      // flash the orb you pressed
        litTimer = 10;
        if (pressed == sequence[index]) {
            index++;
            if (index >= sequence.length) finish(true);
        } else {
            penalty(30);                                       // wrong: watch again
            showing = true;
            showFrame = 0;
            index = 0;
        }
    }

    @Override
    protected void drawContent(GreenfootImage img) {
        for (int i = 0; i < 3; i++) {
            int cx = 70 + i * 90;
            boolean lit = (i == litOrb);
            Color c = element.getColor();
            img.setColor(lit ? c : new Color(c.getRed() / 4, c.getGreen() / 4, c.getBlue() / 4));
            img.fillOval(cx - 22, 36, 44, 44);
            img.setColor(lit ? Color.WHITE : new Color(110, 110, 120));
            img.drawOval(cx - 22, 36, 44, 44);
            drawText(img, "" + (i + 1), 18, lit ? Color.WHITE : new Color(150, 150, 160), cx, 48);
        }
        drawText(img, showing ? "Perhatikan..." : "Giliranmu: " + index + "/4", 14, Color.WHITE, 285, 52);
    }
}
