/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import static Objects.GameObject.image;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Edgar
 */
public class Tile {

    public static Image grass;

    static {
        try {
            grass = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("resources/img/ground.png"));

            grass = ((BufferedImage) grass).getSubimage(5*64,3*64,64,64);

        } catch (IOException ex) {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
