package cosc345.app.model;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import cosc345.app.model.Callback;
import cosc345.app.model.Note;

/**
 * The Grader class takes a sequence of notes, gets the user to sing those notes, and gives a grade
 * based on how close what the user's singing was compared to the original sequence of notes.
 */
public class Grader implements FFT.FFTResultListener {
    static final String LOG_TAG = "Grader";

    int currNoteIndex;
    double score;
    ArrayList<Double> frequencyReadings;
    ArrayList<Note> notes;
    ArrayList<Note> userNotes;
    Thread fftThread = null;
    Callback callback = null;
    final Handler handler = new Handler();

    public Grader(ArrayList<Note> notes) {
        this.notes = notes;
        reset();
    }

    private void reset() {
        currNoteIndex = 0;
        score = 0.0;
        frequencyReadings = new ArrayList<>();
        userNotes = new ArrayList<>();
    }

    public void start() {
        reset();

        Log.i(LOG_TAG, "Starting grading.");
        fftThread = new Thread(new FFT(this));
        fftThread.start();

        Log.i(LOG_TAG, String.format("Recording singing for note %s with a duration of %d ms.", notes.get(currNoteIndex), notes.get(currNoteIndex).getDuration()));
        handler.postDelayed(this::onNoteDone, notes.get(currNoteIndex).getDuration());
    }

    public void stop() {
        Log.i(LOG_TAG, "Stopping grading.");
        handler.removeCallbacksAndMessages(null);

        if (fftThread != null) {
            fftThread.interrupt();
            fftThread = null;
        }

        if (callback != null) {
            callback.execute();
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onFFTResult(double frequency, double amplitude, double averageFrequency, double[] recentFrequencies) {
        frequencyReadings.add(frequency);
    }

    private void onNoteDone() {
        double avgFrequency = 0.0;

        for (double reading : frequencyReadings) {
            avgFrequency += reading;
        }

        avgFrequency /= frequencyReadings.size();
        frequencyReadings = new ArrayList<>();
        Note userNote;

        try {
            userNote = new Note(avgFrequency);
        } catch (IllegalArgumentException e) {
            userNote = new Note(avgFrequency < Note.MIN_FREQUENCY ? Note.MIN_FREQUENCY : Note.MAX_FREQUENCY);
        }

        Log.i(LOG_TAG, String.format("User sung: %s.", userNote));
        userNotes.add(userNote);
        currNoteIndex++;

        if (currNoteIndex == notes.size()) {
            onSequenceDone();
        } else {
            Log.i(LOG_TAG, String.format("Recording singing for note %s with a duration of %d ms.", notes.get(currNoteIndex), notes.get(currNoteIndex).getDuration()));
            handler.postDelayed(this::onNoteDone, notes.get(currNoteIndex).getDuration());
        }
    }

    private void onSequenceDone() {
        Log.d(LOG_TAG, String.format("Target notes: %s", notes.toString()));
        Log.d(LOG_TAG, String.format("User notes: %s", userNotes.toString()));
        score = calculateScore();
        Log.i(LOG_TAG, String.format("Finished grading, user's score is %f.", score));
        stop();
    }

    private double calculateScore() {
        double avgCentDist = 0.0;

        for (int i = 0; i < notes.size(); i++) {
            avgCentDist += Note.centDistance(userNotes.get(i), notes.get(i));
        }

        avgCentDist /= notes.size();

        if (Math.abs(avgCentDist) > 50) {
            return 0.0;
        } else {
            return 100 * (100 - Math.abs(avgCentDist) / 50);
        }
    }

    public double getScore() {
        return score;
    }
}
