import greenfoot.*;

/**
 * Level 3: Gua Watu Lengkara (the shadow cave).
 * Hardest layout: crate, barricade, then TWO pools with two targets and two gates.
 */
public class WatuLengkaraWorld extends ExplorationWorld {

    public WatuLengkaraWorld(int nextStage) {
        super(Element.BAYANGAN, nextStage);
    }

    @Override
    protected String getAreaTitle() {
        return "Gua Watu Lengkara";
    }

    @Override
    protected void buildLevel() {
        addWallWithGap(180, 265, 335);          // crate doorway
        addCrate(180, 300);

        addWallWithGap(320, 230, 370);          // barricade doorway
        addBarricade(320, 300);

        // Room C: first pool, target and gate.
        addWater(395, 182, 120, 66);
        Gate gate1 = addGate(470, 300);
        addWallWithGap(470, 230, 370);
        addTarget(395, 135, gate1);

        // Room D: second pool, target and gate.
        addWater(545, 182, 120, 66);
        Gate gate2 = addGate(620, 300);
        addWallWithGap(620, 230, 370);
        addTarget(545, 135, gate2);

        addGlyph(Element.BAYANGAN, 700, 300);
        addPortal(Element.BAYANGAN, 760, 300);

        addSign("Dua sasaran, dua gerbang!", 400, 440);
    }
}
