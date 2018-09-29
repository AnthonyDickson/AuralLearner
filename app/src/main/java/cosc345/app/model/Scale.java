package cosc345.app.model;

/** Represents a musical scale. */
public class Scale {
    /** the notes in the scale. */
    public Note[] notes;

    /**
     * Create a scale starting of the given type (e.g. major/minor), starting at the given note.
     *
     * @param root the note to start the scale at.
     * @param scaleType the type of scale to create.
     */
    public Scale(Note root, ScaleType scaleType) {
        int[] scalePattern = scaleType.getSemitonePattern();

        int scaleSize = scalePattern.length + 1;
        notes = new Note[scaleSize];
        notes[0] = root;

        for (int i = 0; i < scalePattern.length; i++) {
            Note currNote = notes[i];
            String nextNoteName = Note.NOTE_NAMES[currNote.getNameIndex() + scalePattern[i]];
            Note nextNote = new Note(nextNoteName);
            notes[i + 1] = nextNote;
        }
    }

    /** Captures the different types of scales. */
    public enum ScaleType {
        MAJOR, NATURAL_MINOR, HARMONIC_MINOR, MELODIC_MINOR, MAJOR_PENTATONIC, MINOR_PENTATONIC;

        /**
         * Get a list of numbers where each number represents how many semitones higher the
         * next note in the given scale should be relative to the previous note.
         *
         * @return a list of numbers representing the semitone pattern of the given scale.
         */
        public int[] getSemitonePattern() {
            switch (this) {
                case MAJOR:
                    return new int[] {2, 2, 1, 2, 2, 2, 1};
                case NATURAL_MINOR:
                    return new int[] {2, 1, 2, 2, 1, 2, 2};
                case HARMONIC_MINOR:
                    return new int[] {2, 1, 2, 2, 1, 3, 1};
                case MELODIC_MINOR:
                    return new int[] {2, 1, 2, 2, 2, 2, 1};
                case MAJOR_PENTATONIC:
                    return new int[] {2, 2, 3, 2, 3};
                case MINOR_PENTATONIC:
                    return new int[] {3, 2, 2, 3, 2};
                default:
                    return new int[0];
            }
        }
    }
}
