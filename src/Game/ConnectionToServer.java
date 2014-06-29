/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import static Game.MainGame.monsters;
import Objects.Monster;
import Objects.Player;
import Objects.Projectile;
import Objects.Tombstone;
import Server.GameServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Edgar
 */
public class ConnectionToServer implements Runnable {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private MainGame game;

    public ConnectionToServer(InetAddress host, int port, MainGame game) {
        try {
            socket = new Socket(host, port);
            this.game = game;
        } catch (IOException ex) {
            Logger.getLogger(ConnectionToServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        try {
            getStreams();
            while (true) {
                final String msg = input.readLine();
                if (msg == null) {
                    System.out.println("Connection Lost!");
                    break;
                }

                processMessage(msg);

            }
        } catch (IOException ex) {
            Logger.getLogger(ConnectionToServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void processMessage(String data) {
        if (data.startsWith("CONNECT")) {

        } else if (data.startsWith("MOVE")) {
            Player player = new Player();
            try {
                String username = data.split(" ")[1];
                player.setUsername(username);
                int x = Integer.parseInt(data.split(" ")[2]);
                int y = Integer.parseInt(data.split(" ")[3]);
                String direction = data.split(" ")[4];
                if (!MainGame.players.contains(player)) {
                    player.setLocation(x, y);
                    MainGame.players.add(player);
                } else {
                    for (Player pl : MainGame.players) {
                        if (pl.getUsername().equals(username) && !pl.equals(MainGame.player)) {
                            if (pl.getX() != x || pl.getY() != y) {

                                pl.setMoving(true);
                            }
                            pl.setLocation(x, y);
                            pl.setDirection(direction);
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        } else if (data.startsWith("PROJECTILE")) {
            int x = Integer.parseInt(data.split("\\|")[1].split(" ")[0]);
            int y = Integer.parseInt(data.split("\\|")[1].split(" ")[1]);
            int tx = Integer.parseInt(data.split("\\|")[2].split(" ")[0]);
            int ty = Integer.parseInt(data.split("\\|")[2].split(" ")[1]);
            MainGame.projectiles.add(new Projectile(x, y, tx, ty));
        } else if (data.startsWith("DISCONNECT")) {
            for (final Player pl : MainGame.players) {
                if (pl.getUsername().equals(data.split(" ")[1])) {
                    MainGame.toRemove.add(pl);
                }
            }
        } else if (data.startsWith("MONSTER")) {
            int ID = Integer.parseInt(data.split(" ")[1]);
            int x = Integer.parseInt(data.split(" ")[2]);
            int y = Integer.parseInt(data.split(" ")[3]);
            int targetX = Integer.parseInt(data.split(" ")[5]);
            int targetY = Integer.parseInt(data.split(" ")[6]);
            Monster m = new Monster(Integer.parseInt(data.split(" ")[1]));
            m.setTarget(targetX, targetY);
            m.setLocation(x, y);
            if (!monsters.contains(m)) {
                monsters.add(m);
            } else {
                for (Monster monster : monsters) {
                    if (monster.getID() == ID) {
                        monster.setLocation(x, y);
                        monster.setTarget(targetX, targetY);
                        if (data.contains("STOPATTACK")) {
                            monster.cancelAttack();
                        } else if (data.contains("ATTACK")) {
                            monster.attack();
                        }
                    }
                }
            }
        } else if (data.startsWith("RMONSTER")) {
            int ID = Integer.parseInt(data.split(" ")[1]);
            for (Monster monster : monsters) {
                if (monster.getID() == ID) {
                    System.out.println("REMOVE");
                    monster.kill();

                }

            }

        } else if (data.startsWith("HIT")) {
            //HIT playername
            String username = data.split(" ")[1];
            for (Player pl : MainGame.players) {
                if (pl.getUsername().equals(username)) {
                    pl.setHealth(pl.getHealth() - 1);
                }
            }
        } else if (data.startsWith("DEAD ")) {

            for (Player pl : MainGame.players) {
                if (pl.getUsername().equals(data.split(" ")[1])) {
                    MainGame.gameObjects.add(new Tombstone(pl.getX(), pl.getY()));
                    MainGame.toRemove.add(pl);
                    break;
                }
            }
        } else if (data.startsWith("STOP")) {
            for (Player pl : MainGame.players) {

                if (pl.getUsername().equals(data.split(" ")[1])) {
                    System.out.println(data);
                    pl.setMoving(false);
                    break;
                }
            }
        }

    }

    public void sendMessage(String msg) {
        output.write(msg + "\n");
        output.flush();
    }

    public void getStreams() throws IOException {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        output.flush();
    }

}
