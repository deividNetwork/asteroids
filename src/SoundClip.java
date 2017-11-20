
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;

public class SoundClip {

    Clip clip;

    public static final String THEME_SONG = "src/sound/theme.wav";

    public static final String FIRE_SONG = "src/sound/fire.wav";

    public static final String COLLISION_SONG = "src/sound/collision.wav";

    public static final String GAME_OVER_SONG = "src/sound/game_over.wav";

    /**
     * Constructor
     *
     * @param file
     */
    public SoundClip(String file) {
        try {
            // Open an audio input stream.           
            File soundFile = new File(file); //you could also get the sound file with an URL
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            // Get a sound clip resource.
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
        } catch (UnsupportedAudioFileException e) {
            Logger.getLogger(SoundClip.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException e) {
            Logger.getLogger(SoundClip.class.getName()).log(Level.SEVERE, null, e);
        } catch (LineUnavailableException e) {
            Logger.getLogger(SoundClip.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Play sound
     */
    public void play() {
        clip.setFramePosition(0);
        clip.loop(0);
        clip.start();
    }

    /**
     * Play sound loop mode
     */
    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Stop sound
     */
    public void stop() {
        clip.stop();
    }
}
