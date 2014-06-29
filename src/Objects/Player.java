/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Game.MainGame;
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
            image = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("resources/img/chars.png"));
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
    private int health;
    private String direction = "Left";
    private boolean moving = false;
    private int dy;
    private int dx;
    private float frame = 0;

    public Player() {
        width = GameObject.tileSize;
        height = GameObject.tileSize;
        activeAnimation = botAnimation;
        activeImage = activeAnimation[0];
        health = 14;
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
        if (MainGame.player.equals(this)) {
            x += dx;
            y += dy;

            if (dx != 0 || dy != 0) {
                moving = true;
                if (dx > 0) {
                    activeAnimation = rightAnimation;
                    direction = "Right";
                } else if (dx < 0) {
                    activeAnimation = leftAnimation;
                    direction = "Left";
                }
                if (dy > 0) {
                    activeAnimation = botAnimation;
                    direction = "Bot";
                } else if (dy < 0) {
                    activeAnimation = topAnimation;
                    direction = "Top";
                }
            }
            if (x < 0) {
                x = 0;
            }
            if (x > 19 * GameObject.tileSize) {
                x = 19 * GameObject.tileSize;
            }
            if (y < 0) {
                y = 0;
            }
            if (y > 19 * GameObject.tileSize) {
                y = 19 * GameObject.tileSize;
            }
        } else {
            switch (direction) {
                case "Left":
                    activeAnimation = leftAnimation;
                    break;
                case "Right":
                    activeAnimation = rightAnimation;
                    break;
                case "Top":
                    activeAnimation = topAnimation;
                    break;
                case "Bot":
                    activeAnimation = botAnimation;
                    break;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        if (moving) {
            frame += 0.3f;
            if (frame > activeAnimation.length) {
                frame = 0;
            }
            activeImage = activeAnimation[(int) frame];
        }
        int xd = x - (int) MainGame.camera.getX();
        int yd = y - (int) MainGame.camera.getY();
        if (MainGame.camera.intersects(x, y, 1, 1)) {
            g.drawImage(activeImage, xd, yd, width, height, null);
            g.drawRect(xd, yd, width - 5, height);
            g.drawString(username, xd, yd - 5);
        }
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

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = (health<0)? 0: health;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }
}
