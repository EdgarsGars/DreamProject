/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Game.MainGame;
import Objects.Player;
import Objects.Projectile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

/**
 *
 * @author Edgar
 */
public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Player player;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        player = new Player();
        GameServer.players.add(player);
    }

    @Override
    public void run() {
        try {
            getStreams();
            while (true) {
                String msg = input.readLine();
                if (msg == null) {
                    System.out.println("Lost connection to client!");
                    break;
                }
                processMessage(msg);
            }
        } catch (IOException ex) {

        }
        GameServer.users.remove(this);
    }

    public void getStreams() throws IOException {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        output.flush();
    }

    public void sendMessage(String msg) {
        output.write(msg + "\n");
        output.flush();
    }

    public void processMessage(String msg) {
        if (msg.startsWith("DISCONNECT")) {
            GameServer.users.remove(this);
            for (Player p : GameServer.players) {
                if (p.getUsername().equals(msg.split(" ")[1])) {
                    MobHandler.toRemove.add(p);
                    break;
                }
            }
            for (ClientHandler user : GameServer.users) {
                user.sendMessage("DISCONNECT " + msg.split(" ")[1]);
            }

        } else if (msg.startsWith("MOVE")) {
            System.out.println(msg);
            int x = Integer.parseInt(msg.split(" ")[2]);
            int y = Integer.parseInt(msg.split(" ")[3]);
            String name = msg.split(" ")[1];
            player.setUsername(name);
            player.setLocation(x, y);
            GameServer.sendToAllExecpt(msg, this);
            
            
            boolean toAdd = true;
            for (Player pl : GameServer.players) {
                if(pl.getUsername().equals(name)){
                    toAdd = false;
                    break;
                }
            }
            if(toAdd){
                GameServer.players.add(player);
            }
        } else if (msg.startsWith("GIVEMOB")) {
            GameServer.mobHandler.spawnMonster();

        } else if (msg.startsWith("PROJECTILE|")) {
            System.out.println(msg);
            int x = Integer.parseInt(msg.split("\\|")[1].split(" ")[0]);
            int y = Integer.parseInt(msg.split("\\|")[1].split(" ")[1]);
            int tx = Integer.parseInt(msg.split("\\|")[2].split(" ")[0]);
            int ty = Integer.parseInt(msg.split("\\|")[2].split(" ")[1]);
            GameServer.projectiles.add(new Projectile(x, y, tx, ty));

        } else if (msg.startsWith("DEAD")) {
            String username = msg.split(" ")[1];
            for (Player p : GameServer.players) {
                if (p.getUsername().equals(username)) {
                    MobHandler.toRemove.add(p);
                    break;
                }
            }
        }
        
        GameServer.sendToAll(msg);
    }

    @Override
    public boolean equals(Object obj
    ) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientHandler other = (ClientHandler) obj;
        if (!Objects.equals(this.socket, other.socket)) {
            return false;
        }
        return true;
    }

}
