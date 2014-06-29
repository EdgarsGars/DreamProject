/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Objects.GameObject;
import Objects.Player;
import Objects.Projectile;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Edgar
 */
public class GameServer {

    private ServerSocket server;
    public static MobHandler mobHandler = new MobHandler();
    public static HashSet<ClientHandler> users = new HashSet<>();
    public static ArrayList<Player> players = new ArrayList<>();
    public static ArrayList<Projectile> projectiles = new ArrayList();
    public static ArrayList<GameObject> tombstones = new ArrayList<>();

    public GameServer() {
        try {
            server = new ServerSocket(9999);
        } catch (IOException ex) {
            System.out.println("Couldn't start a server..");
        }
        System.out.println("Server started..");
    }

    public void run() {
        new Thread(mobHandler).start();
        while (true) {
            try {
                Socket socket = server.accept();
                ClientHandler client = new ClientHandler(socket);
                users.add(client);
                System.out.println("Active users " + users.size());
                new Thread(client).start();
            } catch (IOException ex) {
                System.out.println("EXCEPTION");
            }
        }
    }

   public static void main(String[] args) {
        GameServer server = new GameServer();
        server.run();
    }

    public static void sendToAll(String msg){
        for (ClientHandler clientHandler : users) {
            clientHandler.sendMessage(msg);
        }
    }
    
    public static void sendToAllExecpt(String msg,ClientHandler except){
        for (ClientHandler clientHandler : users) {
            if(!clientHandler.equals(except)) clientHandler.sendMessage(msg);
        }
    }
    
    
    
}
