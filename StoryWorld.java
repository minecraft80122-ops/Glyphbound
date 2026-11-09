import greenfoot.*;

/**
 * StoryWorld - shows a page of story text, then continues to the next stage.
 * Reusable: the text and the next stage number are given in the constructor.
 */
public class StoryWorld extends MenuWorld {

    private int nextStage;

    public StoryWorld(String title, String[] lines, int nextStage) {
        this.nextStage = nextStage;

        // Dark parchment-like background with a golden border.
        GreenfootImage bg = new GreenfootImage(GameState.WIDTH, GameState.HEIGHT);
        bg.setColor(new Color(35, 24, 18));
        bg.fillRect(0, 0, GameState.WIDTH, GameState.HEIGHT);
        bg.setColor(new Color(200, 160, 70));
        bg.drawRect(14, 14, GameState.WIDTH - 29, GameState.HEIGHT - 29);
        bg.drawRect(18, 18, GameState.WIDTH - 37, GameState.HEIGHT - 37);
        setBackground(bg);

        addText(title, 40, new Color(255, 220, 120), 70);

        // A 'for' loop walks through the array: i = 0, 1, 2, ... lines.length-1
        for (int i = 0; i < lines.length; i++) {
            addText(lines[i], 22, Color.WHITE, 130 + i * 32);
        }

        addText("Tekan ENTER untuk lanjut", 20, Color.YELLOW, 460);
    }

    protected void onConfirm() {
        Greenfoot.setWorld(GameState.buildStage(nextStage));
    }
}
