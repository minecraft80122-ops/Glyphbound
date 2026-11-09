import greenfoot.*;

/**
 * Target - a bullseye out of reach across the water. When an Arrow hits it,
 * it opens the Gate it is linked to.
 *
 * Note the field 'linkedGate': one object HOLDING A REFERENCE to another object.
 * That is how objects cooperate in OOP ("has-a" relationship, called composition).
 */
public class Target extends Obstacle {

    private Gate linkedGate;
    private boolean activated = false;

    public Target(Gate gate) {
        this.linkedGate = gate;
        draw(false);
    }

    @Override
    public void onArrowHit() {                // replaces the empty parent version
        if (activated) {
            return;
        }
        activated = true;
        draw(true);                           // turn green
        linkedGate.open();                    // ask the gate to open itself
    }

    private void draw(boolean on) {
        GreenfootImage g = new GreenfootImage(44, 44);
        g.setColor(on ? new Color(60, 190, 80) : new Color(200, 50, 50));
        g.fillOval(0, 0, 43, 43);
        g.setColor(Color.WHITE);
        g.fillOval(8, 8, 27, 27);
        g.setColor(on ? new Color(30, 140, 50) : new Color(200, 50, 50));
        g.fillOval(15, 15, 13, 13);
        setImage(g);
    }
}
