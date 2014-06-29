/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Game.MainGame;
import Objects.GameObject;
import Objects.Monster;
import Objects.Player;
import Objects.Projectile;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 *
 * @author Edgar
 */
public class MobHandler implements Runnable {

    public static HashSet<GameObject> toRemove = new HashSet<>();
    ArrayList<Monster> monsters = new ArrayList<>();
    boolean running = true;

    @Override
    public void run() {
        while (running) {
            long lastLoopTime = System.nanoTime();
            final int TARGET_FPS = 60;
            final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
            while (!Thread.currentThread().isInterrupted()) {
                long now = System.nanoTime();
                lastLoopTime = now;

                for (Projectile p : GameServer.projectiles) {
                    p.update();
                    Monster r = null;
                    for (Monster m : monsters) {
                        if (new Rectangle(m.getX(), m.getY(), 100, 100).intersects(p.getX(), p.getY(), 1, 1)) {
                            m.setHealth(m.getHealth() - 1);
                            r = m;
                            break;
                        }
                    }
                    if (r != null) {
                        if (r.getHealth() == 0) {
                            GameServer.sendToAll("RMONSTER " + r.getID());
                            monsters.remove(r);
                        }
                        toRemove.add(p);
                    }

                }

                for (GameObject gameObject : toRemove) {
                    if (gameObject instanceof Projectile) {
                        GameServer.projectiles.remove((Projectile) gameObject);
                    }else if(gameObject instanceof Player){
                        GameServer.players.remove((Player)gameObject);
                    }
                }
                toRemove.clear();
                for (int i = 0; i < GameServer.projectiles.size(); i++) {
                    if (GameServer.projectiles.get(i).checkRemove()) {
                        GameServer.projectiles.remove(GameServer.projectiles.get(i));
                        i--;
                    }
                }

                if (!monsters.isEmpty()) {
                    update(0, monsters.size() / 2);
                    update(monsters.size() / 2, monsters.size());
                }

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
                        double min = 10000;
                        Player cl = null;
                        for (Player user : GameServer.players) {
                            double dist = Math.sqrt(Math.pow(user.getX() - monsters.get(i).getY(), 2) + Math.pow(user.getY() - monsters.get(i).getY(), 2));
                            if (dist < min) {
                                cl = user;
                                min = dist;
                            }
                        }
                        if(cl!=null)monsters.get(i).setTarget(cl.getX(), cl.getY() + new Random().nextInt(32));
                        GameServer.sendToAll(monsters.get(i).toString());
                    }
                }
            }
        }).start();

    }

    public void spawnMonster() {
        int sk = new Random().nextInt(2);
        Monster m = new Monster();
        if (sk == 0) {

            m.setLocation(0, 0);
        } else if (sk == 1) {
            m.setLocation(300, 300);
        }

        monsters.add(m);
    }

}
