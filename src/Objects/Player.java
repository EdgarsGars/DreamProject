/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

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
public class Player extends GameObject {

    static {
        try {
            image = ImageIO.read(new File("resources/img/chars.png"));
        } catch (IOException ex) {
            System.out.println("File not found");
        }
    }
    private static Image[] leftAnimation = new Image[3];
    private static Image[] rightAnimation = new Image[3];
    private static Image[] topAnimation = new Image[3];
    private static Image[] botAnimation = new Image[3];

    private int dy;
    private int dx;
    private float frame = 0;

    public Player() {
        init();
        width = GameObject.tileSize;
        height = GameObject.tileSize;
        image = botAnimation[0];
    }

    public void init() {
        for(int i=0;i<leftAnimation.length;i++){
            leftAnimation[i] = ((BufferedImage)image).getSubimage(i*32, 32, 32, 32);
            rightAnimation[i] = ((BufferedImage)image).getSubimage(i*32, 64, 32, 32);
            botAnimation[i] = ((BufferedImage)image).getSubimage(i*32, 0, 32, 32);
            topAnimation[i] = ((BufferedImage)image).getSubimage(i*32, 96, 32, 32);
        }
    }

    @Override
    public void update() {
        x += dx;
        y += dy;
        
        if(dx > 0){
            image = rightAnimation[(int)frame];
        }else if(dx < 0){
            image = leftAnimation[(int)frame];
        }
        if(dy > 0){
            image = botAnimation[(int)frame];
        }else if(dy < 0){
            image = topAnimation[(int)frame];
        }
        
        frame += 0.3f;
        if(frame > 2){
            frame = 0;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(image, x, y, width, height, null);
        g.drawRect(x, y, width-5, height);
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

}
