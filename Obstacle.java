import greenfoot.*;

/**
 * Obstacle - the PARENT class of everything solid in the exploration levels
 * (walls, water, crates, gates, targets...).
 *
 * Why make a parent class? Because the player only needs ONE rule:
 *     "if I touch an Obstacle, I can't walk there".
 * It doesn't care which kind of obstacle it is. That is POLYMORPHISM.
 *
 * Children can OVERRIDE (re-define) the two methods below to behave differently.
 */
public abstract class Obstacle extends Actor {

    /** Does an arrow stop when it hits me? Default: yes. Water overrides this with 'no'. */
    public boolean blocksArrows() {
        return true;
    }

    /** Called by an Arrow that hit me. Default: do nothing. Target overrides this. */
    public void onArrowHit() {
    }
}
