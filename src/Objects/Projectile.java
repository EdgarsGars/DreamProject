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
    private int x,y,targetX,targetY;
    private double dist;
    
    public Projectile(int x, int y,int targetX, int targetY){
        this.x = x;
        this.y =y;
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
        g.setColor(Color.blue);
        g.fillOval(x, y, 10, 10);      
    }
    
    public boolean checkRemove(){
       if(dist < 10){
            MainGame.projectiles.remove(this);
            return true;
        }else return false;
    }

}
