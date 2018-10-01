package cosc345.AuralLearner.model;

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
    public ArrayList<Note> notes;
    private Iterator<Note> notesIterator;
    private ArrayList<Note> detectedNotes;
    private PitchDetector pitchDetector;
    private Callback callback = null;
    private Callback onSuccess = null;
    private final Handler handler = new Handler();
    private boolean shouldWaitForInput;
    private int timesWaited;

    /** The playable that this grader is grading, which is possibly null */
    public Playable playable;

    public Grader() {
        this(new ArrayList<>());
    }

    public Grader(ArrayList<Note> notes) {
        this.notes = notes;
        this.pitchDetector = new PitchDetector(this);
        timesWaited = 0;

        reset();
    }

    /**
     * Reset the state of the grader.
     */
    private void reset() {
        score = 0.0;
        frequencyReadings = new ArrayList<>();
        detectedNotes = new ArrayList<>();
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
     * Set the callback to be called once grading is finished.
     *
     * @param callback the callback to be called after grading is finished.
     */
    public void setOnSuccessCallback(Callback callback) {
        this.onSuccess = callback;
    }

    /**
     * Record the user's pitch.
     */
    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        float frequency = pitchDetectionResult.getPitch();

        if (frequency == -1 || timesWaited < 3) {
            if (shouldWaitForInput) {
                Log.d(LOG_TAG, "Pitch Detection gave a reading of -1 while waiting for input, " +
                        "waiting some more.");

                timesWaited++;
            } else {
                Log.d(LOG_TAG, "Pitch Detection gave a reading of -1 for frequency during grading, " +
                        "skipping this reading.");
            }
        } else if (shouldWaitForInput) {
            shouldWaitForInput = false;
            playNextNote();
        } else {
            frequencyReadings.add((double) frequency);
        }
    }

    /**
     * Start grading the user's singing.
     */
    public void start() {
        reset();

        Log.i(LOG_TAG, "Starting grading.");
        Log.i(LOG_TAG, String.format("Reference notes: %s", notes.toString()));
        shouldWaitForInput = true;
        notesIterator = notes.iterator();
        pitchDetector.start();
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
        double stddev = Utilities.stddev(frequencyReadings);
        double mean = Utilities.mean(frequencyReadings);

        double avgFrequency = 0.0;
        int numFrequencyReadings = 0;

        for (double frequency: frequencyReadings) {
            if (Math.abs(mean - frequency) < 2.0 * stddev) {
                avgFrequency += frequency;
                numFrequencyReadings++;
            }
        }

        avgFrequency /= numFrequencyReadings;

        Log.d(LOG_TAG, String.format("Average frequency: %s.", avgFrequency));
        frequencyReadings.clear();
        Note userNote;

        try {
            userNote = new Note(avgFrequency);
        } catch (IllegalArgumentException e) {
            userNote = new Note(avgFrequency < Note.MIN_FREQUENCY ? Note.MIN_FREQUENCY : Note.MAX_FREQUENCY);
        }

        Log.i(LOG_TAG, String.format("Detected Note: %s.", userNote));
        detectedNotes.add(userNote);

        playNextNote();
    }

    private void onSequenceDone() {
        Log.d(LOG_TAG, String.format("Reference notes: %s", notes.toString()));
        Log.d(LOG_TAG, String.format("Detected notes: %s", detectedNotes.toString()));
        score = calculateScore();
        Log.i(LOG_TAG, String.format("Finished grading, user's score is %f.", score));

        if (onSuccess != null) {
            onSuccess.execute();
        }

        stop();
    }

    /**
     * Calculate the user's score by comparing the distance of each note the user sang against the
     * grader's sequence of notes.
     *
     * @return the user's score as a number in the range [0.0, 100.0]
     */
    private double calculateScore() {
        int numCorrect = 0;

        for (int i = 0; i < notes.size(); i++) {
            Note detectedNote = detectedNotes.get(i);
            Note referenceNote = notes.get(i);

            double centDist = Note.centDistance(detectedNote, referenceNote);
            Log.d(LOG_TAG, String.format("Reference Pitch: %f Hz; Detected Pitch: %f Hz; Distance in Cents: %f",
                    referenceNote.getFrequency(),
                    detectedNote.getFrequency(),
                    centDist));

            if (detectedNote.getNameWithoutOctave().equals(referenceNote.getNameWithoutOctave())) {
                numCorrect++;
            }
        }

        return 100.0 * numCorrect / notes.size();
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

    /**
     * Get simple feedback on the previous grading session.
     *
     * @return feedback on the last grading session.
     */
    public String getFeedback() {
        if (score == 100.0) {
            return "Your score was perfect!";
        } else if (score >= 80.0) {
            return "Your score was great!";
        } else if (score >= 60.0) {
            return "Your score was good.";
        } else if (score >= 40.0) {
            return "Your score was ok.";
        } else {
            return "Your score was bad.";
        }
    }
}
