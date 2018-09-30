package cosc345.app.model;

import android.util.Log;

import java.util.ArrayList;

/**
 * represents a melody.
 */
public class Melody extends Playable {
    private static final String LOG_TAG = "Melody";
    /**
     * the notes in the melody.
     */
    public ArrayList<Note> notes;
    public Scale scale;

    /**
     * Create a melody, using the notes from the given scale.
     *
     * @param scale               the scale to use when generating the melody.
     * @param melodyLength        how many notes long the melody should be.
     * @param maxStep             the maximum distance between any two notes in the melody.
     * @param range               the maximum distance from the root note that any other note
     *                            in the melody can be.
     * @param probablityToReverse the probability that the melody will reverse direction,
     *                            i.e. that the melody will start going down in pitch or vice versa.
     */
    public Melody(Scale scale, int melodyLength, int maxStep, int range, double probablityToReverse) {
        int noteIndex = 0;
        int maxRange = Math.min(range, scale.notes.size() - 1);
        double probabilityToNotReverse = Utilities.clamp(1.0 - probablityToReverse, 0.0, 1.0);

        this.scale = scale;
        notes = new ArrayList<>();
        notes.add(scale.notes.get(noteIndex));

        for (int i = 1; i < melodyLength - 2; i++) {
            int step = Utilities.random.nextInt(maxStep) + 1;

            if (i > melodyLength / 2 &&
                    Utilities.random.nextDouble() > probabilityToNotReverse) {
                step *= -1;
            }

            noteIndex = Utilities.clamp(noteIndex + step, 0, maxRange);
            notes.add(scale.notes.get(noteIndex));
        }

        if (noteIndex < scale.notes.size() / 2.0) {
            notes.add(scale.notes.get(1));
            notes.add(scale.notes.get(0));
        } else {
            notes.add(scale.notes.get(scale.notes.size() - 2));
            notes.add(scale.notes.get(scale.notes.size() - 1));
        }
    }

    @Override
    public void play() {
        super.play();

        Log.i(LOG_TAG, this.toString());

        playNote(0);
    }

    /**
     * Play each note in the melody recursively.
     *
     * @param index the index of the note to play.
     */
    private void playNote(int index) {
        if (index == notes.size()) {
            if (delegate != null) {
                delegate.onPlaybackFinished();
            }

            onDone();
        } else {
            Note next = notes.get(index);
            next.setDelegate(new Delegate() {
                @Override
                public void onPlaybackStarted() {
                }

                @Override
                public void onPlaybackFinished() {
                    playNote(index + 1);
                }

                @Override
                public void onDone() {

                }
            });

            next.play();
        }
    }

    @Override
    public void stop() {
        for (Note note : notes) {
            note.stop();
        }

        super.stop();
    }

    @Override
    public String toString() {
        return String.format("Melody in %s %s %s",
                notes.get(0).getName(),
                scale.scaleType,
                notes.toString());
    }
}
