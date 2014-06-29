/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Game.GameSounds;
import Game.MainGame;
import static Objects.GameObject.image;
import Server.GameServer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import sun.security.jca.Providers;

/**
 *
 * @author Edgar
 */
public class Monster extends GameObject {

    public static int monsterSpeed = 3;
    public static int startAttackDistance = 70;
    public static int endAttackDistance = 80;

    static {
        try {
            runAnimationLeft = new Image[11];
            runAnimationRight = new Image[11];
            attackAnimationLeft = new Image[12];
            attackAnimationRight = new Image[12];

            dieAnimationLeft = new Image[12];
            dieAnimationRight = new Image[12];
            image = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("resources/img/skel_run.png"));
            init();

        } catch (IOException ex) {
            Logger.getLogger(Monster.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static Image[] runAnimationLeft;
    private static Image[] runAnimationRight;
    private static Image[] attackAnimationLeft;
    private static Image[] attackAnimationRight;
    private static Image[] dieAnimationLeft;
    private static Image[] dieAnimationRight;
    private Image[] activeAnimation;

    private float frame = 0;
    private static long ID = 0;
    private long monsterID;
    private int health = 5;

    private int targetX, targetY;
    private boolean attack = false;
    private boolean alive = true;

    public Monster() {
        monsterID = ++ID;
        activeAnimation = runAnimationLeft;
    }

    public Monster(int monsterID) {
        this.monsterID = monsterID;
        activeAnimation = runAnimationLeft;
    }

    @Override
    public void update() {
        if (!GameServer.users.isEmpty()) {
            double dist = Math.sqrt(Math.pow(targetX - x - 30, 2) + Math.pow(targetY - y - 30, 2));

            if (dist < startAttackDistance) {
                GameServer.sendToAll(this.toString() + " ATTACK");
                attack = true;
            } else {
                if (attack && dist > endAttackDistance) {
                    GameServer.sendToAll(this.toString() + " STOPATTACK");
                    cancelAttack();
                }
                if (!attack) {
                    double tx = targetX - x, ty = targetY - y;
                    x += (tx / dist) * monsterSpeed;
                    y += (ty / dist) * monsterSpeed;

                    GameServer.sendToAll(this.toString());
                }

            }
        }
    }

    @Override
    public void draw(Graphics g) {

        int xd = x - (int) MainGame.camera.getX();
        int yd = y - (int) MainGame.camera.getY();

        if (MainGame.showLines) {
            g.setColor(Color.green);
            if (attack) {
                g.setColor(Color.red);
            }
            g.drawLine(xd + 30, yd + 30, targetX - (int) MainGame.camera.getX(), targetY - (int) MainGame.camera.getY());
            g.drawString(Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY - y, 2)) + "", xd - 10, yd);
        }
        if (MainGame.camera.intersects(x, y, 1, 1)) {
            if (targetX - x > 30) {
                //Target is at right
                if (!alive) {
                    activeAnimation = dieAnimationRight;
                }
                else if (attack) {
                    activeAnimation = attackAnimationRight;
                }  else {
                    activeAnimation = runAnimationRight;
                }
            } else if (targetX - x < -30) {
                //Target is at Left
                if (!alive) {
                    activeAnimation = dieAnimationLeft;
                }if (attack) {
                    activeAnimation = attackAnimationLeft;
                }  else {
                    activeAnimation = runAnimationLeft;
                }
            }
            frame += 0.3f;
            if (frame > activeAnimation.length) {
                if(!alive)MainGame.toRemove.add(this);
                frame = 0;
            }
            if (alive && attack && frame == 1.8f) {
                if (Math.sqrt(Math.pow(x - MainGame.player.getX(), 2) + Math.pow(y - MainGame.player.getY(), 2)) <= 100) {
                    MainGame.server.sendMessage("HIT " + MainGame.player.getUsername());
                    GameSounds.playSound("male_hurt.wav");
                }
            }
            g.drawImage(activeAnimation[(int) frame], xd, yd, 80, 80, null);
        }
    }

    public String toString() {
        return "MONSTER " + monsterID + " " + x + " " + y + " " + health + " " + targetX + " " + targetY + " ";
    }

    public static void init() throws IOException {
        for (int i = 0; i < runAnimationLeft.length; i++) {
            runAnimationLeft[i] = ((BufferedImage) image).getSubimage(i * 143, 0, 143, 178);
            runAnimationRight[i] = ((BufferedImage) image).getSubimage(i * 143, 0, 143, 178);
        }
        image = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("resources/img/skel_attack.png"));
        for (int i = 0; i < attackAnimationLeft.length; i++) {
            attackAnimationLeft[i] = ((BufferedImage) image).getSubimage(i * 173, 0, 173, 167);
            attackAnimationRight[i] = ((BufferedImage) image).getSubimage(i * 173, 0, 173, 167);
        }
        image = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("resources/img/skel_die.png"));
        for (int i = 0; i < dieAnimationLeft.length; i++) {
            dieAnimationLeft[i] = ((BufferedImage) image).getSubimage(i * image.getWidth(null) / dieAnimationLeft.length, 0, image.getWidth(null) / dieAnimationLeft.length, image.getHeight(null));
            dieAnimationRight[i] = ((BufferedImage) image).getSubimage(i * image.getWidth(null) / dieAnimationLeft.length, 0, image.getWidth(null) / dieAnimationLeft.length, image.getHeight(null));
        }
        invertAnimation(attackAnimationLeft);
        invertAnimation(dieAnimationLeft);
        invertAnimation(runAnimationLeft);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Monster other = (Monster) obj;
        if (this.monsterID != other.monsterID) {
            return false;
        }
        return true;
    }

    public long getID() {
        return monsterID;
    }

    public void setTarget(int x, int y) {
        targetX = x + 10;
        targetY = y + 10;
    }

    public static void invertAnimation(Image[] animation) {
        for (int i = 0; i < animation.length; i++) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-animation[i].getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            animation[i] = op.filter((BufferedImage) animation[i], null);
        }
    }

    public void attack() {
        attack = true;
        activeAnimation = attackAnimationLeft;
    }

    public void cancelAttack() {
        attack = false;
        activeAnimation = runAnimationLeft;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void kill() {
        alive = false;
        frame = 0;
        GameSounds.playSound("zombie_death.wav");
    }

}
