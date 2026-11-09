import greenfoot.*;

/**
 * Level 1: Candi Tirta Amerta (the water temple).
 * Layout (left -> right):
 *   [start] | wooden barricade (Raden) | pool with a target behind it (Dyah) | locked gate | [glyph + portal]
 *
 * This class only describes the layout; the parent class does all the work.
 * 'extends ExplorationWorld' = INHERITANCE: we get everything it has for free.
 */
public class TirtaAmertaWorld extends ExplorationWorld {

    public TirtaAmertaWorld(int nextStage) {
        super(Element.AIR, nextStage);          // call the parent's constructor
    }

    @Override
    protected String getAreaTitle() {
        return "Candi Tirta Amerta";
    }

    @Override
    protected void buildLevel() {
        // Wall at x=240 with a gap from y=230 to y=370, plugged by a wooden barricade.
        addWallWithGap(240, 230, 370);
        addBarricade(240, 300);

        // A pool in the next room. The target sits behind the water where nobody can walk.
        addWater(380, 182, 250, 66);
        Gate gate = addGate(520, 300);          // locked gate in the second wall
        addWallWithGap(520, 230, 370);
        addTarget(380, 135, gate);              // hitting this target opens 'gate'

        addGlyph(Element.AIR, 640, 300);
        addPortal(Element.AIR, 745, 300);

        addSign("Kayu menghalangi? Raden bisa menebas (SPACE)", 150, 440);
        addSign("Sasaran di seberang air? Dyah memanah (Q lalu SPACE)", 400, 440);
    }
}
