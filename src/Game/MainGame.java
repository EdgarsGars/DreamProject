/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Objects.GameObject;
import Objects.Monster;
import Objects.Player;
import Objects.Projectile;
import Objects.Tile;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Edgar
 */
public class MainGame extends JPanel implements KeyListener, MouseListener {

    public static ArrayList<GameObject> gameObjects = new ArrayList<>();
    public static ArrayList<Projectile> projectiles = new ArrayList<>();
    public static ArrayList<Monster> monsters = new ArrayList<>();
    public static ArrayList<GameObject> toRemove = new ArrayList<>();

    private int map[][] = new int[20][20];
    public HashSet<Player> players = new HashSet<>();
    private Player player = new Player();
    private final boolean[] keyDown = new boolean[4];
    private ConnectionToServer server;
    public static boolean running = true;

    public MainGame() {
        try {
            server = new ConnectionToServer(InetAddress.getByName("127.0.0.1"), 9999, this);
            addKeyListener(this);
            addMouseListener(this);
            String username = JOptionPane.showInputDialog("Enter your name: ");
            player.setUsername(username);
            player.setLocation(5, 5);
            players.add(player);

            setFocusable(true);
            new Thread(server).start();

            for (int i = 0; i < 400; i++) {
                map[i / 20][i % 20] = 1;
            }

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
        drawMap(g);
        drawGameObjects(g);
        for (Player p : players) {
            p.draw(g);
        }
    }

    public void drawMap(Graphics g) {
        for (int r = 0; r < map.length; r++) {
            for (int k = 0; k < map[0].length; k++) {
                int x = k * GameObject.tileSize;
                int y = r * GameObject.tileSize;
                if (map[r][k] == 1) {
                    g.drawImage(Tile.dirtGrass[1][1], x, y, GameObject.tileSize, GameObject.tileSize, null);
                }
                g.setColor(Color.black);
                g.drawRect(x, y, GameObject.tileSize, GameObject.tileSize);
                g.drawString(r + "," + k, x + 32, y + 32);
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

        for (Player p : players) {
            p.update();
        }
        for (Projectile p : projectiles) {
            p.update();
        }
        for (int i = 0; i < projectiles.size(); i++) {
            if (projectiles.get(i).checkRemove()) {
                i--;
            }
        }
        for (GameObject gameObject : toRemove) {
            if (gameObject instanceof Player) {
                players.remove((Player) gameObject);
            }
        }
        toRemove.clear();

        updateGameObjects();
        if (keyDown[0] || keyDown[1] || keyDown[2] || keyDown[3]) {
            GameSounds.playSound("footstep_grass.wav");
        }
        if (running) {
            server.sendMessage("MOVE" + " " + player.getUsername() + " " + player.getX() + " " + player.getY() + " ");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
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
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
            int x = e.getX();
            int y = e.getY();
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

    void disconnect() {
        running = false;
        server.sendMessage("DISCONNECT " + player.getUsername());
    }

}
