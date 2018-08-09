package cosc345.app.lib;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a musical note.
 */
public class Note implements Comparable<Note> {
    public enum NoteLength {SEMIBREVE, MINIM, CROTCHET, QUAVER, SEMIQUAVER}

    public static final Map<NoteLength, Integer> NoteLengthMap; // NoteLength to duration in ms.

    static {
        NoteLengthMap = new HashMap<>();
        NoteLengthMap.put(NoteLength.SEMIBREVE, 2000);
        NoteLengthMap.put(NoteLength.MINIM, 1000);
        NoteLengthMap.put(NoteLength.CROTCHET, 500);
        NoteLengthMap.put(NoteLength.QUAVER, 250);
        NoteLengthMap.put(NoteLength.SEMIQUAVER, 125);
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
    private static final double MIN_FREQUENCY = 63.57; // C2 minus 49 cents
    private static final double MAX_FREQUENCY = 1077.47; // C6 plus 50 cents

    private final int nameIndex;
    protected final double frequency;
    private final int halfStepDistance;
    private final int octave;
    private final int cents;
    protected int duration; // in ms.

    public Note(double frequency) {
        this(frequency, NoteLength.CROTCHET, false);
    }

    /**
     * Create a musical note based on a frequency.
     *
     * @param frequency       the frequency (in Hertz) to use.
     * @param noteLength      the length of the note (e.g. crotchet).
     * @param useDottedLength whether or not the note length is dotted or not.
     */
    public Note(double frequency, NoteLength noteLength, boolean useDottedLength) {
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
        duration = (int) (NoteLengthMap.get(noteLength) * (useDottedLength ? 1.5 : 1.0));
    }

    /**
     * Create a crotchet length note from a note name.
     *
     * @param name the name of the note that follows the format (Note Letter)[#|b](Octave).
     *             For example a note name may look like: A#3 or Db4.
     */
    public Note(String name) {
        this(name, NoteLength.CROTCHET, false);
    }

    /**
     * Create a musical note from a string.
     *
     * @param name the name of the note that follows the format (Note Letter)[#|b](Octave).
     *             For example a note name may look like: A#3 or Db4.
     * @param noteLength      the length of the note (e.g. crotchet).
     * @param useDottedLength whether or not the note length is dotted or not.
     */
    public Note(String name, NoteLength noteLength, boolean useDottedLength) {
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
        duration = (int) (NoteLengthMap.get(noteLength) * (useDottedLength ? 1.5 : 1.0));
    }

    /**
     * Create a note as a copy of another Note object.
     *
     * @param note the Note object to be copied.
     */
    public Note(Note note) {
        this.cents = note.cents;
        this.duration = note.duration;
        this.frequency = note.frequency;
        this.halfStepDistance = note.halfStepDistance;
        this.nameIndex = note.nameIndex;
        this.octave = note.octave;
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
        return new Note(Note.NOTE_NAMES[i], NoteLength.CROTCHET, false);
    }


    /**
     * Calculate the distance between two notes in cents.
     *
     * @param frequency          the frequency of the measured note.
     * @param referenceFrequency the frequency of the reference note.
     * @return the distance between the two notes in cents.
     */
    public static int centDistance(double frequency, double referenceFrequency) {
        int centDist = (int) Math.round(Note.NUM_CENTS *
                Math.log(frequency / referenceFrequency) / Math.log(2.0));

        return centDist;
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

    public void setDuration(NoteLength noteLength, boolean useDottedLength) {
        duration = (int) (NoteLengthMap.get(noteLength) * (useDottedLength ? 1.5 : 1.0));
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
}
