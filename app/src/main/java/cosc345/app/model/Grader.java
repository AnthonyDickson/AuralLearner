package cosc345.app.model;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

/**
 * The Grader class takes a sequence of notes, gets the user to sing those notes, and gives a grade
 * based on how close what the user's singing was compared to the original sequence of notes.
 */
public class Grader implements PitchDetectionHandler {
    private static final String LOG_TAG = "Grader";

    private double score;
    private ArrayList<Double> frequencyReadings;
    private ArrayList<Note> notes;
    private Iterator<Note> notesIterator;
    private ArrayList<Note> userNotes;
    private PitchDetector pitchDetector;
    private Callback callback = null;
    private final Handler handler = new Handler();

    public Grader(ArrayList<Note> notes) {
        this.notes = notes;
        this.notesIterator = notes.iterator();
        this.pitchDetector = new PitchDetector(this);

        reset();
    }

    /**
     * Reset the state of the grader.
     */
    private void reset() {
        score = 0.0;
        frequencyReadings = new ArrayList<>();
        userNotes = new ArrayList<>();
    }

    /**
     * Get the user's score for the previously graded session.
     * This should be called after the grader has finished grading for best results.
     *
     * @return a double in the range [0.0, 100.0] that represents the user's score.
     */
    public double getScore() {
        return score;
    }

    /**
     * Set the callback to be called once grading is finished.
     *
     * @param callback the callback to be called after grading is finished.
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * Record the user's pitch.
     */
    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        float frequency = pitchDetectionResult.getPitch();

        if (frequency == -1) {
            Log.d(LOG_TAG, "Pitch Detection gave a reading of -1 for frequency during grading, " +
                    "skipping this reading.");
            return;
        }

        frequencyReadings.add((double) frequency);
    }

    /**
     * Start grading the user's singing.
     */
    public void start() {
        reset();

        Log.i(LOG_TAG, "Starting grading.");
        pitchDetector.start();
        playNextNote();
    }

    /**
     * Play each note in the grader's sequence of notes, and record the average frequency that the
     * user sang. When the end of the sequence is reached, finish grading.
     */
    private void playNextNote() {
        if (notesIterator.hasNext()) {
            Note nextNote = notesIterator.next();
            handler.postDelayed(this::onNoteDone, nextNote.getDuration());
        } else {
            onSequenceDone();
        }
    }

    /**
     * Calculate the average frequency the user sang for the last note, and then start
     */
    private void onNoteDone() {
        double avgFrequency = 0.0;

        for (double reading : frequencyReadings) {
            avgFrequency += reading;
        }

        avgFrequency /= frequencyReadings.size();
        frequencyReadings.clear();
        Note userNote;

        try {
            userNote = new Note(avgFrequency);
        } catch (IllegalArgumentException e) {
            userNote = new Note(avgFrequency < Note.MIN_FREQUENCY ? Note.MIN_FREQUENCY : Note.MAX_FREQUENCY);
        }

        Log.i(LOG_TAG, String.format("User sung: %s.", userNote));
        userNotes.add(userNote);

        playNextNote();
    }

    private void onSequenceDone() {
        Log.d(LOG_TAG, String.format("Target notes: %s", notes.toString()));
        Log.d(LOG_TAG, String.format("User notes: %s", userNotes.toString()));
        score = calculateScore();
        Log.i(LOG_TAG, String.format("Finished grading, user's score is %f.", score));
        stop();
    }

    /**
     * Calculate the user's score by comparing the distance of each note the user sang against the
     * grader's sequence of notes.
     * @return the user's score as a number in the range [0.0, 100.0]
     */
    private double calculateScore() {
        double avgCentDist = 0.0;

        for (int i = 0; i < notes.size(); i++) {
            double centDist = Note.centDistance(userNotes.get(i), notes.get(i));
            Log.d(LOG_TAG, String.format("Reference Pitch: %f Hz; User's Pitch: %f Hz; Distance in Cents: %f",
                    userNotes.get(i).getFrequency(),
                    notes.get(i).getFrequency(),
                    centDist));
            avgCentDist += centDist;
        }

        avgCentDist /= notes.size();

        if (Math.abs(avgCentDist) > 50) {
            return 0.0;
        } else {
            return 100 * (100 - Math.abs(avgCentDist) / 50);
        }
    }

    /**
     * Stop grading.
     */
    public void stop() {
        Log.i(LOG_TAG, "Stopping grading.");
        handler.removeCallbacksAndMessages(null);
        pitchDetector.stop();

        if (callback != null) {
            callback.execute();
        }
    }
}
