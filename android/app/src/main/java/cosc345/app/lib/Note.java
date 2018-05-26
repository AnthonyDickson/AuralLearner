package cosc345.app.lib;

/**
 * Represents a musical note.
 *
 * @author Anthony Dickson
 */
public class Note {
    private static final double MIN_FREQUENCY = 27.5;
    private static final double MAX_FREQUENCY = 4186.0;
    private static final int NUM_HALF_STEPS = 12; // per octave.
    private static final int NUM_CENTS = NUM_HALF_STEPS * 100; // per octave.
    private static final double A4_FREQUENCY = 440.0; // in Hertz
    private static final int A4_OCTAVE = 4;
    private static final int A4_INDEX = 57;
    private static final int HALF_STEPS_IN_OCTAVE_BELOW_A4 = 9; // before the octave changes.
    private static final String[] NOTE_NAMES = {
            "C0", "C#0", "D0", "D#0", "E0", "F0", "F#0", "G0", "G#0", "A0", "A#0", "B0",
            "C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1",
            "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2",
            "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3",
            "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4",
            "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5",
            "C6", "C#6", "D6", "D#6", "E6", "F6", "F#6", "G6", "G#6", "A6", "A#6", "B6",
            "C7", "C#7", "D7", "D#7", "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B7",
            "C8", "C#8", "D8", "D#8", "E8", "F8", "F#8", "G8", "G#8", "A8", "A#8", "B8",
            "C9", "C#9", "D9", "D#9", "E9", "F9", "F#9", "G9", "G#9", "A9", "A#9", "B9"};

    private static final String[] NOTE_NAMES_FLATS = {
            "C0", "Db0", "D0", "Eb0", "E0", "F0", "Gb0", "G0", "Ab0", "A0", "Bb0", "B0",
            "C1", "Db1", "D1", "Eb1", "E1", "F1", "Gb1", "G1", "Ab1", "A1", "Bb1", "B1",
            "C2", "Db2", "D2", "Eb2", "E2", "F2", "Gb2", "G2", "Ab2", "A2", "Bb2", "B2",
            "C3", "Db3", "D3", "Eb3", "E3", "F3", "Gb3", "G3", "Ab3", "A3", "Bb3", "B3",
            "C4", "Db4", "D4", "Eb4", "E4", "F4", "Gb4", "G4", "Ab4", "A4", "Bb4", "B4",
            "C5", "Db5", "D5", "Eb5", "E5", "F5", "Gb5", "G5", "Ab5", "A5", "Bb5", "B5",
            "C6", "Db6", "D6", "Eb6", "E6", "F6", "Gb6", "G6", "Ab6", "A6", "Bb6", "B6",
            "C7", "Db7", "D7", "Eb7", "E7", "F7", "Gb7", "G7", "Ab7", "A7", "Bb7", "B7",
            "C8", "Db8", "D8", "Eb8", "E8", "F8", "Gb8", "G8", "Ab8", "A8", "Bb8", "B8",
            "C9", "Db9", "D9", "Eb9", "E9", "F9", "Gb9", "G9", "Ab9", "A9", "Bb9", "B9"};

    private final int octave;
    private final int cents;
    private final int nameIndex;
    private final int halfStepDistance;

    /**
     * Create a musical note based on a frequency.
     *
     * @param frequency the frequency (in Hertz) to use.
     */
    public Note(double frequency) {
        if (frequency < MIN_FREQUENCY || frequency > MAX_FREQUENCY)
            throw new IllegalArgumentException();

        // Formulas found at http://newt.phys.unsw.edu.au/jw/notes.html
        int hsDist = halfStepDistance(frequency);
        double refFreq = frequency(hsDist);
        int centDist = (int) Math.round(NUM_CENTS * Math.log(frequency / refFreq) / Math.log(2.0));

        nameIndex = A4_INDEX + hsDist;
        halfStepDistance = hsDist;
        octave = octave(hsDist);
        cents = centDist % 100;
    }

    /**
     * Create a musical note from a string.
     *
     * @param name the name of the note that follows the format (Note Letter)[#|b](Octave).
     *             For example a note name may look like: A#3 or Db4.
     */
    public Note(String name) {
        int noteIndex = Utilities.indexOf(name, NOTE_NAMES);

        if (noteIndex < 0)
            noteIndex = Utilities.indexOf(name, NOTE_NAMES_FLATS);

        if (noteIndex < 0)
            throw new IllegalArgumentException("Invalid Note Name");

        nameIndex = noteIndex;
        cents = 0;
        octave = Integer.parseInt(Character.toString(name.charAt(name.length() - 1)));
        halfStepDistance = noteIndex - A4_INDEX;
    }

    /**
     * Calculate the octave of a note based on its distance in half steps from A4.
     *
     * @param halfStepDistance the distance in half steps of the note from A4.
     * @return the octave of a note.
     */
    public static int octave(int halfStepDistance) {
        // 1.0 is here to avoid calculation errors due to integer division/rounding.
        return (int) (A4_OCTAVE + 1.0 * (halfStepDistance + HALF_STEPS_IN_OCTAVE_BELOW_A4) / NUM_HALF_STEPS);
    }

    /**
     * Calculate the distance in half steps of a note from A4 based on a given frequency.
     *
     * @param frequency the frequency of the note.
     * @return the distance in half steps from A4.
     */
    public static int halfStepDistance(double frequency) {
        return (int) Math.round(NUM_HALF_STEPS * Math.log(frequency / A4_FREQUENCY) / Math.log(2.0));
    }

    /**
     * Calculate the frequency of a note given its distance in half steps from A4.
     *
     * @param halfStepsDistance the number of half steps from A4
     * @return the frequency of the note in Hertz. This is the frequency assuming perfect pitch.
     */
    public static double frequency(int halfStepsDistance) {
        return Math.pow(2, 1.0 * halfStepsDistance / NUM_HALF_STEPS) * A4_FREQUENCY;
    }

    /**
     * Calculate the frequency of a given note.
     *
     * @param note the note to calculate the frequency of.
     * @return the frequency of the note in Hertz. This is the frequency assuming perfect pitch.
     */
    public static double frequency(Note note) {
        return frequency(note.halfStepDistance);
    }

    /**
     * Getter for the octave of a note.
     *
     * @return the octave of the note.
     */
    public int getOctave() {
        return octave;
    }

    /**
     * Getter for the cents of a note.
     *
     * @return the cents of the note.
     */
    public int getCents() {
        return cents;
    }

    /**
     * Get the name of a note.
     *
     * @return the name of the note.
     */
    public String getName() {
        return NOTE_NAMES[nameIndex];
    }

    /**
     * Get the name of a note.
     *
     * @param useFlats whether or not to use flats (b) or sharps (#) in the name.
     * @return the name of the note.
     */
    public String getName(boolean useFlats) {
        if (useFlats) {
            return NOTE_NAMES_FLATS[nameIndex];
        } else {
            return getName();
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
