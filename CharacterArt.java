import greenfoot.*;
import java.util.HashMap;

/**
 * CharacterArt - ALL the animation frames of ONE character (idle, walk, attack, guard),
 * each in a right-facing and a left-facing version.
 *
 * Loading and scaling images is slow, so we keep finished sets in a static HashMap
 * (a "cache"): the first call builds the set, later calls reuse it.
 *
 * To use your own final art: put new sheets in the images folder and change the
 * file names in get(...). Keep the same layout (128px frames side by side).
 */
public class CharacterArt {

    public GreenfootImage[] idleR, idleL, walkR, walkL, attackR, attackL, guardR, guardL;

    // key like "raden@0.65" -> finished set
    private static HashMap<String, CharacterArt> cache = new HashMap<String, CharacterArt>();

    private CharacterArt(String idle, String walk, String attack, String guard, double scale) {
        // Idle / walk / guard: narrow crop (the body only). Attack: wide crop (arm + book/sword).
        // Both crops are centred on x=64 so the body doesn't "jump" when the animation changes.
        idleR = SpriteSheet.load(idle, 128, 36, 50, 56, 78, scale, true);
        walkR = SpriteSheet.load(walk, 128, 36, 50, 56, 78, scale, true);
        guardR = SpriteSheet.load(guard, 128, 36, 50, 56, 78, scale, true);
        attackR = SpriteSheet.load(attack, 128, 12, 50, 104, 78, scale, true);
        idleL = SpriteSheet.mirrored(idleR);
        walkL = SpriteSheet.mirrored(walkR);
        guardL = SpriteSheet.mirrored(guardR);
        attackL = SpriteSheet.mirrored(attackR);
    }

    /** Get the art for Raden (isRaden = true) or Dyah (false) at the given scale. */
    public static CharacterArt get(boolean isRaden, double scale) {
        String key = (isRaden ? "raden@" : "dyah@") + scale;
        CharacterArt art = cache.get(key);
        if (art == null) {                                    // not built yet: build and remember
            if (isRaden) {
                art = new CharacterArt("raden/Idle.png", "raden/Walk.png", "raden/Attack.png", "raden/Protection.png", scale);
            } else {
                art = new CharacterArt("Test_Player_Idle.png", "Test_Player_Walk.png", "testPlayer/Attack.png", "testPlayer/Protection.png", scale);
            }
            cache.put(key, art);
        }
        return art;
    }
}
