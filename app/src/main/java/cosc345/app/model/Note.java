package cosc345.app.model;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.NonNull;
import android.support.v4.media.AudioAttributesCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a musical note.
 */
public class Note extends Playable implements Comparable<Note>,
        AudioTrack.OnPlaybackPositionUpdateListener {
    public enum NoteLength {
        SEMIBREVE, DOTTED_SEMIBREVE, MINIM, DOTTED_MINIM, CROTCHET,
        DOTTED_CROTCHET, QUAVER, DOTTED_QUAVER, SEMIQUAVER, DOTTED_SEMIQUAVER

    }

    public static final Map<NoteLength, Integer> NoteLengthMap; // NoteLength to duration in ms.

    static {
        NoteLengthMap = new HashMap<>();
        NoteLengthMap.put(NoteLength.SEMIBREVE, 2000);
        NoteLengthMap.put(NoteLength.DOTTED_SEMIBREVE,
                (int) (NoteLengthMap.get(NoteLength.SEMIBREVE) * 1.5));
        NoteLengthMap.put(NoteLength.MINIM, 1000);
        NoteLengthMap.put(NoteLength.DOTTED_MINIM,
                (int) (NoteLengthMap.get(NoteLength.MINIM) * 1.5));
        NoteLengthMap.put(NoteLength.CROTCHET, 500);
        NoteLengthMap.put(NoteLength.DOTTED_CROTCHET,
                (int) (NoteLengthMap.get(NoteLength.CROTCHET) * 1.5));
        NoteLengthMap.put(NoteLength.QUAVER, 250);
        NoteLengthMap.put(NoteLength.DOTTED_QUAVER,
                (int) (NoteLengthMap.get(NoteLength.QUAVER) * 1.5));
        NoteLengthMap.put(NoteLength.SEMIQUAVER, 125);
        NoteLengthMap.put(NoteLength.DOTTED_SEMIQUAVER,
                (int) (NoteLengthMap.get(NoteLength.SEMIBREVE) * 1.5));
    }

    public static final String[] NOTE_NAMES = {
            "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2",
            "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3",
            "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
            "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5",
            "C6"};

    public static final String[] NOTE_NAMES_FLATS = {
            "C2", "Db2", "D2", "Eb2", "E2", "F2", "Gb2", "G2", "Ab2", "A2", "Bb2", "B2",
            "C3", "Db3", "D3", "Eb3", "E3", "F3", "Gb3", "G3", "Ab3", "A3", "Bb3", "B3",
            "C4", "Db4", "D4", "Eb4", "E4", "F4", "Gb4", "G4", "Ab4", "A4", "Bb4", "B4",
            "C5", "Db5", "D5", "Eb5", "E5", "F5", "Gb5", "G5", "Ab5", "A5", "Bb5", "B5",
            "C6"};

    public static final int A4_INDEX = 33;

    public static final double A4_FREQUENCY = 440.0; // in Hertz
    public static final int NUM_HALF_STEPS = 12; // per octave.
    private static final int A4_OCTAVE = 4;
    private static final int HALF_STEPS_IN_OCTAVE_BELOW_A4 = 9; // before the octave changes.
    private static final int NUM_CENTS = Note.NUM_HALF_STEPS * 100; // per octave.
    public static final double MIN_FREQUENCY = 63.57; // C2 minus 49 cents
    public static final double MAX_FREQUENCY = 1077.47; // C6 plus 50 cents
    private final int nameIndex;

    protected final double frequency;
    private final int halfStepDistance;
    private final int octave;
    private final int cents;
    protected int duration; // in ms.
    NoteLength noteLength;

    //// Audio playback related fields. ////
    private static final String LOG_TAG = "Note";
    private static final int SAMPLE_RATE = 8000; // per second.
    private AudioTrack audioTrack;
    private byte generatedSnd[];
    private int numSamples;
    private Thread thread;

    public Note(double frequency) {
        this(frequency, NoteLength.CROTCHET);
    }

    /**
     * Create a musical note based on a frequency.
     *
     * @param frequency  the frequency (in Hertz) to use.
     * @param noteLength the length of the note (e.g. crotchet).
     */
    public Note(double frequency, NoteLength noteLength) {
        if (frequency < Note.MIN_FREQUENCY || frequency > Note.MAX_FREQUENCY) {
            throw new IllegalArgumentException();
        }

        int hsDist = Note.halfStepDistance(frequency);
        double refFreq = Note.frequency(hsDist);

        nameIndex = Note.A4_INDEX + hsDist;
        this.frequency = frequency;
        halfStepDistance = hsDist;
        octave = Note.octave(hsDist);
        cents = Note.centDistanceClamped(frequency, refFreq);
        duration = NoteLengthMap.get(noteLength);
        this.noteLength = noteLength;

        generateTone();
    }

    /**
     * Create a crotchet length note from a note name.
     *
     * @param name the name of the note that follows the format (Note Letter)[#|b](Octave).
     *             For example a note name may look like: A#3 or Db4.
     */
    public Note(String name) {
        this(name, NoteLength.CROTCHET);
    }

    /**
     * Create a musical note from a string.
     *
     * @param name       the name of the note that follows the format (Note Letter)[#|b](Octave).
     *                   For example a note name may look like: A#3 or Db4.
     * @param noteLength the length of the note (e.g. crotchet).
     */

    public Note(String name, NoteLength noteLength) {
        int noteIndex = Utilities.indexOf(name, Note.NOTE_NAMES);

        if (noteIndex < 0) {
            noteIndex = Utilities.indexOf(name, Note.NOTE_NAMES_FLATS);
        }

        if (noteIndex < 0) {
            throw new IllegalArgumentException("Invalid Note Name");
        }

        nameIndex = noteIndex;
        halfStepDistance = noteIndex - Note.A4_INDEX;
        frequency = Note.frequency(halfStepDistance);
        octave = Note.octave(halfStepDistance);
        cents = 0;
        duration = NoteLengthMap.get(noteLength);
        this.noteLength = noteLength;

        generateTone();
    }

    /**
     * Create a note as a copy of another Note object.
     *
     * @param note the Note object to be copied.
     */
    public Note(Note note) {
        this.cents = note.cents;
        this.duration = note.duration;
        this.noteLength = note.noteLength;
        this.frequency = note.frequency;
        this.halfStepDistance = note.halfStepDistance;
        this.nameIndex = note.nameIndex;
        this.octave = note.octave;

        generateTone();
    }


    /**
     * Generate the tone of the note.
     */
    private void generateTone() {
        // Code adapted from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
        numSamples = Note.SAMPLE_RATE * duration / 1000;
        generatedSnd = new byte[2 * numSamples];

        Log.i(Note.LOG_TAG, "Generating tone.");
        double[] sample = new double[numSamples];

        // fill out the array
        double coefficient = 2 * Math.PI / (Note.SAMPLE_RATE / frequency);

        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(coefficient * i);
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (double dVal : sample) {
            // scale to maximum amplitude
            short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
    }

    public NoteLength getNoteLength() {
        return noteLength;
    }

    /**
     * Calculate the octave of a note based on its distance in half steps from A4.
     *
     * @param halfStepDistance the distance in half steps of the note from A4.
     * @return the octave of a note.
     */
    public static int octave(int halfStepDistance) {
        // 1.0 is here to avoid calculation errors due to integer division/rounding.
        return (int) (Note.A4_OCTAVE + 1.0 * (halfStepDistance + Note.HALF_STEPS_IN_OCTAVE_BELOW_A4) / Note.NUM_HALF_STEPS);
    }

    /**
     * Calculate the distance in half steps of a note from A4 based on a given frequency.
     *
     * @param frequency the frequency of the note.
     * @return the distance in half steps from A4.
     */
    public static int halfStepDistance(double frequency) {
        return (int) Math.round(Note.NUM_HALF_STEPS * Math.log(frequency / Note.A4_FREQUENCY) / Math.log(2.0));
    }

    /**
     * Calculate the frequency of a note given its distance in half steps from A4.
     *
     * @param halfStepsDistance the number of half steps from A4
     * @return the frequency of the note in Hertz. This is the frequency assuming perfect pitch.
     */
    public static double frequency(int halfStepsDistance) {
        return Math.pow(2, 1.0 * halfStepsDistance / Note.NUM_HALF_STEPS) * Note.A4_FREQUENCY;
    }

    /**
     * @return a note chosen at random.
     */
    public static Note getRandom() {
        double weighted_i = Utilities.random.nextGaussian() *
                Note.NUM_HALF_STEPS + Note.NOTE_NAMES.length / 2;
        int i = (int) Math.max(0, Math.min(weighted_i, Note.NOTE_NAMES.length));
        return new Note(Note.NOTE_NAMES[i], NoteLength.CROTCHET);
    }


    /**
     * Calculate the distance between two notes in cents.
     *
     * @param frequency          the frequency of the measured note.
     * @param referenceFrequency the frequency of the reference note.
     * @return the distance between the two notes in cents.
     */
    public static int centDistance(double frequency, double referenceFrequency) {
        return (int) Math.round(Note.NUM_CENTS *
                Math.log(frequency / referenceFrequency) / Math.log(2.0));
    }

    /**
     * Calculate the distance between two notes in cents.
     *
     * @param actual    the measured note.
     * @param reference the reference note.
     * @return the distance between the two notes in cents.
     */
    public static int centDistance(Note actual, Note reference) {
        return Note.centDistance(actual.getFrequency(), reference.getFrequency());
    }

    /**
     * Calculate the distance between two notes in cents.
     *
     * @param frequency          the frequency of the measured note.
     * @param referenceFrequency the frequency of the reference note.
     * @return the distance between the two notes in cents, clamped to a value between -50 and 50.
     */
    public static int centDistanceClamped(double frequency, double referenceFrequency) {
        int dist = Note.centDistance(frequency, referenceFrequency);

        if (Math.abs(dist) <= 50) {
            return dist;
        }

        return (50 + dist) % 100 + 50;
    }

    /**
     * Calculate the distance between two notes in cents.
     *
     * @param actual    the measured note.
     * @param reference the reference note.
     * @return the distance between the two notes in cents, clamped to a value between -50 and 50.
     */
    public static int centDistanceClamped(Note actual, Note reference) {
        return Note.centDistanceClamped(actual.getFrequency(), reference.getFrequency());
    }

    /**
     * Calculate the difference in half steps between this note and another.
     *
     * @param o the other note to compare with.
     * @return the half step distance between this note and the other.
     */
    @Override
    public int compareTo(@NonNull Note o) {
        return nameIndex - o.nameIndex;
    }

    /**
     * Check if one note is the same as the other.
     * Note that this only checks if the two notes have the same musical notation (e.g. both are A4),
     * but the difference in things like frequency are not taken into account.
     *
     * @param obj the other Note object to compare with.
     * @return true if the two notes are equivalent, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Note)) {
            return false;
        }

        return compareTo((Note) obj) == 0;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public double getFrequency() {
        return frequency;
    }

    public int getOctave() {
        return octave;
    }

    /**
     * @return the cents of the note from the closest note.
     */
    public int getCents() {
        return cents;
    }

    public void setNoteLength(NoteLength noteLength) {
        duration = NoteLengthMap.get(noteLength);
        this.noteLength = noteLength;

        generateTone();
    }

    public int getDuration() {
        return duration;
    }

    /**
     * Get the name of a note.
     *
     * @return the name of the note.
     */
    public String getName() {
        return Note.NOTE_NAMES[nameIndex];
    }

    /**
     * Get the name of a note.
     *
     * @param useFlats whether or not to use flats (b) or sharps (#) in the name.
     * @return the name of the note.
     */
    public String getName(boolean useFlats) {
        if (useFlats) {
            return Note.NOTE_NAMES_FLATS[nameIndex];
        } else {
            return getName();
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    //// Note Playback Stuff ////
    @Override
    public void play() {
        if (isPlaying) return;

        super.play();

        thread = new Thread(() -> {
            Log.i(Note.LOG_TAG, String.format("Playing note with a frequency of %.2f for %d ms",
                    frequency, duration));
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    Note.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                    AudioTrack.MODE_STATIC);
            audioTrack.write(generatedSnd, 0, generatedSnd.length);
            audioTrack.setNotificationMarkerPosition(numSamples);
            audioTrack.setPlaybackPositionUpdateListener(this);
            audioTrack.play();
        });

        thread.start();
    }

    @Override
    public void onMarkerReached(AudioTrack track) {
        onDone();
    }

    @Override
    public void onPeriodicNotification(AudioTrack track) {}

    @Override
    public void stop() {
        if (!isPlaying) return;

        if (audioTrack != null && audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.pause();
            audioTrack.flush();
        }

        if (thread != null) {
            thread.interrupt();
            thread = null;
        }

        onDone();
    }

    @Override
    protected void onDone() {
        super.onDone();

        if (audioTrack != null) {
            audioTrack.release();
        }

        Log.i(Note.LOG_TAG, "Playback finished.");
    }
}
