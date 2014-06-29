/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Objects;

import Game.MainGame;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Edgar
 */
public class Tombstone extends GameObject{
    
    static{
        try {
            tmb = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("resources/img/tombstone2.png"));
        } catch (IOException ex) {
            Logger.getLogger(Tombstone.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Tombstone(int x,int y){
        this.x = x;
        this.y = y;
    }
    
    private static Image tmb;
    
    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(tmb,x-(int)MainGame.camera.getX(), y-(int)MainGame.camera.getY(), 64, 64, null);
    }
    
}
