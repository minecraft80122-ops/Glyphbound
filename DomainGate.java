import greenfoot.*;

/**
 * DomainGate - a glowing doorway on the map. Walk close and press E to enter a domain
 * (temple level). Also used for the Southern Port, which needs all 3 glyphs.
 *
 * Another class that implements Interactable, completely different from Npc.
 */
public class DomainGate extends MapActor implements Interactable {

    private String name;
    private Element element;            // which glyph lives here (null for the port)
    private int stageId;                // stage to load when entering
    private int requiredGlyphs;         // how many glyphs are needed to open it
    private int tick = 0;

    public DomainGate(String name, Element element, int stageId, int requiredGlyphs) {
        this.name = name;
        this.element = element;
        this.stageId = stageId;
        this.requiredGlyphs = requiredGlyphs;
        drawImage();
    }

    private void drawImage() {
        Color c = (element == null) ? new Color(230, 90, 70) : element.getColor();
        GreenfootImage g = new GreenfootImage(220, 100);

        // Glowing ellipse "portal" on the ground (feet position = bottom centre of the image).
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 70));
        g.fillOval(75, 28, 70, 70);
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 190));
        g.fillOval(88, 42, 44, 52);
        g.setColor(new Color(255, 255, 255, 220));
        g.fillOval(100, 56, 20, 30);

        // Name plate above it.
        GreenfootImage plate = new GreenfootImage(name, 15, Color.WHITE, new Color(0, 0, 0, 150));
        g.drawImage(plate, (220 - plate.getWidth()) / 2, 4);
        setImage(g);
    }

    public void act() {
        tick++;
        getImage().setTransparency(190 + (int) (60 * Math.sin(tick * 0.1)));    // gentle pulsing
    }

    public String getPrompt() {
        if (requiredGlyphs > GameState.glyphCount()) {
            return "E: " + name + " (terkunci)";
        }
        if (element != null && GameState.hasGlyph(element)) {
            return name + " (selesai)";
        }
        return "E: masuk " + name;
    }

    public void interact(MyWorld world) {
        if (requiredGlyphs > GameState.glyphCount()) {
            world.openDialogue("Segel Sihir", new String[]{
                "Gerbang ini disegel kuat. Kumpulkan Glif lebih dulu (" + GameState.glyphCount() + "/" + requiredGlyphs + ")."
            });
            return;
        }
        if (element != null && GameState.hasGlyph(element)) {
            world.openDialogue(name, new String[]{"Glif dari tempat ini sudah kamu ambil."});
            return;
        }
        // Remember where to put the hero when they come back (a bit below the gate).
        GameState.setReturnPoint(mapX, mapY + 40);
        Greenfoot.setWorld(GameState.buildStage(stageId));
    }
}
