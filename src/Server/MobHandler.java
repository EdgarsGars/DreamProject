/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Objects.Monster;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Edgar
 */
public class MobHandler implements Runnable {

    ArrayList<Monster> monsters = new ArrayList<>();
    boolean running = true;

    @Override
    public void run() {
        Monster m = new Monster();
        Monster m2 = new Monster();
        m2.setLocation(-50, 80);
        m.setLocation(100, 100);
        monsters.add(m);
        monsters.add(m2);
        while (running) {
            long lastLoopTime = System.nanoTime();
            final int TARGET_FPS = 60;
            final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
            while (!Thread.currentThread().isInterrupted()) {
                long now = System.nanoTime();
                lastLoopTime = now;

                update(0,monsters.size()/2);
                update(monsters.size()/2,monsters.size());

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

    public void update(final int start, final int end) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = start; i < end; i++) {
                    monsters.get(i).update();
                    if (!GameServer.users.isEmpty()) {
                        monsters.get(i).setTarget(new ArrayList<ClientHandler>(GameServer.users).get(0).getX() + new Random().nextInt(64), new ArrayList<ClientHandler>(GameServer.users).get(0).getY() + new Random().nextInt(64));
                        GameServer.sendToAll(monsters.get(i).toString());
                    }
                }
            }
        }).start();

    }

    public void spawnMonster() {
        int sk = new Random().nextInt(2);
        if (sk == 0) {
            Monster m = new Monster();
            m.setLocation(0, 0);
            monsters.add(m);
        } else if (sk == 1) {
            Monster m = new Monster();
            m.setLocation(100, 100);
            monsters.add(m);
        }

    }

}
