import greenfoot.*;

/**
 * Player - the hero on the overworld map (your original class, rebuilt).
 *
 * What changed compared to your first version:
 *  - walks with WASD *or* arrow keys, diagonal movement is normalised (not faster)
 *  - collision: the ground under the feet must be walkable (see MyWorld.isWalkable)
 *  - extends MapActor, so the camera positions it on screen
 *  - Q swaps between Raden and Dyah, E talks to people / enters gates
 *  - animation is done by HeroAnimator (your idle / walk sprite sheets)
 */
public class Player extends MapActor {

    private static final double SPEED = 3.0;
    private static final double ART_SCALE = 0.5;       // overworld characters are smaller than in temples

    private boolean isRaden = true;
    private boolean facingRight = true;
    private boolean qWasDown = false;
    private boolean eWasDown = true;                   // true at start so a held key doesn't trigger instantly
    private CharacterArt radenArt, dyahArt;
    private HeroAnimator animator;

    public Player() {
        radenArt = CharacterArt.get(true, ART_SCALE);
        dyahArt = CharacterArt.get(false, ART_SCALE);
        animator = new HeroAnimator(radenArt);
        setImage(animator.next(true));
    }

    public void act() {
        MyWorld world = (MyWorld) getWorld();

        if (world.isDialogueOpen()) {                  // frozen while someone is talking
            animator.setState(HeroAnimator.State.IDLE);
            setImage(animator.next(facingRight));
            return;
        }
        handleSwitch();
        boolean moving = handleMovement(world);
        handleInteract(world);

        animator.setState(moving ? HeroAnimator.State.WALK : HeroAnimator.State.IDLE);
        setImage(animator.next(facingRight));
    }

    private void handleSwitch() {
        boolean qDown = Greenfoot.isKeyDown("q");
        if (qDown && !qWasDown) {
            isRaden = !isRaden;
            animator.setArt(isRaden ? radenArt : dyahArt);
            getWorld().addObject(new FloatingText(isRaden ? "Raden" : "Dyah", 20, Color.WHITE),
                    getX(), getY() - 40);
        }
        qWasDown = qDown;
    }

    /** @return true if the hero is walking this frame */
    private boolean handleMovement(MyWorld world) {
        double dx = 0, dy = 0;
        if (Greenfoot.isKeyDown("left") || Greenfoot.isKeyDown("a")) dx -= 1;
        if (Greenfoot.isKeyDown("right") || Greenfoot.isKeyDown("d")) dx += 1;
        if (Greenfoot.isKeyDown("up") || Greenfoot.isKeyDown("w")) dy -= 1;
        if (Greenfoot.isKeyDown("down") || Greenfoot.isKeyDown("s")) dy += 1;
        if (dx == 0 && dy == 0) return false;

        if (dx != 0 && dy != 0) {                      // diagonal: scale by 1/sqrt(2) so speed stays equal
            dx *= 0.7071;
            dy *= 0.7071;
        }
        if (dx != 0) facingRight = dx > 0;

        // Try X and Y separately so the hero can slide along walls instead of getting stuck.
        double newX = mapX + dx * SPEED;
        if (canStandAt(world, newX, mapY)) mapX = newX;
        double newY = mapY + dy * SPEED;
        if (canStandAt(world, mapX, newY)) mapY = newY;
        return true;
    }

    /** The feet are about 12px wide: check left foot, right foot and centre. */
    private boolean canStandAt(MyWorld world, double x, double y) {
        return world.isWalkable(x - 6, y) && world.isWalkable(x + 6, y) && world.isWalkable(x, y)
                && !world.isBlockedByNpc(x, y);
    }

    private void handleInteract(MyWorld world) {
        boolean eDown = Greenfoot.isKeyDown("e") || Greenfoot.isKeyDown("enter");
        if (eDown && !eWasDown) {
            Interactable target = world.findInteractable(mapX, mapY);
            if (target != null) {
                target.interact(world);
            }
        }
        eWasDown = eDown;
    }

    public boolean isRaden() {
        return isRaden;
    }
}
