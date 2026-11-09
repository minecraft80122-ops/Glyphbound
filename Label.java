import greenfoot.*;

/**
 * Label - an Actor whose image is just a piece of text.
 *
 * Greenfoot has world.showText(), but a Label actor is more flexible: we can
 * choose colour and size, and place it in front/behind other actors.
 * This class is a small example of REUSE: many worlds use Label.
 */
public class Label extends Actor {

    private int size;
    private Color color;
    private Color background;
    private String currentText = null;   // remembers the last text so we skip needless redraws

    /** Text on a transparent background. */
    public Label(String text, int size, Color color) {
        this(text, size, color, new Color(0, 0, 0, 0));
    }

    /** Text on a coloured background (handy for readable HUD text over a busy map). */
    public Label(String text, int size, Color color, Color background) {
        this.size = size;
        this.color = color;
        this.background = background;
        setText(text);
    }

    /** Change the displayed text. */
    public void setText(String text) {
        if (text == null || text.length() == 0) {
            text = " ";                       // an empty string would make a 0-pixel image
        }
        if (text.equals(currentText)) {
            return;                           // same text as before: nothing to do
        }
        currentText = text;
        // This constructor renders text into an image. Last argument = transparent background.
        setImage(new GreenfootImage(text, size, color, background));
    }
}
