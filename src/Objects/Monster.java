/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import static Objects.GameObject.image;
import Server.ClientHandler;
import Server.GameServer;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
public class Monster extends GameObject {

    static {
        try {
            runAnimation = new Image[11];
            attackAnimation = new Image[12];
            image = ImageIO.read(new File("src/resources/img/skel_run.png"));
            init();

        } catch (IOException ex) {
            Logger.getLogger(Monster.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static Image[] runAnimation;
    private static Image[] attackAnimation;
    private Image[] activeAnimation;

    private float frame = 0;
    private static long ID = 0;
    private long monsterID;
    private int health = 3;
    private int targetX, targetY;
    private String direction = "RIGHT";
    
    
    public Monster() {
        monsterID = ++ID;
        activeAnimation = runAnimation;
    }

    public Monster(int monsterID) {
        this.monsterID = monsterID;
        activeAnimation = runAnimation;
    }

    @Override
    public void update() {
        if (!GameServer.users.isEmpty()) {
            if (Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY - y, 2)) < 10) {
                GameServer.sendToAll(this.toString()+" ATTACK");
            } else {
                x += (targetX - x) / 38;
                y += (targetY - y) / 38;
                GameServer.sendToAll(this.toString());
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        frame += 0.3f;
        if (frame > activeAnimation.length) {
            frame = 0;
        }
        String nDirection;
        System.out.println(targetX - x);
        
         if(targetX - x > 0){
            nDirection = "LEFT";
         }else nDirection= "RIGHT";

        System.out.println(nDirection);
        if (!direction.equals(nDirection)) {
            for (int i = 0; i < activeAnimation.length; i++) {
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-activeAnimation[i].getWidth(null), 0);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                activeAnimation[i] = op.filter((BufferedImage) activeAnimation[i], null);

            }
            direction = nDirection;
        }

        g.drawImage(activeAnimation[(int) frame], x, y, 80, 80, null);
    }

    public String toString() {
        return "MONSTER " + monsterID + " " + x + " " + y + " " + health + " "+targetX+" "+targetY+" ";
    }

    public static void init() throws IOException {
        for (int i = 0; i < runAnimation.length; i++) {
            runAnimation[i] = ((BufferedImage) image).getSubimage(i * 143, 0, 143, 178);
        }
        image = ImageIO.read(new File("src/resources/img/skel_attack.png"));
        for (int i = 0; i < attackAnimation.length; i++) {
            attackAnimation[i] = ((BufferedImage) image).getSubimage(i * 173, 0, 173, 167);
        }

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
        targetX = x;
        targetY = y;
    }
}
