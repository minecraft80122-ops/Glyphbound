import greenfoot.*;

/**
 * Vignette - a transparent picture that darkens the screen corners, drawn on top of the
 * map. It is a cheap trick that makes any scene look more "cinematic".
 * We paint a small 100x63 image pixel by pixel, then enlarge it to full screen.
 */
public class Vignette extends Actor {

    public Vignette(int width, int height) {
        int w = 100, h = 63;
        GreenfootImage g = new GreenfootImage(w, h);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                double nx = (x - w / 2.0) / (w / 2.0);          // -1 .. 1 across the image
                double ny = (y - h / 2.0) / (h / 2.0);
                double d = Math.sqrt(nx * nx + ny * ny);        // 0 at the centre, ~1.4 in the corners
                int alpha = (int) (Math.max(0, d - 0.6) / 0.85 * 150);
                g.setColorAt(x, y, new Color(10, 5, 20, Math.min(160, alpha)));
            }
        }
        g.scale(width, height);
        setImage(g);
    }
}
