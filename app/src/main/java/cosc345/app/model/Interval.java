package cosc345.app.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static cosc345.app.model.Utilities.random;

/**
 * Represents a musical interval.
 */
public class Interval extends Playable {
    private static final String LOG_TAG = "Interval";
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

    public final Intervals interval;
    public final IntervalName name;
    public Note root;
    public Note other;
    public final int size;

    /**
     * Create an interval from a single note.
     *
     * @param root     the root note of the interval.
     * @param interval the interval to create.
     */
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
            this.interval = Intervals.values()[size % (Intervals.values().length - 1)];
            // What was the second note in the interval becomes the root.
            String newRootNoteName = Note.NOTE_NAMES[root.getNameIndex() + interval.ordinal()];
            this.root = new Note(newRootNoteName);
            // old root node raised one octave and becomes second note in interval.
            String otherNoteName = Note.NOTE_NAMES[root.getNameIndex() + Note.NUM_HALF_STEPS];
            other = new Note(otherNoteName);
        } else {
            // prevent out of range intervals.
            if (root.getNameIndex() + interval.ordinal() >= Note.NOTE_NAMES.length) {
                size = (Note.NOTE_NAMES.length - 1) - root.getNameIndex();
            } else {
                size = interval.ordinal();
            }

            name = Interval.intervalNames.get(size);
            this.interval = Intervals.values()[size % (Intervals.values().length - 1)];
            this.root = root;
            String otherNoteName = Note.NOTE_NAMES[root.getNameIndex() + size];
            other = new Note(otherNoteName);
        }

        other.setNoteLength(root.noteLength);
        setNoteDelegates();
    }

    /**
     * Create an interval from two notes.
     */
    public Interval(Note root, Note other) {
        size = Math.abs(root.getNameIndex() - other.getNameIndex());
        name = Interval.intervalNames.get(size % Note.NUM_HALF_STEPS);
        interval = Intervals.values()[size % (Intervals.values().length - 1)];
        this.root = root;
        this.other = other;

        setNoteDelegates();
    }

    /**
     * Generate a random interval, with a random root note.
     *
     * @return a new Interval object with the root note and interval type chosen randomly.
     */
    public static Interval randomInterval() {
        return randomInterval(Note.getRandom());
    }

    /**
     * Generate a random interval, with a given root note.
     *
     * @param root the root note of the interval to create.
     * @return a new Interval object with the interval type chosen randomly.
     */
    public static Interval randomInterval(Note root) {
        Intervals intervalSize = Intervals.values()[random.nextInt(Intervals.values().length)];
        return new Interval(root, intervalSize);
    }

    private void setNoteDelegates() {
        root.setDelegate(new PlayableDelegate() {
            @Override
            public void onPlaybackStarted() {}

            @Override
            public void onPlaybackFinished() {
                playNext();
            }
        });

        other.setDelegate(new PlayableDelegate() {
            @Override
            public void onPlaybackStarted() {}

            @Override
            public void onPlaybackFinished() {
                onDone();
            }
        });
    }

    public ArrayList<Note> getNotes() {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(root);
        notes.add(other);

        return notes;
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

    public static String[] getShortNames() {
        String[] names = new String[Interval.intervalNames.size()];

        for (int i = 0; i < names.length; i++) {
            names[i] = Interval.intervalNames.get(i).shortName;
        }

        return names;
    }

    public static String[] getFullNames() {
        String[] names = new String[Interval.intervalNames.size()];

        for (int i = 0; i < names.length; i++) {
            names[i] = Interval.intervalNames.get(i).fullName;
        }

        return names;
    }

    //// Playback related stuff ////
    @Override
    public void play() {
        if (isPlaying) return;

        super.play();
        Log.i(LOG_TAG, "Playing interval.");
        root.play();
    }

    @Override
    public void stop() {
        if (!isPlaying) return;

        Log.i(LOG_TAG, "Stopping interval playback.");
        isPlaying = false;
        root.stop();
        other.stop();
        onDone();
    }

    /**
     * Play the next note in the interval.
     */
    private void playNext() {
        if (!isPlaying) return;

        Log.i(LOG_TAG, "Playing next note.");
        other.play();
    }

    @Override
    protected void onDone() {
        super.onDone();
        Log.i(LOG_TAG, "Interval playback finished.");
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
