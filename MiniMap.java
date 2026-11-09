import greenfoot.*;
import java.util.List;

/** MiniMap - a small overview of the whole map in the corner, with a dot for the player. */
public class MiniMap extends Actor {

    private static final int W = 176, H = 117;
    private GreenfootImage base;            // the shrunken map with a border (made once)
    private int tick = 0;

    public MiniMap() {
        base = new GreenfootImage("Designer.png");
        base.scale(W, H);
        base.setColor(new Color(220, 180, 80));
        base.drawRect(0, 0, W - 1, H - 1);
        base.drawRect(1, 1, W - 3, H - 3);
        setImage(new GreenfootImage(base));
    }

    public void act() {
        tick++;
        if (tick % 4 != 0) return;                         // redraw only every 4th frame (saves time)

        MyWorld world = (MyWorld) getWorld();
        GreenfootImage img = new GreenfootImage(base);     // fresh copy, then draw dots on it
        double scale = (double) W / MyWorld.MAP_W;

        List<DomainGate> gates = world.getObjects(DomainGate.class);
        for (DomainGate gate : gates) {
            img.setColor(Color.YELLOW);
            img.fillOval((int) (gate.getMapX() * scale) - 3, (int) (gate.getMapY() * scale) - 3, 6, 6);
        }
        Player p = world.getPlayer();
        int px = (int) (p.getMapX() * scale);
        int py = (int) (p.getMapY() * scale);
        img.setColor(Color.BLACK);
        img.fillOval(px - 4, py - 4, 8, 8);
        img.setColor(Color.WHITE);
        img.fillOval(px - 3, py - 3, 6, 6);
        setImage(img);
    }
}
