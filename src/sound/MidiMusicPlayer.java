package sound;

import settings.Settings;

import javax.sound.midi.*;
import java.io.File;

/**
 * MidiMusicPlayer handles background theme music playback using MIDI tracks.
 */
public class MidiMusicPlayer {
    private Sequencer sequencer;
    private Synthesizer synth;

    /**
     * Constructs a new MidiMusicPlayer and initializes the MIDI sequencer.
     */
    public MidiMusicPlayer() {
        try {
            this.sequencer = MidiSystem.getSequencer(false);
            if (this.sequencer != null) {
                this.sequencer.open();

                this.synth = MidiSystem.getSynthesizer();
                this.synth.open();

                //Connect the sequencer to the synthesizer's receiver
                Transmitter seqTransmitter = sequencer.getTransmitter();
                Receiver synthReceiver = synth.getReceiver();
                seqTransmitter.setReceiver(synthReceiver);
            }//end if
        } catch (MidiUnavailableException e) {
            System.out.println("Couldn't create midi player error: " + e);
        }//end try-catch
    }//end constructor

    /**
     * Loads and plays a MIDI file from the given file path.
     * If loop is true, the MIDI will play continuously.
     *
     * @param midiFilePath the path to the MIDI file
     * @param loop         true to loop continuously, false for one-time play
     */
    public void playMidi(String midiFilePath, boolean loop) {
        try {
            File midiFile = new File(midiFilePath);
            Sequence sequence = MidiSystem.getSequence(midiFile);
            sequencer.setSequence(sequence);
            if (loop) {
                //LOOP_CONTINUOUSLY (or -1) makes the sequencer repeat indefinitely
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            }//end if
            else {
                sequencer.setLoopCount(0);
            }//end else
            sequencer.start();
            setVolume();

        } catch (Exception e) {
            System.out.println("Couldn't player midi player error: " + e);
        }//end try-catch
    }//end playMidi

    /**
     * Sets the volume of all MIDI channels.
     */
    public void setVolume() {
        if (synth == null) return; //just in case

        float volume = Settings.getMusicVolume();

        MidiChannel[] channels = synth.getChannels();
        int midiVolume = Math.min(127, Math.max(0, (int) (volume * 127)));

        for (MidiChannel channel : channels) {
            if (channel != null) {
                channel.controlChange(7, midiVolume); //Controller 7 = Channel Volume
            }//end if
        }//end for
        if(Settings.getDebugMode()){
            System.out.println("MIDI volume set to " + midiVolume + " (" + (int)(volume * 100) + "%)");
        }//end if
    }//end setVolume

    /**
     * Stops the currently playing MIDI track.
     */
    public void stop() {
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();
        }//end if
    }//end stop

    /**
     * Closes the sequencer and releases system resources.
     */
    public void close() {
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.close();
        }//end if
        if (synth != null && synth.isOpen()) {
            synth.close();
        }//end if
    }//end close

}//end class
