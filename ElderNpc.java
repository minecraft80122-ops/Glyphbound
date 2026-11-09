/** Ki Ageng, the village elder. His words change with how many glyphs you own. */
public class ElderNpc extends Npc {

    public ElderNpc() {
        super("Ki Ageng", "npc/Elder.png");
    }

    @Override
    protected String[] getLines() {
        int glyphs = GameState.glyphCount();            // static method: no object needed
        if (glyphs == 0) {
            return new String[]{
                "Raden, Dyah... syukurlah kalian datang.",
                "Portugis menguasai Pelabuhan Selatan, dan gerbangnya disegel sihir.",
                "Hanya tiga Glif Elemen yang mampu mematahkan segel itu.",
                "Candi Utara menyimpan Glif Air, Candi Timur menyimpan Glif Surya, dan Gua Barat menyimpan Glif Bayangan.",
                "Tekan Q untuk bertukar antara Raden dan Dyah. Berangkatlah!"
            };
        } else if (glyphs < 3) {
            return new String[]{
                "Kalian sudah membawa " + glyphs + " dari 3 Glif. Bagus sekali!",
                "Jangan lengah. Candi yang tersisa lebih berbahaya."
            };
        } else {
            return new String[]{
                "Tiga Glif sudah bersatu! Demak berutang budi pada kalian.",
                "Pergilah ke Pelabuhan Selatan dan patahkan segelnya!"
            };
        }
    }
}
