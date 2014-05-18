/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Objects.GameObject;
import Objects.Player;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author Edgar
 */
public class MainGame extends JPanel implements KeyListener {
    private ArrayList<GameObject> gameObjects = new ArrayList<>();
    private int map[][] = new int[10][10];
    private Player player = new Player();
    private boolean[] keyDown = new boolean[4];

    public MainGame() {
        addKeyListener(this);
        player.setLocation(5, 5);
        setFocusable(true);
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
                Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
            } catch (InterruptedException ex) {
                System.out.println("aaaaa");
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMap(g);
        player.draw(g);
    }
    
    
    public void drawMap(Graphics g){
        for (int r = 0; r < map.length; r++) {
            for(int k = 0; k < map[0].length; k++){
                int x = k*GameObject.tileSize;
                int y = r*GameObject.tileSize;
                g.setColor(Color.black);
                g.drawRect(x, y, GameObject.tileSize, GameObject.tileSize);
                g.drawString(r+","+k,x+32,y+32);
            }
        }
    }

    public void update() {
        player.update();
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

}
