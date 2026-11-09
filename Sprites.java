import greenfoot.*;

/**
 * Sprites - draws the ENEMIES with code (the heroes use the real sprite sheets, see CharacterArt).
 * Each method creates a blank GreenfootImage and "paints" shapes onto it.
 * Tip for learning: change numbers/colours here and run again to see what moves!
 */
public class Sprites {

    /** Portuguese soldier: dark tunic, steel helmet, spear. (8px free space on top for the health bar.) */
    public static GreenfootImage soldier() {
        GreenfootImage g = new GreenfootImage(40, 58);
        g.setColor(new Color(50, 40, 40));    g.fillRect(12, 46, 6, 10); g.fillRect(22, 46, 6, 10);
        g.setColor(new Color(60, 70, 120));   g.fillRect(9, 28, 22, 20);
        g.setColor(new Color(235, 185, 140)); g.fillOval(12, 14, 16, 15);
        g.setColor(new Color(170, 175, 185)); g.fillOval(10, 10, 20, 11); g.fillRect(10, 16, 20, 3);  // helmet
        g.setColor(new Color(200, 60, 60));   g.fillRect(19, 6, 3, 6);                                // crest
        g.setColor(new Color(120, 80, 40));   g.drawLine(34, 14, 34, 54);                             // spear shaft
        g.setColor(Color.LIGHT_GRAY);         g.fillPolygon(new int[]{32, 36, 34}, new int[]{14, 14, 6}, 3);
        return g;
    }

    /** Musketeer: red coat, wide hat, long gun. */
    public static GreenfootImage musketeer() {
        GreenfootImage g = new GreenfootImage(44, 58);
        g.setColor(new Color(50, 40, 40));    g.fillRect(14, 46, 6, 10); g.fillRect(24, 46, 6, 10);
        g.setColor(new Color(170, 55, 45));   g.fillRect(11, 28, 22, 20);
        g.setColor(new Color(235, 185, 140)); g.fillOval(14, 14, 16, 15);
        g.setColor(new Color(70, 50, 35));    g.fillRect(8, 16, 28, 3); g.fillRect(14, 8, 16, 9);    // hat
        g.setColor(new Color(90, 90, 95));    g.fillRect(2, 36, 40, 3);                              // musket
        g.setColor(new Color(110, 70, 35));   g.fillRect(2, 34, 10, 6);                              // gun stock
        return g;
    }

    /** Naga Tirta: a blue-green water serpent made of overlapping circles. */
    public static GreenfootImage naga() {
        GreenfootImage g = new GreenfootImage(110, 80);
        for (int i = 0; i < 6; i++) {                              // body segments follow a sine wave
            int y = 40 + (int) (12 * Math.sin(i * 1.1));
            g.setColor(new Color(30, 120 + i * 12, 150));
            g.fillOval(4 + i * 14, y, 28, 28);
        }
        g.setColor(new Color(40, 190, 190));  g.fillOval(78, 22, 30, 26);                           // head
        g.setColor(Color.YELLOW);             g.fillOval(94, 28, 7, 7);                             // eye
        g.setColor(Color.BLACK);              g.fillOval(97, 30, 3, 3);
        g.setColor(new Color(230, 230, 200)); g.drawLine(84, 24, 80, 10); g.drawLine(92, 22, 94, 8);  // horns
        return g;
    }

    /** Garuda Surya: golden bird with big wings and a beak. */
    public static GreenfootImage garuda() {
        GreenfootImage g = new GreenfootImage(100, 84);
        g.setColor(new Color(230, 140, 30));
        g.fillPolygon(new int[]{50, 0, 28, 50}, new int[]{40, 14, 56, 56}, 4);                      // left wing
        g.fillPolygon(new int[]{50, 100, 72, 50}, new int[]{40, 14, 56, 56}, 4);                    // right wing
        g.setColor(new Color(200, 100, 30));  g.fillOval(34, 28, 32, 46);                           // body
        g.setColor(new Color(250, 210, 80));  g.fillOval(36, 8, 28, 26);                            // head
        g.setColor(new Color(255, 120, 30));  g.fillPolygon(new int[]{44, 56, 50}, new int[]{26, 26, 42}, 3);  // beak
        g.setColor(Color.BLACK);              g.fillOval(41, 17, 5, 5); g.fillOval(54, 17, 5, 5);
        return g;
    }

    /** Macan Bayangan: a dark purple shadow tiger with glowing eyes. */
    public static GreenfootImage macan() {
        GreenfootImage g = new GreenfootImage(100, 64);
        g.setColor(new Color(60, 30, 90));    g.fillOval(6, 18, 70, 36);                            // body
        g.fillRect(14, 44, 8, 18); g.fillRect(56, 44, 8, 18);                                       // legs
        g.setColor(new Color(80, 45, 115));   g.fillOval(62, 8, 34, 32);                            // head
        g.fillPolygon(new int[]{64, 70, 74}, new int[]{14, 0, 14}, 3);                              // ears
        g.fillPolygon(new int[]{84, 90, 94}, new int[]{14, 0, 14}, 3);
        g.setColor(new Color(255, 80, 200));  g.fillOval(70, 18, 7, 7); g.fillOval(84, 18, 7, 7);   // glowing eyes
        g.setColor(new Color(20, 10, 35));                                                          // stripes
        for (int x = 20; x < 66; x += 12) { g.drawLine(x, 20, x + 4, 40); }
        return g;
    }
}
