/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author Edgar
 */
public class GameSounds {

    static boolean footstep = false;

    public static synchronized void playSound(final String name) {
        if(!footstep){
            footstep = true;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Clip clip = AudioSystem.getClip();
                        AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("src/resources/sounds/" + name));
                        clip.open(inputStream);
                        clip.start();
                        Thread.sleep(370);
                        footstep = false;
                        
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }

                }
            }).start();
        }
    }
}
