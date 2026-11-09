import greenfoot.*;

/**
 * BattleHero - Raden or Dyah standing on the left side of the battle screen.
 * Both are visible at the same time ("pertarungan kolaborasi dua pahlawan").
 * It only handles ANIMATION: idle breathing, an attack animation when one of
 * their cards is played, a guard pose and a blink when the party is hurt.
 */
public class BattleHero extends Actor {

    private HeroAnimator animator;
    private int guardTimer = 0;
    private int hurtTimer = 0;

    public BattleHero(boolean isRaden) {
        animator = new HeroAnimator(CharacterArt.get(isRaden, 1.3));      // big sprites for the battle screen
        setImage(animator.next(true));
    }

    public void act() {
        animator.setState(guardTimer > 0 ? HeroAnimator.State.GUARD : HeroAnimator.State.IDLE);
        if (guardTimer > 0) guardTimer--;
        setImage(animator.next(true));
        getImage().setTransparency(hurtTimer > 0 && hurtTimer % 6 < 3 ? 110 : 255);
        if (hurtTimer > 0) hurtTimer--;
    }

    /** Play the attack animation once. */
    public void attack() {
        animator.startAttack();
    }

    /** Hold the guard pose for a number of frames. */
    public void guard(int frames) {
        guardTimer = frames;
    }

    /** Blink for a moment. */
    public void hurt() {
        hurtTimer = 24;
    }
}
