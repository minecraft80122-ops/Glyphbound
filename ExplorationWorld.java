import greenfoot.*;

/**
 * ExplorationWorld - ABSTRACT parent of the three exploration levels.
 *
 * It contains everything the levels have in common (background, HUD, helper
 * methods to place walls/water/crates, the glyph flag). Each child level only
 * has to say WHAT to place and WHERE by implementing buildLevel().
 *
 * This is the "don't repeat yourself" (DRY) principle in action: instead of
 * copy-pasting 100 lines into 3 files, we write them once here.
 */
public abstract class ExplorationWorld extends World {

    private Element area;                // which temple are we in?
    private int nextStage;               // stage to load when the portal is entered
    private boolean glyphCollected = false;
    private Label hudCharacter;          // top-left text showing the active hero

    public ExplorationWorld(Element area, int nextStage) {
        super(GameState.WIDTH, GameState.HEIGHT, 1);
        this.area = area;
        this.nextStage = nextStage;

        // Background picture + a dark strip at the top so the HUD text is readable.
        setBackground(Scenery.create(area, GameState.WIDTH, GameState.HEIGHT));
        getBackground().setColor(new Color(0, 0, 0, 150));
        getBackground().fillRect(0, 0, GameState.WIDTH, 36);

        // Actors listed first are drawn ON TOP of those listed later.
        setPaintOrder(FloatingText.class, Label.class, SlashEffect.class, Arrow.class,
                Explorer.class, Glyph.class, Portal.class, Obstacle.class);

        addObject(new Explorer(), 60, 300);              // the hero starts at the left

        // NOTE: calling an abstract method from a constructor works (the child's
        // version runs), but the child's own fields aren't ready yet. So the
        // children below deliberately don't use any fields of their own.
        buildLevel();

        addObject(new Label(getAreaTitle(), 22, Color.WHITE), GameState.WIDTH / 2, 18);
        hudCharacter = new Label("Aktif: RADEN", 18, new Color(255, 120, 120));
        addObject(hudCharacter, 90, 18);
        addObject(new Label("Panah/WASD  SPACE aksi  Q ganti  R ulang", 13, Color.WHITE), 655, 18);
    }

    /** Runs every frame. Press R to restart the level if a crate gets stuck. */
    public void act() {
        if (Greenfoot.isKeyDown("r")) {
            // The exploration stage number is always exactly one less than its battle stage.
            Greenfoot.setWorld(GameState.buildStage(nextStage - 1));
        }
    }

    /** Each level fills the world with its own puzzle layout. */
    protected abstract void buildLevel();

    /** Each level gives its own title. */
    protected abstract String getAreaTitle();

    // ---------------------------------------------------------------- HUD & state
    public void setHudCharacter(boolean isRaden) {
        if (isRaden) {
            hudCharacter.setText("Aktif: RADEN");
        } else {
            hudCharacter.setText("Aktif: DYAH");
        }
    }

    /** Called by Glyph when the hero touches it. */
    public void glyphCollected(Element e) {
        glyphCollected = true;
        GameState.addGlyph(e);
        addObject(new FloatingText(e.getDisplayName() + " didapat! Masuki gerbang cahaya", 22, e.getColor()),
                GameState.WIDTH / 2, 200);
        addObject(new GlyphBlast(e.getColor()), GameState.WIDTH / 2, 300);
    }

    public boolean isGlyphCollected() {
        return glyphCollected;
    }

    public int getNextStage() {
        return nextStage;
    }

    protected Element getArea() {
        return area;
    }

    // ---------------------------------------------------------------- level-building helpers
    // 'protected' = visible to this class and its children, hidden from everyone else.

    protected void addWall(int cx, int cy, int w, int h) {
        addObject(new Wall(w, h, Scenery.wallColor(area)), cx, cy);
    }

    /**
     * A vertical wall at column x spanning the whole floor, except for a gap
     * between y=gapTop and y=gapBottom (we build it from a top and a bottom block).
     */
    protected void addWallWithGap(int x, int gapTop, int gapBottom) {
        int topH = gapTop - Scenery.HORIZON;
        addWall(x, Scenery.HORIZON + topH / 2, 30, topH);
        int bottomH = getHeight() - gapBottom;
        addWall(x, gapBottom + bottomH / 2, 30, bottomH);
    }

    protected void addWater(int cx, int cy, int w, int h) {
        addObject(new Water(w, h), cx, cy);
    }

    protected void addCrate(int x, int y) {
        addObject(new Crate(), x, y);
    }

    protected void addBarricade(int x, int y) {
        addObject(new Barricade(30, 140), x, y);          // exactly fills a 140px gap
    }

    /** Creates a gate AND returns it, so a Target can be linked to it. */
    protected Gate addGate(int x, int y) {
        Gate gate = new Gate(30, 140, Scenery.wallColor(area));
        addObject(gate, x, y);
        return gate;
    }

    protected void addTarget(int x, int y, Gate gate) {
        addObject(new Target(gate), x, y);
    }

    protected void addGlyph(Element e, int x, int y) {
        addObject(new Glyph(e), x, y);
    }

    protected void addPortal(Element e, int x, int y) {
        addObject(new Portal(e), x, y);
    }

    /** A small hint text on the floor (acts like a signpost). */
    protected void addSign(String text, int x, int y) {
        addObject(new Label(text, 15, Color.WHITE), x, y);
    }
}
