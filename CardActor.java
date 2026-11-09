import greenfoot.*;

/**
 * CardActor - the on-screen picture of a Card. Drawn entirely in code,
 * so adding new cards needs no new image files.
 * Colours tell you the owner: red = Raden attack, blue = Raden skill,
 * green = Dyah attack, teal = Dyah skill.
 */
public class CardActor extends Actor {

    public static final int W = 100;
    public static final int H = 140;

    private Card card;
    private boolean selected = false;
    private boolean playable = true;

    public CardActor(Card card) {
        this.card = card;
        redraw();
    }

    public Card getCard() { return card; }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            redraw();
        }
    }

    public void setPlayable(boolean playable) {
        if (this.playable != playable) {
            this.playable = playable;
            redraw();
        }
    }

    public boolean hit(int mx, int my) {
        return Math.abs(mx - getX()) <= W / 2 && Math.abs(my - getY()) <= H / 2;
    }

    private void redraw() {
        GreenfootImage img = new GreenfootImage(W, H);

        boolean raden = card.owner == Card.Owner.RADEN;
        boolean attack = card.type == Card.Type.ATTACK;
        Color face;
        if (raden) face = attack ? new Color(135, 45, 40) : new Color(45, 80, 140);
        else       face = attack ? new Color(45, 110, 60) : new Color(40, 115, 120);
        if (!playable) face = new Color(75, 75, 80);

        img.setColor(selected ? new Color(255, 220, 60) : new Color(225, 225, 225));
        img.fillRect(0, 0, W, H);
        img.setColor(face);
        img.fillRect(3, 3, W - 6, H - 6);

        // cost orb
        img.setColor(new Color(240, 150, 30));
        img.fillOval(5, 5, 26, 26);
        img.setColor(Color.BLACK);
        img.drawOval(5, 5, 26, 26);
        Gfx.text(img, "" + card.cost, 16, Color.BLACK, 18, 8);

        // owner label + name
        Gfx.text(img, raden ? "RADEN" : "DYAH", 11, new Color(255, 255, 255, 190), 66, 12);
        Gfx.text(img, card.name, 14, Color.WHITE, W / 2, 34);

        drawIcon(img);

        // description (up to 3 lines)
        for (int i = 0; i < card.lines.length; i++) {
            Gfx.text(img, card.lines[i], 11, new Color(240, 240, 240), W / 2, 98 + i * 12);
        }
        setImage(img);
    }

    private void drawIcon(GreenfootImage img) {
        img.setColor(new Color(0, 0, 0, 80));
        img.fillRect(8, 54, 84, 40);
        int cx = 50, cy = 74;
        boolean raden = card.owner == Card.Owner.RADEN;

        if (card.damage > 0 && raden && card.allEnemies) {            // crossed swords
            img.setColor(new Color(225, 225, 235));
            for (int i = -1; i <= 1; i++) {
                img.drawLine(cx - 16 + i, cy + 14, cx + 16 + i, cy - 14);
                img.drawLine(cx - 16 + i, cy - 14, cx + 16 + i, cy + 14);
            }
        } else if (card.damage > 0 && raden) {                       // sword
            img.setColor(new Color(225, 225, 235));
            for (int i = -1; i <= 1; i++) img.drawLine(cx - 14 + i, cy + 12, cx + 14 + i, cy - 14);
            img.setColor(new Color(210, 170, 60));
            for (int i = -1; i <= 1; i++) img.drawLine(cx - 15, cy + 2 + i, cx - 3, cy + 14 + i);
        } else if (card.damage > 0) {                                 // arrow(s)
            int rows = card.allEnemies ? 3 : 1;
            for (int r = 0; r < rows; r++) {
                int y = cy + (r - rows / 2) * 9;
                img.setColor(new Color(160, 110, 50));
                img.drawLine(cx - 20, y, cx + 14, y);
                img.setColor(new Color(230, 230, 235));
                img.fillPolygon(new int[]{cx + 12, cx + 22, cx + 12}, new int[]{y - 4, y, y + 4}, 3);
            }
        } else if (card.block > 0) {                                  // shield
            img.setColor(new Color(150, 200, 255));
            img.fillPolygon(new int[]{cx - 14, cx + 14, cx + 14, cx, cx - 14},
                            new int[]{cy - 15, cy - 15, cy + 3, cy + 17, cy + 3}, 5);
        } else if (card.heal > 0) {                                   // plus
            img.setColor(new Color(90, 220, 110));
            img.fillRect(cx - 4, cy - 15, 8, 30);
            img.fillRect(cx - 15, cy - 4, 30, 8);
        } else if (card.draw > 0) {                                   // two cards
            img.setColor(new Color(235, 235, 235));
            img.fillRect(cx - 16, cy - 14, 20, 28);
            img.setColor(new Color(200, 200, 215));
            img.fillRect(cx - 4, cy - 10, 20, 28);
        }

        if (card.mark) {                                              // small crosshair = "marks the enemy"
            img.setColor(new Color(255, 230, 60));
            img.drawOval(cx + 22, cy - 18, 16, 16);
            img.drawLine(cx + 30, cy - 22, cx + 30, cy);
            img.drawLine(cx + 18, cy - 10, cx + 42, cy - 10);
        }
    }
}
