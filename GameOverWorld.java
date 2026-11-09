import greenfoot.*;

/** Shown when Raden's health reaches zero. ENTER retries the battle. */
public class GameOverWorld extends MenuWorld {

    private int retryStage;

    public GameOverWorld(int retryStage) {
        this.retryStage = retryStage;
        GreenfootImage bg = new GreenfootImage(GameState.WIDTH, GameState.HEIGHT);
        bg.setColor(new Color(40, 0, 0));
        bg.fillRect(0, 0, GameState.WIDTH, GameState.HEIGHT);
        setBackground(bg);
        addText("PAHLAWAN GUGUR", 56, new Color(230, 60, 60), 190);
        addText("Nusantara masih membutuhkan Raden dan Dyah...", 22, Color.WHITE, 260);
        addText("Tekan ENTER untuk mencoba lagi", 26, Color.YELLOW, 340);
    }

    protected void onConfirm() {
        Greenfoot.setWorld(GameState.buildStage(retryStage));
    }
}
