package src.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import src.entity.Player;
import src.tile.TileManager;

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTitleSize = 16; // 16 * 16, tile or size of the sprite
    final int scale = 3; // scale for the tile to make it bigger or smaller

    public final int tileSize = originalTitleSize * scale; // 48*48, size of the tile on the screen
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    // screen is 16 * 12 so a ration of 4 * 3
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int maxWorldWidth = tileSize * maxWorldCol;
    public final int maxWorldHeight = tileSize * maxWorldRow;

    // FPS
    int FPS = 60;

    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    Thread gamThread;
    public CollisionChecker cChecker = new CollisionChecker(this);
    public Player player = new Player(this, keyH);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // preffered size for the window
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true); // the gamepanel can be focused on to read key input
    }

    public void startGameThread() {

        gamThread = new Thread(this);
        gamThread.start();
    }


    //draw and update at the fps rate with System.nanoTime() to get the current time
    @Override
    public void run() {
        //TO DO
    }

    public void update() {
        player.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        tileM.draw(g2);
        player.draw(g2);

        g2.dispose();
    }
}
