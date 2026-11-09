import greenfoot.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DialogueBox - a text box with a typewriter effect. Press E / ENTER / SPACE to
 * show the whole line instantly, or to go to the next line. When the last line is
 * finished it asks MyWorld to remove it.
 */
public class DialogueBox extends Actor {

    private static final int W = 760, H = 124, MAX_CHARS = 66;

    private String speaker;
    private String[] lines;
    private int lineIndex = 0;
    private int shown = 0;               // how many characters of the current line are visible
    private int tick = 0;
    private int age = 0;
    private boolean keyWasDown = true;   // starts true: the key that OPENED the box must be released first

    public DialogueBox(String speaker, String[] lines) {
        this.speaker = speaker;
        this.lines = lines;
        redraw();
    }

    public void act() {
        age++;
        String full = lines[lineIndex];

        if (shown < full.length()) {                       // typewriter: one new letter every 2 frames
            tick++;
            if (tick % 2 == 0) {
                shown++;
                redraw();
            }
        }

        boolean down = Greenfoot.isKeyDown("e") || Greenfoot.isKeyDown("enter") || Greenfoot.isKeyDown("space");
        if (down && !keyWasDown && age > 6) {              // a fresh key PRESS
            if (shown < full.length()) {
                shown = full.length();                     // first press: finish the line
                redraw();
            } else {
                lineIndex++;                               // second press: next line
                shown = 0;
                if (lineIndex >= lines.length) {
                    ((MyWorld) getWorld()).closeDialogue();
                    return;
                }
                redraw();
            }
        }
        keyWasDown = down;
    }

    private void redraw() {
        GreenfootImage g = new GreenfootImage(W, H);
        g.setColor(new Color(15, 12, 22, 230));
        g.fillRect(0, 0, W, H);
        g.setColor(new Color(220, 180, 80));
        g.drawRect(0, 0, W - 1, H - 1);
        g.drawRect(3, 3, W - 7, H - 7);

        g.drawImage(new GreenfootImage(speaker, 20, new Color(255, 215, 110), new Color(0, 0, 0, 0)), 18, 10);

        // Wrap the FULL line into rows, then reveal only the first 'shown' characters.
        List<String> rows = wrap(lines[lineIndex], MAX_CHARS);
        int left = shown;
        for (int i = 0; i < rows.size() && left > 0; i++) {
            String row = rows.get(i);
            String part = row.substring(0, Math.min(row.length(), left));
            if (part.length() > 0) {
                g.drawImage(new GreenfootImage(part, 19, Color.WHITE, new Color(0, 0, 0, 0)), 18, 42 + i * 24);
            }
            left -= row.length() + 1;                      // +1 for the space that was replaced by the line break
        }

        if (shown >= lines[lineIndex].length()) {          // hint that we can continue
            g.drawImage(new GreenfootImage("E >", 15, new Color(255, 215, 110), new Color(0, 0, 0, 0)), W - 50, H - 24);
        }
        setImage(g);
    }

    /** Break a long text into rows of at most maxChars characters, only at spaces. */
    private List<String> wrap(String text, int maxChars) {
        List<String> rows = new ArrayList<String>();
        String[] words = text.split(" ");
        String current = "";
        for (String word : words) {
            if (current.length() > 0 && current.length() + 1 + word.length() > maxChars) {
                rows.add(current);                         // row is full: start a new one
                current = word;
            } else {
                current = (current.length() == 0) ? word : current + " " + word;
            }
        }
        if (current.length() > 0) rows.add(current);
        return rows;
    }
}
