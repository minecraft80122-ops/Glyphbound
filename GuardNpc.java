/** Prajurit Jaka, a guard with battle tips. */
public class GuardNpc extends Npc {

    public GuardNpc() {
        super("Prajurit Jaka", "npc/Guard.png");
    }

    @Override
    protected String[] getLines() {
        return new String[]{
            "Pertempuran memakai kartu! Tiap giliran kamu dapat 3 energi dan 5 kartu.",
            "Kartu Raden menandai musuh dengan lingkaran kuning. Kartu Dyah pada musuh bertanda jadi KOMBO x2!",
            "Setiap kartu mengisi bar Glif. Bila penuh, klik tombol Glif dan selesaikan teka-tekinya.",
            "Bos punya perisai biru. Hanya kombo dan kekuatan Glif yang menghancurkannya dengan cepat."
        };
    }
}
