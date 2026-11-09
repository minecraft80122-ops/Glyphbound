import greenfoot.*;

/**
 * Level 2: Menara Surya Kencana (the sun tower).
 * New twist: a heavy crate plugs the first doorway and must be pushed out of the way by Raden.
 */
public class SuryaKenconoWorld extends ExplorationWorld {

    public SuryaKenconoWorld(int nextStage) {
        super(Element.SURYA, nextStage);
    }

    @Override
    protected String getAreaTitle() {
        return "Menara Surya Kencana";
    }

    @Override
    protected void buildLevel() {
        // Narrow doorway (gap 265..335, only 70px tall) blocked by a 50px crate.
        addWallWithGap(200, 265, 335);
        addCrate(200, 300);

        // Second doorway with a wooden barricade.
        addWallWithGap(400, 230, 370);
        addBarricade(400, 300);

        // Third room: pool + target + gate.
        addWater(500, 182, 170, 66);
        Gate gate = addGate(600, 300);
        addWallWithGap(600, 230, 370);
        addTarget(500, 135, gate);

        addGlyph(Element.SURYA, 695, 300);
        addPortal(Element.SURYA, 755, 300);

        addSign("Peti berat? Hanya Raden yang bisa mendorong", 130, 440);
        addSign("Dorong peti ke atas/bawah agar jalan terbuka", 470, 440);
    }
}
