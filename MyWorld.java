import greenfoot.*;
import java.util.List;

/**
 * MyWorld - the OVERWORLD: the big map of Demak that you walk around.
 * (Your original MyWorld, rebuilt around a camera.)
 *
 * How it works:
 *  - The map picture (Designer.png, 1536x1024) is much bigger than the 800x500 window.
 *  - We keep a camera position (camX, camY) and every frame draw the part of the map
 *    the camera sees onto the world background.
 *  - Characters/gates are MapActors that know their position on the MAP; we place them
 *    on screen relative to the camera.
 *  - Walking is limited by walkmask.png: a tiny black/white picture where ONE pixel
 *    stands for an 8x8 pixel square of the map. White = walkable, black = blocked.
 *    You can edit it in any paint program to fix places where the hero gets stuck!
 */
public class MyWorld extends World {

    public static final int MAP_W = 1536;
    public static final int MAP_H = 1024;
    private static final int CELL = 8;                  // map pixels per mask pixel

    private GreenfootImage mapImage;
    private GreenfootImage walkMask;
    private double camX, camY;
    private int lastDrawnX = -9999, lastDrawnY = -9999;

    private Player player;
    private DialogueBox dialogue = null;               // null = nobody is talking
    private Label objectiveLabel, promptLabel;

    public MyWorld() {
        // 'false' = NOT bounded: actors may be placed outside the window (they are just off-camera).
        super(GameState.WIDTH, GameState.HEIGHT, 1, false);

        mapImage = new GreenfootImage("Designer.png");
        walkMask = new GreenfootImage("walkmask.png");

        // First in the list = drawn on top.
        setPaintOrder(DialogueBox.class, Label.class, GlyphHud.class, MiniMap.class,
                FloatingText.class, Vignette.class, Player.class, Npc.class, DomainGate.class);

        buildWorld();
        buildHud();

        // Start with the camera already centred on the hero (no sliding at the start).
        camX = clampCamX(player.getMapX() - GameState.WIDTH / 2.0);
        camY = clampCamY(player.getMapY() - GameState.HEIGHT / 2.0);
        drawMap();
        syncActors();
    }

    // ------------------------------------------------------------------ building the level
    private void buildWorld() {
        // ---- the four gates (positions are in MAP pixels) ----
        addMapActor(new DomainGate("Candi Tirta Amerta", Element.AIR, GameState.STAGE_TIRTA_EXPLORE, 0), 767, 147);
        addMapActor(new DomainGate("Menara Surya Kencana", Element.SURYA, GameState.STAGE_SURYA_EXPLORE, 0), 1331, 227);
        addMapActor(new DomainGate("Gua Watu Lengkara", Element.BAYANGAN, GameState.STAGE_WATU_EXPLORE, 0), 207, 233);
        addMapActor(new DomainGate("Pelabuhan Selatan", null, GameState.STAGE_ENDING, 3), 768, 813);

        // ---- villagers ----
        addMapActor(new ElderNpc(), 813, 467);
        addMapActor(new MerchantNpc(), 704, 467);
        addMapActor(new GuardNpc(), 333, 507);
        addMapActor(new FisherNpc(), 640, 747);

        // ---- the hero (appears where we left the map, or at the town centre at the start) ----
        player = new Player();
        addMapActor(player, GameState.getReturnX(), GameState.getReturnY());

        // ---- screen effects ----
        addObject(new Vignette(GameState.WIDTH, GameState.HEIGHT), GameState.WIDTH / 2, GameState.HEIGHT / 2);
    }

    private void buildHud() {
        addObject(new GlyphHud(), 110, 30);
        addObject(new MiniMap(), GameState.WIDTH - 98, 68);
        objectiveLabel = new Label(" ", 17, Color.WHITE, new Color(0, 0, 0, 150));
        addObject(objectiveLabel, GameState.WIDTH / 2, GameState.HEIGHT - 16);
        promptLabel = new Label(" ", 20, new Color(255, 230, 120), new Color(0, 0, 0, 170));
        addObject(promptLabel, GameState.WIDTH / 2, GameState.HEIGHT - 48);
    }

