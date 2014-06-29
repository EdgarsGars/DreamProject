/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Game.MainGame;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Edgar
 */
public class Projectile extends GameObject {

    private static final int speed = 10;
    private int targetX, targetY;
    private double dist;

    public Projectile(int x, int y, int targetX, int targetY) {
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public void update() {
        double tx = targetX - x, ty = targetY - y;
        dist = Math.sqrt(tx * tx + ty * ty);
        double dX = (tx / dist) * speed;
        double dY = (ty / dist) * speed;

        x += dX;
        y += dY;

    }

    @Override
    public void draw(Graphics g) {

        int xd = x - (int) MainGame.camera.getX();
        int yd = y - (int) MainGame.camera.getY();
        if (MainGame.camera.intersects(x, y, 1, 1)) {
            g.setColor(Color.blue);
            g.fillOval(xd, yd, 10, 10);
        }
    }

    public boolean checkRemove() {
        return dist < 10;
    }

}
