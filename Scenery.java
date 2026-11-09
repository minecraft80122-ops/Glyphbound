import greenfoot.*;
import java.util.Random;

/**
 * Scenery - draws the BACKGROUND pictures for each area, all with code
 * (so the project needs no image files at all!).
 *
 * Every method is static (we never create a Scenery object; it is just a toolbox).
 * The picture is built in layers, like a painter would: sky -> far scenery -> floor.
 *
 * GreenfootImage drawing cheat-sheet used below:
 *   setColor(c)             choose the "paint" colour
 *   fillRect(x,y,w,h)       filled rectangle (x,y = top-left corner)
 *   fillOval(x,y,w,h)       filled ellipse inside that rectangle
 *   drawLine(x1,y1,x2,y2)   a line
 *   fillPolygon(xs,ys,n)    filled shape from n corner points
 * Remember: in Greenfoot (0,0) is the TOP-left and y grows DOWNWARDS.
 */
public class Scenery {

    // The "horizon": everything above this y is backdrop, below it is walkable floor.
    public static final int HORIZON = 120;

    /** Build the complete background image for an area. */
    public static GreenfootImage create(Element area, int w, int h) {
        GreenfootImage img = new GreenfootImage(w, h);
        paintSky(img, area, w);
        paintBackdrop(img, area, w);
        paintFloor(img, area, w, h);
        return img;
    }

    /** Colour used for walls/gates in each area, so the walls match the scenery. */
    public static Color wallColor(Element area) {
        switch (area) {
            case AIR:      return new Color(95, 125, 120);
            case SURYA:    return new Color(185, 145, 90);
            default:       return new Color(75, 58, 95);
        }
    }

    // ---------- layer 1: the sky (a vertical colour gradient) ----------
    private static void paintSky(GreenfootImage img, Element area, int w) {
        Color top, bottom;
        switch (area) {
            case AIR:   top = new Color(15, 70, 100);  bottom = new Color(110, 200, 190); break;
            case SURYA: top = new Color(240, 140, 60); bottom = new Color(255, 225, 150); break;
            default:    top = new Color(12, 8, 28);    bottom = new Color(65, 32, 85);    break;
        }
        // Draw one horizontal line per row, each with a slightly different colour.
        for (int y = 0; y < HORIZON; y++) {
            double t = (double) y / HORIZON;      // 0.0 at the top ... 1.0 at the horizon
            img.setColor(blend(top, bottom, t));
            img.drawLine(0, y, w, y);
        }
    }

    /** Mix two colours: t=0 gives colour a, t=1 gives colour b. */
    private static Color blend(Color a, Color b, double t) {
        int r = (int) (a.getRed() * (1 - t) + b.getRed() * t);
        int g = (int) (a.getGreen() * (1 - t) + b.getGreen() * t);
        int bl = (int) (a.getBlue() * (1 - t) + b.getBlue() * t);
        return new Color(r, g, bl);
    }

    // ---------- layer 2: distant scenery (temple, tower, cave ceiling) ----------
    private static void paintBackdrop(GreenfootImage img, Element area, int w) {
        Random rng = new Random(7);      // fixed seed = same "random" picture every time
        switch (area) {
            case AIR:
                // A stepped candi (temple) silhouette in the middle.
                img.setColor(new Color(20, 60, 70));
                int cx = w / 2;
                img.fillRect(cx - 130, 95, 260, 25);
                img.fillRect(cx - 95, 70, 190, 25);
                img.fillRect(cx - 60, 45, 120, 25);
                img.fillOval(cx - 20, 14, 40, 44);                 // stupa on top
                // Two simple trees: brown trunk + green leaves.
                for (int i = 0; i < 2; i++) {
                    int tx = (i == 0) ? 90 : w - 90;
                    img.setColor(new Color(70, 45, 25));
                    img.fillRect(tx - 5, 60, 10, 60);
                    img.setColor(new Color(30, 110, 70));
                    img.fillOval(tx - 40, 20, 80, 55);
                }
                break;

            case SURYA:
                // A big glowing sun made of several transparent circles.
                for (int r = 70; r >= 20; r -= 10) {
                    img.setColor(new Color(255, 245, 170, 55));
                    img.fillOval(660 - r, 52 - r, r * 2, r * 2);
                }
                img.setColor(new Color(255, 250, 200));
                img.fillOval(640, 32, 40, 40);
                // A tall tower built from 5 stacked, narrowing blocks.
                for (int i = 0; i < 5; i++) {
                    int bw = 100 - i * 16;
                    img.setColor(new Color(150 - i * 5, 100, 60));
                    img.fillRect(250 - bw / 2, HORIZON - 22 * (i + 1), bw, 22);
                }
                img.setColor(new Color(120, 70, 40));
                img.fillPolygon(new int[]{222, 250, 278}, new int[]{HORIZON - 110, HORIZON - 150, HORIZON - 110}, 3);
                break;

            default:
                // Stalactites (rock spikes) hanging from the cave ceiling.
                for (int x = 0; x < w; x += 45) {
                    int len = 25 + rng.nextInt(60);
                    img.setColor(new Color(30, 22, 45));
                    img.fillPolygon(new int[]{x, x + 42, x + 21}, new int[]{0, 0, len}, 3);
                }
                // Glowing purple crystals near the horizon.
                for (int i = 0; i < 7; i++) {
                    int x = 40 + rng.nextInt(w - 80);
                    img.setColor(new Color(170, 110, 230, 120));
                    img.fillOval(x - 14, HORIZON - 34, 28, 34);
                    img.setColor(new Color(220, 190, 255));
                    img.fillOval(x - 5, HORIZON - 26, 10, 14);
                }
                break;
        }
    }

    // ---------- layer 3: the floor (tiles + decoration) ----------
    private static void paintFloor(GreenfootImage img, Element area, int w, int h) {
        Random rng = new Random(21);
        Color base;
        switch (area) {
            case AIR:   base = new Color(70, 112, 115); break;
            case SURYA: base = new Color(205, 175, 115); break;
            default:    base = new Color(52, 42, 68);   break;
        }
        img.setColor(base);
        img.fillRect(0, HORIZON, w, h - HORIZON);

        // Tile grid: darker lines every 50 pixels.
        img.setColor(base.darker());
        for (int x = 0; x < w; x += 50) {
            img.drawLine(x, HORIZON, x, h);
        }
        for (int y = HORIZON; y < h; y += 50) {
            img.drawLine(0, y, w, y);
        }

        // A few decorations scattered at random (but repeatable) positions.
        for (int i = 0; i < 14; i++) {
            int x = rng.nextInt(w);
            int y = HORIZON + 10 + rng.nextInt(h - HORIZON - 20);
            switch (area) {
                case AIR:     // puddles
                    img.setColor(new Color(90, 170, 200, 110));
                    img.fillOval(x, y, 30 + rng.nextInt(30), 12);
                    break;
                case SURYA:   // sand specks
                    img.setColor(new Color(235, 210, 150));
                    img.fillOval(x, y, 6, 3);
                    break;
                default:      // glowing cracks
                    img.setColor(new Color(130, 80, 180, 140));
                    img.drawLine(x, y, x + 20, y + 8);
                    break;
            }
        }

        // A soft dark line at the horizon makes the floor look like it starts there.
        img.setColor(new Color(0, 0, 0, 90));
        img.fillRect(0, HORIZON, w, 4);
    }
}
