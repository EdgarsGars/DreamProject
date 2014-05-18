/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import javax.swing.JFrame;

/**
 *
 * @author Edgar
 */
public class Launcher {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainGame game = new MainGame();
        frame.add(game);
        frame.setSize(800, 600);
        frame.setVisible(true);
        
        
        game.run();
    }
}
