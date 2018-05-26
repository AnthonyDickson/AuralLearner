package cosc345.app.lib;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a musical interval.
 */
public class Interval {
    private static final Map<Integer, IntervalName> intervalNames;

    static {
        intervalNames = new HashMap<>();
        Interval.intervalNames.put(0, new IntervalName("Perfect unison", "P1"));
        Interval.intervalNames.put(1, new IntervalName("Minor second", "m2"));
        Interval.intervalNames.put(2, new IntervalName("Major second", "M2"));
        Interval.intervalNames.put(3, new IntervalName("Minor third", "m3"));
        Interval.intervalNames.put(4, new IntervalName("Major third", "M3"));
        Interval.intervalNames.put(5, new IntervalName("Perfect fourth", "P4"));
        Interval.intervalNames.put(6, new IntervalName("Augmented fourth", "A4"));
        Interval.intervalNames.put(7, new IntervalName("Perfect fifth", "P5"));
        Interval.intervalNames.put(8, new IntervalName("Minor sixth", "m6"));
        Interval.intervalNames.put(9, new IntervalName("Major sixth", "M6"));
        Interval.intervalNames.put(10, new IntervalName("Minor seventh", "m7"));
        Interval.intervalNames.put(11, new IntervalName("Major seventh", "M7"));
        Interval.intervalNames.put(12, new IntervalName("Perfect octave", "P8"));
    }

    public final IntervalName name;
    public final Note root;
    public final Note other;
    public final int size;

    public Interval(Note root, Intervals interval) {
        this(root, interval, false);
    }

    /**
     * Create an interval from a single note.
     *
     * @param root     the root note of the interval.
     * @param interval the interval to create.
     * @param invert   inverts the
     */
    public Interval(Note root, Intervals interval, boolean invert) {
        if (invert) {
            size = Math.abs(Note.NUM_HALF_STEPS - interval.ordinal());
            name = Interval.intervalNames.get(size);
            // What was the second note in the interval becomes the root.
            String newRootNodeName = Note.NOTE_NAMES[root.getNameIndex() + interval.ordinal()];
            this.root = new Note(newRootNodeName);
            // old root node raised one octave and becomes second note in interval.
            String otherNodeName = Note.NOTE_NAMES[root.getNameIndex() + Note.NUM_HALF_STEPS];
            other = new Note(otherNodeName);

        } else {
            size = interval.ordinal();
            name = Interval.intervalNames.get(size);
            this.root = root;
            String otherNoteName = Note.NOTE_NAMES[root.getNameIndex() + size];
            other = new Note(otherNoteName);
        }
    }

    /**
     * Create an interval from two notes.
     */
    public Interval(Note root, Note other) {
        size = Math.abs(root.getNameIndex() - other.getNameIndex());
        name = Interval.intervalNames.get(size % Note.NUM_HALF_STEPS);
        this.root = root;
        this.other = other;
    }

    @Override
    public String toString() {
        return name.getFullName() +
                " " +
                String.format("(%s, %s)", root.getName(), other.getName());
    }

    /**
     * Captures short names of musical intervals.
     */
    public enum Intervals {
        P1, m2, M2, m3, M3, P4, A4, P5, m6, M6, m7, M7, P8
    }

    /**
     * Captures both the full name and short name of an musical interval.
     */
    public static class IntervalName {
        private final String fullName;
        private final String shortName;

        IntervalName(String fullName, String shortName) {
            this.fullName = fullName;
            this.shortName = shortName;
        }

        public String getFullName() {
            return fullName;
        }

        public String getShortName() {
            return shortName;
        }

        @Override
        public String toString() {
            return getFullName();
        }
    }
}
