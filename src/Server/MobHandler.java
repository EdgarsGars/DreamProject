/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Objects.Monster;
import java.util.ArrayList;

/**
 *
 * @author Edgar
 */
public class MobHandler implements Runnable {

    ArrayList<Monster> monsters = new ArrayList<>();
    boolean running = true;

    
    
    @Override
    public void run() {
        monsters.add(new Monster());
        while (running) {
            long lastLoopTime = System.nanoTime();
            final int TARGET_FPS = 60;
            final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
            while (!Thread.currentThread().isInterrupted()) {
                long now = System.nanoTime();
                lastLoopTime = now;
                
                update();
                
                try {
                    long sleepTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
                    if (sleepTime < 0) {
                        sleepTime *= -1;
                    }
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    System.out.println("aaaaa");
                }
            }
        }
    }
    
    public void update(){
        for (Monster monster : monsters) {
            monster.update();
            GameServer.sendToAll(monster.toString());
        }
    }

}
