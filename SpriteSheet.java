import greenfoot.*;

/**
 * SpriteSheet - a small toolbox that cuts a "sprite sheet" (one long PNG with many
 * animation frames side by side) into separate GreenfootImages.
 *
 * Your original Player.java did this with getAwtImage(); this version uses only
 * Greenfoot's own methods, and adds three things:
 *   - CROP:   your frames are 128x128 but the character only fills the middle, so we
 *             cut away the empty space (this makes collisions much more accurate)
 *   - SCALE:  shrink the frames so the character fits the map's scale
 *   - SHADOW: draw a soft shadow under the feet so the character looks "grounded"
 */
public class SpriteSheet {

    /**
     * @param file    image file name in the images folder
     * @param frameW  width of ONE frame inside the sheet (128 for your sheets)
     * @param cropX   left edge of the part to keep (inside each frame)
     * @param cropY   top edge of the part to keep
     * @param cropW   width of the part to keep
     * @param cropH   height of the part to keep
     * @param scale   1.0 = original size, 0.5 = half size
     * @param shadow  draw a shadow under the feet?
     */
    public static GreenfootImage[] load(String file, int frameW, int cropX, int cropY,
                                        int cropW, int cropH, double scale, boolean shadow) {
        GreenfootImage sheet = new GreenfootImage(file);
        int count = sheet.getWidth() / frameW;                 // how many frames fit in the sheet
        GreenfootImage[] frames = new GreenfootImage[count];

        for (int i = 0; i < count; i++) {
            GreenfootImage f = new GreenfootImage(cropW, cropH);
            if (shadow) {                                      // shadow first, so the feet cover it
                f.setColor(new Color(0, 0, 0, 80));
                f.fillOval(cropW / 2 - 17, cropH - 10, 34, 11);
            }
            // Drawing the WHOLE sheet at a negative offset slides frame i into view;
            // anything outside the small image is simply clipped away.
            f.drawImage(sheet, -(i * frameW + cropX), -cropY);
            f.scale(Math.max(1, (int) (cropW * scale)), Math.max(1, (int) (cropH * scale)));
            frames[i] = f;
        }
        return frames;
    }

    /** Make a left-facing copy of every frame (so we can show a character walking left). */
    public static GreenfootImage[] mirrored(GreenfootImage[] source) {
        GreenfootImage[] result = new GreenfootImage[source.length];
        for (int i = 0; i < source.length; i++) {
            result[i] = new GreenfootImage(source[i]);          // copy first, never flip the original!
            result[i].mirrorHorizontally();
        }
        return result;
    }
}
