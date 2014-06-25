/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import static Objects.GameObject.image;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Edgar
 */
public class ANIMATIONTEST extends GameObject {

    static {
        try {
            image = ImageIO.read(new File("resources/img/ghost.png"));
        } catch (IOException ex) {
            System.out.println("File not found");
        }
    }

    private Image[] animation1 = new Image[9];
    private Image[] animation2;
    private Image[] animation3;

    
    private float frame;

    public ANIMATIONTEST() {
         for(int i=0;i<animation1.length;i++){
            animation1[i] = ((BufferedImage)image).getSubimage(i*84, 0, 84, 97);
        }
    }

    @Override
    public void update() {
        frame += 0.3f;
        if (frame > animation1.length) {
            frame = 0;
        }
        
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(animation1[(int)frame], x, y, null);
    }

}
