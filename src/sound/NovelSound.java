/*
This was one of the lovely and great classes that required a lot of googling and taking stuff from there, resources and references used:
Class notes/slides
https://docs.oracle.com/javase/tutorial/sound/
https://docs.oracle.com/javase/tutorial/sound/controls.html
https://github.com/philfrei/AudioDicer
https://www.youtube.com/watch?v=Kux_LvRl57U&ab_channel=JonasTyroller
https://www.youtube.com/watch?v=mlCrrgZ2Dg4&ab_channel=MusicTechWithDr.E
https://www.youtube.com/watch?v=7gtfR61wJTQ&ab_channel=EDMProd
https://www.youtube.com/watch?v=qPVkRtuf9CQ&ab_channel=RyiSnow
https://www.youtube.com/watch?v=f0dwg99EVfo
https://www.youtube.com/watch?v=8SMAXTg0tMw&ab_channel=WolfSound
AI was used for brainstorming, understanding and interrogating and as always, proved useless.
*/

package sound;

import settings.Settings;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * NovelSound applies a simple low-pass filter to a WAV file before playback.
 */
public class NovelSound extends Thread {

    /** Path to the WAV file to play. */
    @SuppressWarnings("FieldMayBeFinal")
    private String filename;

    @SuppressWarnings("FieldMayBeFinal")
    private float volume;

    /**
     * Constructs a NovelSound with the specified filename.
     *
     * @param filename path to the WAV file
     */
    public NovelSound(String filename) {
        this.filename = filename;
        this.volume = Settings.getSoundEffectVolume();
    }//end constructor

    /**
     * Reads the audio data, applies a low-pass filter, and plays the filtered sound.
     */
    @Override
    public void run() {
        AudioInputStream audioInputStream = null;
        SourceDataLine line = null;
        try {
            File file = new File(filename);
            audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);

            //Volume control
            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                float safeVolume = Math.max(0.0001f, volume); //avoid log(0)
                float dB = (float) (Math.log10(safeVolume) * 20);
                gainControl.setValue(dB);
            }//end if
            else {
                System.out.println("Volume control not supported.");
            }//end else

            line.start();
            //Determine sample properties
            int bytesPerSample = format.getSampleSizeInBits() / 8; //typically 2 for 16-bit ??? TODO double check again
            int channels = format.getChannels();
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            byte[] filteredBuffer = new byte[bufferSize];

            //Keep previous sample for each channel for the low-pass filter
            int[] prevSample = new int[channels];

            int bytesRead;
            while ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                //Process the buffer in blocks corresponding to each sample per channel
                for (int i = 0; i < bytesRead; i += bytesPerSample * channels) {
                    for (int ch = 0; ch < channels; ch++) {
                        int index = i + ch * bytesPerSample;
                        //Ensure we have a complete sample in the buffer
                        if (index + 1 >= bytesRead) break;
                        //For 16-bit little-endian, combine two bytes into one sample
                        int sample = (buffer[index + 1] << 8) | (buffer[index] & 0xff);
                        //Apply a simple low-pass filter: average with previous sample
                        int filteredSample = (sample + prevSample[ch]) / 2;
                        //Update previous sample for this channel
                        prevSample[ch] = sample;
                        //Write the filtered sample back into the output buffer
                        filteredBuffer[index] = (byte)(filteredSample & 0xff);
                        filteredBuffer[index + 1] = (byte)((filteredSample >> 8) & 0xff);
                    }//end for loop
                }//end for loop
                //Write the filtered data to the audio line
                line.write(filteredBuffer, 0, bytesRead);
            }//end while loop
            line.drain();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("NovelSound error: " + e);
        } finally {
            if (line != null) {
                line.close();
            }//end if
            if (audioInputStream != null) {
                try {
                    audioInputStream.close();
                } catch (IOException e) {
                    //ignore
                }//end try-catch
            }//end if
        }//end try-catch-finally
    }//end run

}//end class
