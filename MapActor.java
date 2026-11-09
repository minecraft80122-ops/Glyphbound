import greenfoot.*;

/**
 * MapActor - the parent of everything that lives ON THE BIG MAP (Player, Npc, DomainGate).
 *
 * Your original game moved EVERY actor whenever the player walked (scrollWorld).
 * This version uses a CAMERA instead: each MapActor remembers its real position on the
 * map (mapX, mapY) and the world simply places it on screen at (mapX - cameraX, mapY - cameraY).
 * Benefits: positions never drift, collisions are easy (we know real map positions),
 * and the player can reach the screen edges when the camera stops at the map border.
 *
 * mapX/mapY is the position of the FEET, not the image centre. That makes it
 * natural to ask "is the ground under my feet walkable?".
 */
public abstract class MapActor extends Actor {

    protected double mapX;
    protected double mapY;

    public void setMapPosition(double x, double y) {
        mapX = x;
        mapY = y;
    }

    public double getMapX() {
        return mapX;
    }

    public double getMapY() {
        return mapY;
    }

    /** Distance between this actor's feet and a point on the map. */
    public double distanceTo(double x, double y) {
        double dx = x - mapX;
        double dy = y - mapY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** Called by MyWorld every frame: place the picture on screen according to the camera. */
    public void syncToCamera(int camX, int camY) {
        GreenfootImage img = getImage();
        int halfHeight = (img == null) ? 0 : img.getHeight() / 2;     // lift the image so the FEET sit at mapY
        setLocation((int) Math.round(mapX - camX), (int) Math.round(mapY - camY) - halfHeight);
    }
}
