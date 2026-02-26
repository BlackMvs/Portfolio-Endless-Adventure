package gameExtended2D;

import game2D.Sound;
import settings.Settings;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import javax.sound.sampled.*;

/**
 * An extended version of the base {@link Sound} class that adds volume control support.
 * <p>
 * It plays sound effects with user-defined volume from {@link Settings},
 * using Java's sound API and gain adjustment via decibels.
 */
public class SoundExtended extends Sound {

    /** The playback volume (0.0 to 1.0) retrieved from game settings. */
    private float volume;

    /**
     * Constructs a new SoundExtended instance for the specified audio file.
     * Automatically applies the current sound effect volume from {@link Settings}.
     *
     * @param filePath the path to the sound file to be played
     */
    public SoundExtended(String filePath) {
        super(filePath);
        this.volume = Settings.getSoundEffectVolume();
    }//end constructor

    @Override
    public void run() {
        try {
            File file = new File(filename);
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat	format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip)AudioSystem.getLine(info);
            clip.open(stream);

            //Volume control
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                //Convert volume (0.0 to 1.0) to decibels
                float dB = (float) (Math.log10(volume) * 20);
                gainControl.setValue(dB);
            }//end if

            clip.start();
            Thread.sleep(100);
            while (clip.isRunning()) { Thread.sleep(100); }
            clip.close();
        } catch (Exception e) {
            System.out.println("SoundExtended error: " + e);
        }//end try-catch
        finished = true;
    }//end run

}//end class
