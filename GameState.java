import greenfoot.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GameState - data shared by the WHOLE game (all static, so no "new GameState()").
 *
 * Game flow:
 *   Title -> Prolog story -> OVERWORLD (MyWorld) --enter a domain gate-->
 *        temple puzzle level -> boss battle -> short story page -> back to OVERWORLD
 *   After all 3 glyphs are collected the Southern Port gate opens.
 *
 * buildStage() is a FACTORY METHOD: give it a stage number, get the right World object.
 */
public class GameState {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 500;

    // ---- stage numbers ----
    public static final int STAGE_TITLE = 0;
    public static final int STAGE_INTRO = 1;
    public static final int STAGE_TIRTA_EXPLORE = 2;    // Northern Domain
    public static final int STAGE_TIRTA_BATTLE = 3;
    public static final int STAGE_STORY_1 = 4;
    public static final int STAGE_SURYA_EXPLORE = 5;    // Eastern Domain
    public static final int STAGE_SURYA_BATTLE = 6;
    public static final int STAGE_STORY_2 = 7;
    public static final int STAGE_WATU_EXPLORE = 8;     // Western Domain
    public static final int STAGE_WATU_BATTLE = 9;
    public static final int STAGE_ENDING = 10;
    public static final int STAGE_OVERWORLD = 11;
    public static final int STAGE_STORY_3 = 12;

    // ---- card battle data: the party's health and the deck (survive between fights) ----
    public static int maxHp = 60;
    public static int hp = 60;
    public static ArrayList<Card> deck = new ArrayList<Card>();

    /** Start a new game: full health and the starting deck. */
    public static void resetCards() {
        maxHp = 60;
        hp = 60;
        deck.clear();
        for (int i = 0; i < 3; i++) deck.add(Card.tebas());
        for (int i = 0; i < 3; i++) deck.add(Card.tangkis());
        for (int i = 0; i < 3; i++) deck.add(Card.panah());
        deck.add(Card.hantamBerat());
        deck.add(Card.hujanPanah());
        deck.add(Card.jamu());
    }

    /** Lets a BattleWorld be started directly (right-click > new BattleWorld(...)) for testing. */
    public static void ensureDeck() {
        if (deck.isEmpty()) resetCards();
    }

    // ---- collected glyphs ----
    private static List<Element> glyphs = new ArrayList<Element>();

    // ---- where the hero reappears on the overworld map (in MAP pixels) ----
    private static double returnX = 771;
    private static double returnY = 440;

    public static void reset() {
        resetCards();
        glyphs.clear();
        returnX = 771;
        returnY = 440;
    }

    public static void addGlyph(Element e) {
        if (!glyphs.contains(e)) glyphs.add(e);
    }

    public static boolean hasGlyph(Element e) {
        return glyphs.contains(e);
    }

    public static int glyphCount() {
        return glyphs.size();
    }

    public static void setReturnPoint(double x, double y) {
        returnX = x;
        returnY = y;
    }

    public static double getReturnX() { return returnX; }

    public static double getReturnY() { return returnY; }

    public static World buildStage(int stage) {
        switch (stage) {
            case STAGE_INTRO:
                return new StoryWorld("PROLOG", new String[]{
                    "Sekitar tahun 1512, Portugis merebut Malaka",
                    "dan mulai mengincar seluruh Nusantara.",
                    "",
                    "Kini kapal-kapal mereka menguasai Pelabuhan Selatan Demak,",
                    "dan gerbangnya disegel dengan sihir.",
                    "",
                    "Raden Kalifatullah dan adiknya Dyah Sekar diutus",
                    "mencari tiga Glif Elemen di candi-candi kuno",
                    "untuk mematahkan segel itu."
                }, STAGE_OVERWORLD);

            case STAGE_OVERWORLD:
                return new MyWorld();

            case STAGE_TIRTA_EXPLORE:
                return new TirtaAmertaWorld(STAGE_TIRTA_BATTLE);
            case STAGE_TIRTA_BATTLE:
                return new BattleWorld(STAGE_TIRTA_BATTLE, Element.AIR, STAGE_STORY_1);
            case STAGE_STORY_1:
                return new StoryWorld("GLIF AIR DIDAPAT", new String[]{
                    "Sang Naga Tirta telah ditaklukkan,",
                    "dan Glif Air bersinar di tangan Raden.",
                    "",
                    "Kembalilah ke Pusat Kota dan carilah",
                    "candi lain yang menyimpan Glif berikutnya."
                }, STAGE_OVERWORLD);

            case STAGE_SURYA_EXPLORE:
                return new SuryaKenconoWorld(STAGE_SURYA_BATTLE);
            case STAGE_SURYA_BATTLE:
                return new BattleWorld(STAGE_SURYA_BATTLE, Element.SURYA, STAGE_STORY_2);
            case STAGE_STORY_2:
                return new StoryWorld("GLIF SURYA DIDAPAT", new String[]{
                    "Garuda Surya jatuh, dan Menara Surya Kencana kembali terang.",
                    "",
                    "Cahaya Glif menghangatkan tangan Dyah.",
                    "Kembalilah ke peta dan teruskan perjalanan."
                }, STAGE_OVERWORLD);

            case STAGE_WATU_EXPLORE:
                return new WatuLengkaraWorld(STAGE_WATU_BATTLE);
            case STAGE_WATU_BATTLE:
                return new BattleWorld(STAGE_WATU_BATTLE, Element.BAYANGAN, STAGE_STORY_3);
            case STAGE_STORY_3:
                return new StoryWorld("GLIF BAYANGAN DIDAPAT", new String[]{
                    "Macan Bayangan lenyap menjadi kabut,",
                    "meninggalkan Glif yang berdenyut gelap.",
                    "",
                    "Tiga Glif sudah lengkap?",
                    "Segel di Pelabuhan Selatan menunggu."
                }, STAGE_OVERWORLD);

            case STAGE_ENDING:
                // TODO (your next big feature): add a FINAL BOSS BATTLE here, before the ending.
                return new StoryWorld("KEMENANGAN", new String[]{
                    "Tiga Glif bersatu: Air, Surya, dan Bayangan.",
                    "Segel Pelabuhan Selatan pecah,",
                    "dan kapal-kapal Portugis mundur ke lautan.",
                    "",
                    "Raden Kalifatullah dan Dyah Sekar dikenang",
                    "sebagai pelindung Nusantara.",
                    "",
                    "TAMAT (demo) - terima kasih sudah bermain!"
                }, STAGE_TITLE);

            default:
                return new TitleWorld();
        }
    }
}
