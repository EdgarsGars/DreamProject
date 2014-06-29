/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Game.MainGame;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Edgar
 */
public class Clementory extends GameObject {
    public static Image img;
    static {
        try {
            img = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("resources/img/mobSpawn.png"));
        } catch (IOException ex) {
            Logger.getLogger(Clementory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Clementory(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(img, x - (int) MainGame.camera.getX(), y - (int) MainGame.camera.getY(), 400, 400, null);
    }

}
