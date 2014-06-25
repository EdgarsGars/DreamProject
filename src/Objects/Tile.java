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
    public static Image[][] dirtGrass;
    
    static {
        try {
            Image image = ImageIO.read(new File("src/resources/img/tiles.png"));
            dirtGrass = new Image[3][3];
            for(int i=0;i<9;i++){
                dirtGrass[i/3][i%3] = ((BufferedImage) image).getSubimage((i%3)*128, (i/3)*128, 128, 128);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

}