    private void addMapActor(MapActor actor, double mapX, double mapY) {
        actor.setMapPosition(mapX, mapY);
        addObject(actor, 0, 0);                        // real screen position is set by syncActors()
    }

    // ------------------------------------------------------------------ every frame
    public void act() {
        updateCamera();
        drawMap();
        syncActors();
        updateHud();
    }

    private void updateCamera() {
        double targetX = clampCamX(player.getMapX() - GameState.WIDTH / 2.0);
        double targetY = clampCamY(player.getMapY() - GameState.HEIGHT / 2.0);
        camX += (targetX - camX) * 0.15;               // move 15% of the way each frame = smooth follow
        camY += (targetY - camY) * 0.15;
    }

    private double clampCamX(double x) {
        return Math.max(0, Math.min(MAP_W - GameState.WIDTH, x));
    }

    private double clampCamY(double y) {
        return Math.max(0, Math.min(MAP_H - GameState.HEIGHT, y));
    }

    /** Paint the visible part of the map onto the world background. */
    private void drawMap() {
        int cx = (int) Math.round(camX);
        int cy = (int) Math.round(camY);
        if (cx == lastDrawnX && cy == lastDrawnY) return;      // camera didn't move: nothing to redraw
        getBackground().drawImage(mapImage, -cx, -cy);
        lastDrawnX = cx;
        lastDrawnY = cy;
    }

    /** Put every MapActor at (mapX - camX, mapY - camY) on the screen. */
    private void syncActors() {
        int cx = (int) Math.round(camX);
        int cy = (int) Math.round(camY);
        List<MapActor> actors = getObjects(MapActor.class);
        for (MapActor a : actors) {
            a.syncToCamera(cx, cy);
        }
    }

    private void updateHud() {
        // Objective text depends on progress.
        int glyphs = GameState.glyphCount();
        if (glyphs < 3) {
            objectiveLabel.setText(" Tujuan: ambil Glif dari tiga candi (" + glyphs + "/3) ");
        } else {
            objectiveLabel.setText(" Tujuan: menuju Pelabuhan Selatan! ");
        }

        // Prompt for the closest thing you can interact with.
        Interactable near = isDialogueOpen() ? null : findInteractable(player.getMapX(), player.getMapY());
        promptLabel.setText(near == null ? " " : " " + near.getPrompt() + " ");
    }

    // ------------------------------------------------------------------ collision
    /** Is the map point (x, y) walkable according to walkmask.png? */
    public boolean isWalkable(double x, double y) {
        int mx = (int) (x / CELL);
        int my = (int) (y / CELL);
        if (mx < 0 || my < 0 || mx >= walkMask.getWidth() || my >= walkMask.getHeight()) {
            return false;                              // outside the map
        }
        return walkMask.getColorAt(mx, my).getRed() > 128;      // white-ish = walkable
    }

    /** Villagers are solid: the player can't walk through them. */
    public boolean isBlockedByNpc(double x, double y) {
        List<Npc> npcs = getObjects(Npc.class);
        for (Npc n : npcs) {
            if (n.distanceTo(x, y) < 16) return true;
        }
        return false;
    }

    // ------------------------------------------------------------------ interaction & dialogue
    /** Find the closest Interactable within 60 map pixels of (x, y), or null if none. */
    public Interactable findInteractable(double x, double y) {
        Interactable best = null;
        double bestDist = 60;
        List<MapActor> actors = getObjects(MapActor.class);
        for (MapActor a : actors) {
            if (a instanceof Interactable) {                    // is this actor an Interactable?
                double d = a.distanceTo(x, y);
                if (d < bestDist) {
                    bestDist = d;
                    best = (Interactable) a;                    // cast: treat it as an Interactable
                }
            }
        }
        return best;
    }

    public void openDialogue(String speaker, String[] lines) {
        if (dialogue != null) return;
        dialogue = new DialogueBox(speaker, lines);
        addObject(dialogue, GameState.WIDTH / 2, GameState.HEIGHT - 80);
    }

    public void closeDialogue() {
        if (dialogue != null) {
            removeObject(dialogue);
            dialogue = null;
        }
    }

    public boolean isDialogueOpen() {
        return dialogue != null;
    }

    public Player getPlayer() {
        return player;
    }
}
