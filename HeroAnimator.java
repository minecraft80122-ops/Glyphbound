import greenfoot.*;

/**
 * HeroAnimator - plays the frames of a CharacterArt. It replaces the animTimer /
 * currFrame code that used to live inside Player.java, so Player, Explorer, Raden and
 * Dyah can all share it (one piece of code, four users = no copy-paste).
 *
 * Usage inside an Actor's act():
 *     animator.setState(HeroAnimator.State.WALK);
 *     setImage(animator.next(facingRight));
 */
public class HeroAnimator {

    /** The animations a hero can play. */
    public enum State { IDLE, WALK, ATTACK, GUARD }

    private CharacterArt art;
    private State state = State.IDLE;
    private int frame = 0;
    private int tick = 0;

    public HeroAnimator(CharacterArt art) {
        this.art = art;
    }

    /** Swap to another character's art (used when pressing Q). */
    public void setArt(CharacterArt art) {
        this.art = art;
        frame = 0;
    }

    /** Request an animation. Ignored while an attack animation is still playing. */
    public void setState(State newState) {
        if (state == State.ATTACK) return;       // let the attack finish first
        if (newState != state) {
            state = newState;
            frame = 0;
            tick = 0;
        }
    }

    public void startAttack() {
        state = State.ATTACK;
        frame = 0;
        tick = 0;
    }

    public boolean isAttacking() {
        return state == State.ATTACK;
    }

    /** Advance the animation by one game frame and return the picture to show. */
    public GreenfootImage next(boolean facingRight) {
        GreenfootImage[] frames = framesFor(state, facingRight);
        int delay = delayFor(state);

        tick++;
        if (tick >= delay) {
            tick = 0;
            frame++;
        }
        if (frame >= frames.length) {
            if (state == State.ATTACK) {          // attack played once -> go back to idle
                state = State.IDLE;
                frames = framesFor(state, facingRight);
                frame = 0;
            } else if (state == State.GUARD) {
                frame = frames.length - 1;        // hold the last guard pose
            } else {
                frame = 0;                        // idle/walk loop forever
            }
        }
        return frames[Math.min(frame, frames.length - 1)];
    }

    private GreenfootImage[] framesFor(State s, boolean right) {
        switch (s) {
            case WALK:   return right ? art.walkR : art.walkL;
            case ATTACK: return right ? art.attackR : art.attackL;
            case GUARD:  return right ? art.guardR : art.guardL;
            default:     return right ? art.idleR : art.idleL;
        }
    }

    /** How many game frames each animation frame lasts (bigger = slower). */
    private int delayFor(State s) {
        switch (s) {
            case WALK:   return 4;
            case ATTACK: return 3;
            case GUARD:  return 5;
            default:     return 8;
        }
    }
}
