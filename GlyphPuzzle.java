import greenfoot.*;

/**
 * GlyphPuzzle - ABSTRACT parent of the quick puzzles you solve in the middle of a fight.
 *
 * The parent handles what ALL puzzles share: the panel, the title, the countdown
 * timer bar, reading the keyboard, and telling the BattleWorld when it's over.
 * Each child puzzle only implements two methods:
 *     updatePuzzle(key)   - the RULES (what happens when a key is pressed)
 *     drawContent(img)    - the LOOK of the middle of the panel
 *
 * The fight never pauses while the puzzle is open, so enemies keep attacking!
 */
public abstract class GlyphPuzzle extends Actor {

    protected static final int W = 320;
    protected static final int H = 112;

    protected final Element element;
    protected int timeLeft;                    // frames remaining
    private final int timeLimit;
    private int age = 0;                       // frames since the puzzle appeared
    private boolean finished = false;
    private GreenfootImage titleImage;

    public GlyphPuzzle(Element element, String title, int timeLimit) {
        this.element = element;
        this.timeLimit = timeLimit;
        this.timeLeft = timeLimit;
        titleImage = new GreenfootImage(title, 15, Color.WHITE, new Color(0, 0, 0, 0));
        // PITFALL (important Java lesson): we must NOT call paintPanel() here!
        // paintPanel() calls the child's drawContent(), but while THIS constructor runs
        // the child's own fields (like 'sequence') do not exist yet -> NullPointerException.
        // Instead, the first act() call paints the panel, when everything is ready.
    }

    public void act() {
        if (finished) return;
        age++;
        timeLeft--;

        // Always read the key (so old presses don't pile up), but ignore the first
        // 20 frames: that is the 'E' key that was used to OPEN the puzzle.
        String key = Greenfoot.getKey();
        if (age <= 20) key = null;

        updatePuzzle(key);                     // child's rules
        if (finished) return;                  // the child may have finished the puzzle

        if (timeLeft <= 0) {
            finish(false);                     // out of time
            return;
        }
        paintPanel();
    }

    /** Child puzzles call this when solved (true) or failed (false). */
    protected void finish(boolean success) {
        if (finished) return;
        finished = true;
        ((BattleWorld) getWorld()).puzzleFinished(success);   // the world removes us
    }

    /** Subtract time as a penalty for mistakes. */
    protected void penalty(int frames) {
        timeLeft -= frames;
    }

    // ---- child classes implement these two ----
    protected abstract void updatePuzzle(String key);

    protected abstract void drawContent(GreenfootImage img);

    // ---- drawing shared by every puzzle ----
    private void paintPanel() {
        GreenfootImage img = new GreenfootImage(W, H);
        img.setColor(new Color(15, 15, 25, 225));
        img.fillRect(0, 0, W, H);
        img.setColor(element.getColor());
        img.drawRect(0, 0, W - 1, H - 1);
        img.drawRect(1, 1, W - 3, H - 3);
        img.drawImage(titleImage, (W - titleImage.getWidth()) / 2, 6);   // centred title

        drawContent(img);                                                 // child's part

        // Time bar at the bottom.
        double fraction = Math.max(0, (double) timeLeft / timeLimit);
        img.setColor(new Color(60, 60, 60));
        img.fillRect(10, H - 14, W - 20, 6);
        img.setColor(fraction > 0.3 ? element.getColor() : new Color(230, 70, 70));
        img.fillRect(10, H - 14, (int) ((W - 20) * fraction), 6);
        setImage(img);
    }

    /** Helper for children: write centred text at (cx, y) on the panel. */
    protected void drawText(GreenfootImage img, String text, int size, Color color, int cx, int y) {
        GreenfootImage t = new GreenfootImage(text, size, color, new Color(0, 0, 0, 0));
        img.drawImage(t, cx - t.getWidth() / 2, y);
    }
}
