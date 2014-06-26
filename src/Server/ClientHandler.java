/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

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
    private int x, y;
    private String name;

    public ClientHandler(Socket socket) {
        this.socket = socket;
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
        System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "] " + msg);
        if (msg.startsWith("DISCONNECT")) {
            GameServer.users.remove(this);
            for (ClientHandler user : GameServer.users) {
                user.sendMessage("DISCONNECT " + msg.split(" ")[1]);
            }
        } else if (msg.startsWith("MOVE")) {
            x = Integer.parseInt(msg.split(" ")[2]);
            y = Integer.parseInt(msg.split(" ")[3]);
            GameServer.sendToAllExecpt(msg, this);
        } else if(msg.startsWith("GIVEMOB")){
           GameServer.mobHandler.spawnMonster();
        }else{
           GameServer.sendToAll(msg);
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
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
