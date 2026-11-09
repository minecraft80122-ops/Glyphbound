import greenfoot.*;
import java.util.List;

/**
 * Explorer - the ONE visible hero inside temple puzzle levels.
 *
 * Raden and Dyah are one Actor with a flag (isRaden). Q flips the flag and swaps the
 * artwork. Different abilities:
 *   Raden: slash wooden barricades, push crates          (SPACE)
 *   Dyah : shoot arrows at far targets over water        (SPACE)
 * Animation is handled by a HeroAnimator (idle / walk / attack frames from the sprite sheets).
 */
public class Explorer extends Actor {

    private static final int SPEED = 3;
    private static final double ART_SCALE = 0.65;      // size of the sprites inside temples

    private boolean isRaden = true;
    private int facingX = 1, facingY = 0;              // direction we look (for aiming)
    private boolean facingRight = true;                // which way the picture faces
    private int actionCooldown = 0;
    private int hintCooldown = 0;
    private boolean qWasDown = false;
    private boolean moving = false;

    private CharacterArt radenArt, dyahArt;
    private HeroAnimator animator;

    public Explorer() {
        radenArt = CharacterArt.get(true, ART_SCALE);
        dyahArt = CharacterArt.get(false, ART_SCALE);
        animator = new HeroAnimator(radenArt);
        setImage(animator.next(true));
    }

    public void act() {
        handleSwitch();
        handleMovement();
        handleAction();
        updateAnimation();
        if (actionCooldown > 0) actionCooldown--;
        if (hintCooldown > 0) hintCooldown--;
    }

    private void updateAnimation() {
        animator.setState(moving ? HeroAnimator.State.WALK : HeroAnimator.State.IDLE);
        setImage(animator.next(facingRight));
    }

    // ------------------------------------------------------------------ switching
    private void handleSwitch() {
        boolean qDown = Greenfoot.isKeyDown("q");
        if (qDown && !qWasDown) {
            isRaden = !isRaden;
            animator.setArt(isRaden ? radenArt : dyahArt);
            ((ExplorationWorld) getWorld()).setHudCharacter(isRaden);
            getWorld().addObject(new FloatingText(isRaden ? "Raden" : "Dyah", 20, Color.WHITE),
                    getX(), getY() - 45);
        }
        qWasDown = qDown;
    }

    // ------------------------------------------------------------------ movement
    private void handleMovement() {
        int dx = 0, dy = 0;
        if (Greenfoot.isKeyDown("left") || Greenfoot.isKeyDown("a")) dx -= SPEED;
        if (Greenfoot.isKeyDown("right") || Greenfoot.isKeyDown("d")) dx += SPEED;
        if (Greenfoot.isKeyDown("up") || Greenfoot.isKeyDown("w")) dy -= SPEED;
        if (Greenfoot.isKeyDown("down") || Greenfoot.isKeyDown("s")) dy += SPEED;

        moving = (dx != 0 || dy != 0);
        if (!moving) return;

        facingX = Integer.signum(dx);
        facingY = Integer.signum(dy);
        if (dx != 0) facingRight = dx > 0;            // only left/right changes the picture direction

        if (dx != 0) tryMove(dx, 0);
        if (dy != 0) tryMove(0, dy);
    }

    private void tryMove(int dx, int dy) {
        int oldX = getX();
        int oldY = getY();
        setLocation(oldX + dx, oldY + dy);

        if (isRaden) {
            List<Crate> crates = getIntersectingObjects(Crate.class);
            for (Crate c : crates) {
                c.tryPush(dx, dy);
            }
        }

        if (isTouching(Obstacle.class) || isOutOfBounds()) {
            boolean heavy = isTouching(Barricade.class) || isTouching(Crate.class);
            boolean gate = isTouching(Gate.class);
            setLocation(oldX, oldY);
            showHint(heavy, gate);
        }
    }

    private boolean isOutOfBounds() {
        return getX() < 20 || getX() > getWorld().getWidth() - 20
                || getY() < Scenery.HORIZON + 20 || getY() > getWorld().getHeight() - 30;
    }

    private void showHint(boolean heavy, boolean gate) {
        if (hintCooldown > 0) return;
        String msg = null;
        if (heavy && !isRaden) msg = "Terlalu berat! Tekan Q untuk Raden";
        else if (gate) msg = "Gerbang terkunci - cari sasaran!";
        if (msg != null) {
            getWorld().addObject(new FloatingText(msg, 18, Color.WHITE), getX(), getY() - 45);
            hintCooldown = 90;
        }
    }

    // ------------------------------------------------------------------ actions
    private void handleAction() {
        if (!Greenfoot.isKeyDown("space") || actionCooldown > 0) return;
        if (isRaden) slash(); else shoot();
    }

    private void slash() {
        actionCooldown = 25;
        animator.startAttack();
        int angle = (int) Math.toDegrees(Math.atan2(facingY, facingX));
        getWorld().addObject(new SlashEffect(angle, Color.WHITE),
                getX() + facingX * 36, getY() + facingY * 36);
        List<Barricade> nearby = getObjectsInRange(95, Barricade.class);
        for (Barricade b : nearby) {
            b.slash();
        }
    }

    private void shoot() {
        actionCooldown = 30;
        animator.startAttack();
        getWorld().addObject(new Arrow(facingX, facingY, 12),
                getX() + facingX * 28, getY() + facingY * 28);
    }
}
