import greenfoot.*;

/** The title screen, drawn over a darkened picture of the world map. */
public class TitleWorld extends MenuWorld {

    public TitleWorld() {
        GameState.reset();

        // Use the big map image as the title background: scale it, then darken it.
        GreenfootImage map = new GreenfootImage("Designer.png");
        map.scale(GameState.WIDTH, GameState.WIDTH * 2 / 3);        // keep the 3:2 shape
        GreenfootImage bg = new GreenfootImage(GameState.WIDTH, GameState.HEIGHT);
        bg.drawImage(map, 0, -20);
        bg.setColor(new Color(0, 0, 0, 140));
        bg.fillRect(0, 0, GameState.WIDTH, GameState.HEIGHT);
        setBackground(bg);

        addText("LEGENDA DEMAK", 64, new Color(255, 220, 120), 100);
        addText("Raden Kalifatullah & Dyah Sekar", 28, Color.WHITE, 158);
        addText("Usir penjajah dengan kekuatan tiga Glif Elemen", 20, Color.WHITE, 196);

        addText("PETA & CANDI:  Panah/WASD jalan  |  E bicara/masuk  |  SPACE aksi  |  Q ganti Raden/Dyah", 16, Color.WHITE, 280);
        addText("Raden: tebas kayu & dorong peti      Dyah: memanah sasaran jauh      R: ulang level candi", 16, Color.WHITE, 306);
        addText("TARUNG (KARTU):  klik kartu, klik musuh  |  End Turn  |  tombol Glif saat penuh", 16, Color.WHITE, 342);
        addText("Kartu Raden menandai musuh -> kartu Dyah pada musuh bertanda = KOMBO x2!", 16, Color.WHITE, 368);

        addText("Tekan ENTER untuk mulai", 30, Color.YELLOW, 440);
    }

    protected void onConfirm() {
        Greenfoot.setWorld(GameState.buildStage(GameState.STAGE_INTRO));
    }
}
