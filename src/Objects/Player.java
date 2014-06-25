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
import java.util.Objects;
import javax.imageio.ImageIO;

/**
 *
 * @author Edgar
 */
public class Player extends GameObject {

    static {
        try {
            image = ImageIO.read(new File("src/resources/img/chars.png"));
            leftAnimation = new Image[3];
            topAnimation = new Image[3];
            botAnimation = new Image[3];
            rightAnimation = new Image[3];

        } catch (IOException ex) {
            System.out.println("File not found");
        }
        init();
    }
    private static Image[] leftAnimation;
    private static Image[] rightAnimation;
    private static Image[] topAnimation;
    private static Image[] botAnimation;
    private Image[] activeAnimation;
    private Image activeImage;
    private String username;

    private int dy;
    private int dx;
    private float frame = 0;

    public Player() {
        width = GameObject.tileSize;
        height = GameObject.tileSize;
        activeAnimation = botAnimation;
        activeImage = activeAnimation[0];
    }

    public static void init() {
        for (int i = 0; i < leftAnimation.length; i++) {
            leftAnimation[i] = ((BufferedImage) image).getSubimage(i * 32, 32, 32, 32);
            rightAnimation[i] = ((BufferedImage) image).getSubimage(i * 32, 64, 32, 32);
            botAnimation[i] = ((BufferedImage) image).getSubimage(i * 32, 0, 32, 32);
            topAnimation[i] = ((BufferedImage) image).getSubimage(i * 32, 96, 32, 32);
        }
    }

    @Override
    public void update() {
        x += dx;
        y += dy;
        if (dx != 0 || dy != 0) {
            if (dx > 0) {
                activeAnimation = rightAnimation;
            } else if (dx < 0) {
                activeAnimation = leftAnimation;
            }
            if (dy > 0) {
                activeAnimation = botAnimation;
            } else if (dy < 0) {
                activeAnimation = topAnimation;
            }
            activeImage = activeAnimation[(int) frame];
        }

        frame += 0.3f;
        if (frame > activeAnimation.length) {
            frame = 0;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(activeImage, x, y, width, height, null);
        g.drawRect(x, y, width - 5, height);
        g.drawString(username, x-5, y);
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.username);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Player other = (Player) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return true;
    }

}
