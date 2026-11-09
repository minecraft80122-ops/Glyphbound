/** Pak Sutar, a fisherman with a bit of lore. */
public class FisherNpc extends Npc {

    public FisherNpc() {
        super("Pak Sutar", "npc/Fisher.png");
    }

    @Override
    protected String[] getLines() {
        if (GameState.glyphCount() >= 3) {
            return new String[]{"Lihat! Segel di dermaga mulai bergetar. Gerbang pelabuhan akan terbuka untuk kalian!"};
        }
        return new String[]{
            "Kapal-kapal Portugis itu tidak pernah pergi dari dermaga.",
            "Gerbang pelabuhan tertutup rapat. Kata orang, hanya tiga Glif yang bisa membukanya.",
            "Kembalilah dengan ketiganya, Raden."
        };
    }
}
