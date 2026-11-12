import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Chicken {
    public int x, y;
    public int width = 30;
    public int height = 30;
    private int speed = 40; // TILE_SIZE movement step

    public Chicken(int startX, int startY) {
        this.x = startX + 5;
        this.y = startY + 5;
    }

    public void move(int dx, int dy, int[][] tileMap, int tileSize) {
        int newX = x + (dx * speed);
        int newY = y + (dy * speed);

        // Grid boundaries check
        int col = newX / tileSize;
        int row = newY / tileSize;

        if (row >= 0 && row < tileMap.length && col >= 0 && col < tileMap[0].length) {
            // Check solid tiles (wall = 3)
            if (tileMap[row][col] != 3) {
                x = newX;
                y = newY;
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics2D g2) {
        // Draw Chicken (Yellow body, red comb)
        g2.setColor(new Color(255, 204, 0));
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.RED);
        g2.fillRect(x + 10, y - 8, 10, 8); // Comb
        g2.setColor(new Color(255, 102, 0));
        g2.fillRect(x + width, y + 10, 6, 6); // Beak
    }
}