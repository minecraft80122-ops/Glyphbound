import greenfoot.*;

/**
 * MenuWorld - an ABSTRACT base class for all "press ENTER to continue" screens.
 *
 * ABSTRACT class = a half-finished class. You cannot write "new MenuWorld()".
 * It exists only to be EXTENDED. It contains shared code (the ENTER handling)
 * and declares an abstract method onConfirm() that every child MUST implement.
 * This is INHERITANCE + the "template method" idea: parent controls the flow,
 * children fill in the blanks.
 */
public abstract class MenuWorld extends World {

    // Without this, an ENTER press that was still held down from the previous
    // screen would instantly skip the new screen.
    private boolean enterReleased = false;

    public MenuWorld() {
        // 'super(...)' calls the constructor of the parent class (World).
        super(GameState.WIDTH, GameState.HEIGHT, 1);
    }

    /** Runs every frame (Greenfoot calls act() automatically). */
    public void act() {
        if (!Greenfoot.isKeyDown("enter")) {
            enterReleased = true;               // the key has been let go at least once
        } else if (enterReleased) {
            onConfirm();                        // children decide what happens
        }
    }

    /** Called when the player presses ENTER. Each child class defines it. */
    protected abstract void onConfirm();

    /** Helper: put a horizontally-centred line of text on screen at height y. */
    protected void addText(String text, int size, Color color, int y) {
        addObject(new Label(text, size, color), getWidth() / 2, y);
    }
}
