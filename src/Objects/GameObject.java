/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.awt.Graphics;
import java.awt.Image;

/**
 *
 * @author Edgar
 */
public abstract class GameObject {
    public static final int tileSize = 64;
    protected int x;
    protected int y;
    protected int height;
    protected int width;
    protected static Image image;

    //Colllision boxes
    public abstract void update();

    public abstract void draw(Graphics g);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidht() {
        return width;
    }

    public void setWidht(int widht) {
        this.width = widht;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
