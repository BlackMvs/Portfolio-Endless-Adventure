package sound;

import gameExtended2D.SoundExtended;

import java.util.HashMap;
import java.util.Map;

/**
 * SoundManager manages and plays multiple sound effects.
 * <p></p>
 * It supports playing sounds either normally using {@link SoundExtended} or
 * with a novel filter via {@link NovelSound}.
 */
public class SoundManager {

    /** Mapping from sound names to file paths. */
    private static Map<String, String> soundFiles = new HashMap<>();

    static {
        soundFiles.put("slash", "sounds/effects/swordSlash.wav");
        soundFiles.put("landing", "sounds/effects/landing.wav");
        soundFiles.put("jump", "sounds/effects/jump.wav");
        soundFiles.put("levelUp", "sounds/effects/levelUp.wav");
    }//end static

    //TODO change this to work with a enum, so when you call playsound, you cannot mispell
    public enum soundNames{
        JUMP,
        HIT,
        COIN,
        GAMEOVER
    }//end enum

    /**
     * Plays a sound effect normally using the Sound class.
     *
     * @param soundName the key for the sound effect
     */
    public static void playSound(String soundName) {
        String filePath = soundFiles.get(soundName);
        if (filePath != null) {
            SoundExtended sound = new SoundExtended(filePath);
            sound.start();
        }//end if
    }//end playSound

    /**
     * Plays a sound effect using the novel low-pass filter.
     *
     * @param soundName the key for the sound effect
     */
    public static void playFilteredSound(String soundName) {
        String filePath = soundFiles.get(soundName);
        if (filePath != null) {
            NovelSound novelSounds = new NovelSound(filePath);
            novelSounds.start();
        }//end if
    }//end playFilteredSound

}//end class

