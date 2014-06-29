/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Objects.Clementory;
import Objects.GameObject;
import Objects.Monster;
import Objects.Player;
import Objects.Projectile;
import Objects.Tile;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Edgar
 */
//105/13
public class MainGame extends JPanel implements KeyListener, MouseListener, Runnable {

    static {
        try {
            uiHealth = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("resources/img/UI.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean showLines = false;
    public static boolean showGrid = false;
    public static boolean isAlive = true;

    public static HashSet<Player> players = new HashSet<>();
    public static ArrayList<GameObject> gameObjects = new ArrayList<>();
    public static ArrayList<Projectile> projectiles = new ArrayList<>();
    public static ArrayList<Monster> monsters = new ArrayList<>();
    public static ArrayList<GameObject> toRemove = new ArrayList<>();
    public static Player player = new Player();

    private int map[][] = new int[20][20];
    private String username = "";
    private static Image uiHealth;
    private static Image healthBar;
    private final boolean[] keyDown = new boolean[4];
    public static ConnectionToServer server;
    public static boolean running = true;
    public static Rectangle camera = new Rectangle(0, 0, 300, 300);
    private Timer timer;
    private int time = 0;

    public MainGame(String username) {
        try {
            server = new ConnectionToServer(InetAddress.getByName("127.0.0.1"), 9999, this);
            addKeyListener(this);
            addMouseListener(this);
            this.username = username;
            player.setUsername(username);
            player.setLocation(5, 5);
            players.add(player);
            healthBar = ((BufferedImage) uiHealth).getSubimage(240, 26, 102, 5);
            uiHealth = ((BufferedImage) uiHealth).getSubimage(5, 5, 221, 81);
            setFocusable(true);
            new Thread(server).start();

            for (int i = 0; i < map.length * map[0].length; i++) {
                map[i / map.length][i % map[0].length] = 1;
            }
            gameObjects.add(new Clementory(100, 100));
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    time++;
                }
            });
            timer.setRepeats(true);
            timer.start();

        } catch (UnknownHostException ex) {
            Logger.getLogger(MainGame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void run() {
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        while (!Thread.currentThread().isInterrupted()) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            double delta = updateLength / ((double) OPTIMAL_TIME);

            update();
            repaint();
            try {
                long sleepTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
                if (sleepTime < 0) {
                    sleepTime *= -1;
                }
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                System.out.println("aaaaa");
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int bx = (camera.getX() < 0) ? 0 : (int) camera.getX();
        int by = (camera.getY() < 0) ? 0 : (int) camera.getY();
        int ox = -1 * (bx - (int) camera.getX());
        int oy = -1 * (by - (int) camera.getY());

        drawMap(g);
        drawGameObjects(g);
        for (Player p : players) {
            p.draw(g);
        }
        g.clipRect(0, 0, getWidth(), getHeight());
        g.drawImage(uiHealth, 0, 0, 221, 81, null);
        g.drawImage(healthBar, 83, 17, 26 * player.getHealth(), 12, null);
        if (!isAlive) {
            timer.stop();
            g.setColor(Color.red);
            g.setFont(new Font("Sherif", 1, 50));
            g.drawString("YOU DIED!", getWidth() / 2 - 50, getHeight() / 2);
            g.drawString("Time survived : " + time, getWidth() / 2 - 46, getHeight() / 2 + 50);
        }

    }

    public void drawMap(Graphics g) {
        for (int r = 0; r < map.length; r++) {
            for (int k = 0; k < map[0].length; k++) {
                int x = k * GameObject.tileSize;
                int y = r * GameObject.tileSize;
                if (camera.intersects(x + GameObject.tileSize, y, 1, 1) || camera.intersects(x, y + GameObject.tileSize, 1, 1)
                        || camera.intersects(x - GameObject.tileSize, y, 1, 1) || camera.intersects(x + GameObject.tileSize, y + GameObject.tileSize, 1, 1)
                        || camera.intersects(x, y - GameObject.tileSize, 1, 1)) {

                    x -= (int) camera.getX();
                    y -= (int) camera.getY();
                    if (map[r][k] == 1) {
                        g.drawImage(Tile.grass, x, y, GameObject.tileSize, GameObject.tileSize, null);
                    }
                    if (showGrid) {
                        g.setColor(Color.black);
                        g.drawRect(x, y, GameObject.tileSize, GameObject.tileSize);
                        g.drawString(r + "," + k, x + 32, y + 32);
                    }

                }
            }
        }

    }

    public void drawGameObjects(final Graphics g) {
        for (GameObject gameObject : gameObjects) {
            gameObject.draw(g);
        }
        for (Projectile p : projectiles) {
            p.draw(g);
        }

        for (int i = 0; i < monsters.size(); i++) {
            monsters.get(i).draw(g);
        }
    }

    public void updateGameObjects() {
        for (GameObject gameObject : gameObjects) {
            gameObject.update();
        }
    }

    public void update() {
        if (player.getHealth() <= 0 && isAlive) {
            server.sendMessage("DEAD " + player.getUsername());
            isAlive = false;
        }

        for (Player p : players) {
            p.update();
        }
        for (Projectile p : projectiles) {
            p.update();
        }
        for (int i = 0; i < projectiles.size(); i++) {
            if (projectiles.get(i).checkRemove()) {
                MainGame.projectiles.remove(projectiles.get(i));
                i--;
            }
        }
        for (GameObject gameObject : toRemove) {
            if (gameObject instanceof Player) {
                players.remove((Player) gameObject);
            } else if (gameObject instanceof Monster) {
                monsters.remove((Monster) gameObject);
            }
        }
        toRemove.clear();

        updateGameObjects();

        if (isAlive && (keyDown[0] || keyDown[1] || keyDown[2] || keyDown[3])) {
            GameSounds.playSound("footstep_grass.wav");

        }
        if (isAlive && running) {
            server.sendMessage("MOVE" + " " + player.getUsername() + " " + player.getX() + " " + player.getY() + " " + player.getDirection() + " ");
        }

        updateCamera();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isAlive) {
            if (e.getKeyChar() == 'w') {
                player.setDy(-4);
                keyDown[2] = true;
            } else if (e.getKeyChar() == 's') {
                player.setDy(4);
                keyDown[3] = true;
            } else if (e.getKeyChar() == 'a') {
                player.setDx(-4);
                keyDown[0] = true;
            } else if (e.getKeyChar() == 'd') {
                keyDown[1] = true;
                player.setDx(4);
            } else if (e.getKeyChar() == ' ') {
                server.sendMessage("GIVEMOB");
            }
        } else {
            player = new Player();
            player.setUsername(username);
            players.add(player);
            isAlive = true;
            time = 0;
            timer.start();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
            keyDown[2] = false;
        } else if (e.getKeyChar() == 's') {
            keyDown[3] = false;
        } else if (e.getKeyChar() == 'a') {
            keyDown[0] = false;
        } else if (e.getKeyChar() == 'd') {
            keyDown[1] = false;
        }

        if (keyDown[0] && !keyDown[1]) {
            player.setDx(-4);
        } else if (!keyDown[0] && keyDown[1]) {
            player.setDx(4);
        } else {
            player.setDx(0);
        }

        if (keyDown[2] && !keyDown[3]) {
            player.setDy(-4);
        } else if (!keyDown[2] && keyDown[3]) {
            player.setDy(4);
        } else {
            player.setDy(0);
        }

        if (player.getDx() == 0 && player.getDy() == 0) {
            server.sendMessage("STOP " + player.getUsername() + " ");
            player.setMoving(false);
        } else {
            player.setMoving(true);
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        requestFocus();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
            int x = e.getX() + (int) camera.getX();
            int y = e.getY() + (int) camera.getY();
            server.sendMessage("PROJECTILE|" + (player.getX() + 32) + " " + (player.getY() + 32) + "|" + x + " " + y + "|");
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void disconnect() {
        running = false;
        server.sendMessage("DISCONNECT " + player.getUsername());
    }

    public void updateCamera() {
        int offsetMaxX = map[0].length * GameObject.tileSize - getWidth();
        int offsetMaxY = map.length * GameObject.tileSize - getHeight();
        int offsetMinX = 0;
        int offsetMinY = 0;

        int cameraX = player.getX() - getWidth() / 2;
        int cameraY = player.getY() - getHeight() / 2;

        camera = new Rectangle(cameraX, cameraY, getWidth(), getHeight());

    }

}
