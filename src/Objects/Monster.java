/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Server.ClientHandler;
import Server.GameServer;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Edgar
 */
public class Monster extends GameObject {

    private static long ID = 0;
    private long monsterID;
    private int health = 3;

    public Monster() {
        monsterID = ++ID;
    }

    public Monster(int monsterID){
        this.monsterID = monsterID;
    }
    
    @Override
    public void update() {
        if(!GameServer.users.isEmpty()){
            x += (new ArrayList<>(GameServer.users).get(0).getX()- x) / 5;
            y += (new ArrayList<>(GameServer.users).get(0).getY()- y) / 5;
            GameServer.sendToAll(toString());
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(x, y, width, height);
    }

    public String toString(){
        return "MONSTER "+ monsterID+" "+x+" "+y+" "+health+" ";
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
    
    
    
}
