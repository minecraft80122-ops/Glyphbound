import greenfoot.*;

/**
 * Npc - ABSTRACT parent of every villager you can talk to.
 *
 * It does the common work (animation, turning toward the player, the dialogue prompt).
 * Each child only supplies WHO it is and WHAT it says by implementing getLines().
 * To add a new villager: copy one of the small child classes, change the text. That's it!
 *
 * 'implements Interactable' = this class keeps the promise of that interface.
 */
public abstract class Npc extends MapActor implements Interactable {

    private String name;
    private GreenfootImage[] right, left;
    private int frame = 0;
    private int tick = 0;
    private boolean facingRight = true;

    public Npc(String name, String sheetFile) {
        this.name = name;
        right = SpriteSheet.load(sheetFile, 128, 36, 50, 56, 78, 0.5, true);
        left = SpriteSheet.mirrored(right);
        setImage(right[0]);
    }

    public void act() {
        // Turn toward the player.
        MyWorld world = (MyWorld) getWorld();
        facingRight = world.getPlayer().getMapX() >= mapX;

        tick++;
        if (tick >= 9) {
            tick = 0;
            frame = (frame + 1) % right.length;       // % wraps 8 -> 0, so the idle loop repeats
        }
        setImage(facingRight ? right[frame] : left[frame]);
    }

    /** What this villager says RIGHT NOW (may depend on game progress). Each child writes this. */
    protected abstract String[] getLines();

    public String getPrompt() {
        return "E: bicara dengan " + name;
    }

    public void interact(MyWorld world) {
        world.openDialogue(name, getLines());
    }
}
