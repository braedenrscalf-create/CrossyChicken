import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {
    private final int TILE_SIZE = 40;
    private final int MAX_SCREEN_COL = 23;
    private final int MAX_SCREEN_ROW = 17;
    private final int SCREEN_WIDTH = MAX_SCREEN_COL * TILE_SIZE; // 920
    private final int SCREEN_HEIGHT = MAX_SCREEN_ROW * TILE_SIZE; // 680

    private Thread gameThread;
    private final int FPS = 60;

    // Tile Map: 0 = Grass, 1 = Road, 2 = Water, 3 = Wall
    private final int[][] tileMap = {
        {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
        {3, 3, 2, 3, 3, 2, 3, 3, 3, 3, 2, 3, 3, 3, 2, 3, 3, 2, 3, 3, 3, 2, 3},
        {3, 3, 0, 3, 3, 0, 3, 3, 3, 3, 0, 3, 3, 3, 0, 3, 3, 0, 3, 3, 3, 0, 3},
        {3, 3, 0, 3, 3, 0, 3, 3, 3, 3, 0, 3, 3, 3, 0, 3, 3, 0, 3, 3, 3, 0, 3},
        {3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 0, 0, 3, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3},
        {3, 0, 0, 3, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3},
        {3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 0, 0, 3, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3},
        {3, 0, 0, 3, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3},
        {3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3},
        {3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3},
        {3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3},
        {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3}
    };

    private int spawnX, spawnY;
    private Chicken chicken;
    private List<Car> cars = new ArrayList<>();
    private List<CollectibleKey> keys = new ArrayList<>();
    private int collectedKeys = 0;
    
    private long startTime;
    private int timeLimitSeconds = 60;
    private boolean gameOver = false;
    private boolean hasWon = false;

    private boolean upPressed, downPressed, leftPressed, rightPressed;

    public GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        
        spawnX = (tileMap[0].length / 2) * TILE_SIZE;
        spawnY = (tileMap.length - 2) * TILE_SIZE;
        
        initGame();

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                    chicken.move(0, -1, tileMap, TILE_SIZE);
                }
                if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                    chicken.move(0, 1, tileMap, TILE_SIZE);
                }
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                    chicken.move(-1, 0, tileMap, TILE_SIZE);
                }
                if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                    chicken.move(1, 0, tileMap, TILE_SIZE);
                }
            }
        });
    }

    private void initGame() {
        chicken = new Chicken(spawnX, spawnY);

        // Populate cars dynamically based on road rows (value 1)
        for (int row = 0; row < tileMap.length; row++) {
            if (tileMap[row][1] == 1) {
                int yPos = row * TILE_SIZE;
                int speed = (row % 2 == 0) ? 3 : -3;
                Color col = (row % 2 == 0) ? Color.BLUE : Color.RED;
                cars.add(new Car(0, yPos, speed, SCREEN_WIDTH, col));
            }
        }

        // Find all water tile coordinates to spawn keys
        List<Point> waterPositions = new ArrayList<>();
        for (int row = 0; row < tileMap.length; row++) {
            for (int col = 0; col < tileMap[row].length; col++) {
                if (tileMap[row][col] == 2) {
                    waterPositions.add(new Point(col * TILE_SIZE, row * TILE_SIZE));
                }
            }
        }
        Collections.shuffle(waterPositions);

        // Spawn 6 keys
        for (int i = 0; i < Math.min(6, waterPositions.size()); i++) {
            Point p = waterPositions.get(i);
            keys.add(new CollectibleKey(p.x, p.y));
        }

        startTime = System.currentTimeMillis();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                if (!gameOver && !hasWon) {
                    update();
                }
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        // Update cars
        for (Car car : cars) {
            car.update();
            // Collision detection with chicken
            if (chicken.getBounds().intersects(car.getBounds())) {
                // Hit car - reset chicken position
                chicken.x = spawnX + 5;
                chicken.y = spawnY + 5;
            }
        }

        // Check key collections
        for (int i = keys.size() - 1; i >= 0; i--) {
            CollectibleKey k = keys.get(i);
            if (chicken.getBounds().intersects(k.getBounds())) {
                keys.remove(i);
                collectedKeys++;
                chicken.x = spawnX + 5;
                chicken.y = spawnY + 5;
                if (collectedKeys >= 6) {
                    hasWon = true;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw Tile Map
        for (int row = 0; row < tileMap.length; row++) {
            for (int col = 0; col < tileMap[row].length; col++) {
                int tileType = tileMap[row][col];
                switch (tileType) {
                    case 0: g2.setColor(new Color(46, 139, 87)); break; // Grass
                    case 1: g2.setColor(new Color(80, 80, 80)); break;  // Road
                    case 2: g2.setColor(new Color(30, 144, 255)); break;// Water
                    case 3: g2.setColor(new Color(112, 128, 144)); break;// Wall
                }
                g2.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // Draw Keys
        for (CollectibleKey k : keys) {
            k.draw(g2);
        }

        // Draw Cars
        for (Car car : cars) {
            car.draw(g2);
        }

        // Draw Chicken
        chicken.draw(g2);

        // Draw UI (Timer & Score)
        long secondsElapsed = (System.currentTimeMillis() - startTime) / 1000;
        long timeLeft = Math.max(0, timeLimitSeconds - secondsElapsed);
        if (timeLeft <= 0 && !hasWon) {
            gameOver = true;
        }

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        g2.drawString("Keys: " + collectedKeys + "/6", 20, 35);
        g2.drawString("Time Left: " + timeLeft, SCREEN_WIDTH - 200, 35);

        // End Game Messages
        if (hasWon) {
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(Color.YELLOW);
            g2.drawString("YOU WIN!", SCREEN_WIDTH / 2 - 120, SCREEN_HEIGHT / 2);
        } else if (gameOver) {
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            g2.setColor(Color.RED);
            g2.drawString("TIME'S UP!", SCREEN_WIDTH / 2 - 130, SCREEN_HEIGHT / 2);
        }

        g2.dispose();
    }
}