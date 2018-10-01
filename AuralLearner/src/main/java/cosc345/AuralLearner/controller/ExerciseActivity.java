package cosc345.AuralLearner.controller;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cosc345.AuralLearner.model.Grader;
import cosc345.AuralLearner.model.Note;
import cosc345.AuralLearner.model.Playable;
import cosc345.AuralLearner.model.TextToSpeechManager;

/**
 * A generalised form of the exercise activities. <br >
 * <br >
 * Any implementing class should override
 * the onCreate() method and make sure to: <br >
 * - set the activity layout<br >
 * - assign the startBtn and stopBtn buttons<br >
 * - set the startBtn onClickListener to startExercise(), and set the onClickListener of the stopBtn
 *   to be stopExercise()<br >
 * - override the setupExercise() method and initialise grader.
 */
public abstract class ExerciseActivity extends AppCompatActivity implements Playable.Delegate {
    protected Button startBtn;
    protected Button stopBtn;
    protected Playable target;

    protected Grader grader;
    protected int timesPlayed;
    protected Handler handler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();

        TextToSpeechManager.getInstance().restart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopExercise();
        TextToSpeechManager.getInstance().close();
    }

    /**
     * Start the exercise.
     */
    protected void startExercise() {
        setupExercise();

        timesPlayed = 0;
        startBtn.setVisibility(View.GONE);
        stopBtn.setVisibility(View.VISIBLE);
        grader.setOnSuccessCallback(this::onGradingDone);
        grader.setCallback(this::showStartButton);
        target = grader.playable;
        target.setDelegate(this);

        TextToSpeechManager.getInstance().setOneTimeCallback(target::play);
        TextToSpeechManager.getInstance().speak(target.prettyPrint());
    }

    /**
     * Prepare the exercise. <br >
     * Implementing classes should use this method to: <br >
     *     - Initialise the grader for the exercise.
     */
    abstract void setupExercise();

    /**
     * Stop the exercise while it is in progress.
     */
    protected void stopExercise() {
        if (grader != null) {
            grader.playable.stop();
            grader.stop();
        }

        showStartButton();
        TextToSpeechManager.getInstance().pause();
    }

    @Override
    public void onPlaybackStarted() {
        timesPlayed++;
    }

    @Override
    public void onPlaybackFinished() {
        if (timesPlayed < 2) {
            handler.postDelayed(target::play, Note.NoteLengthMap.get(Note.NoteLength.CROTCHET));
        } else {
            grader.start();
        }
    }

    @Override
    public void onDone() {

    }

    /**
     * Give the user feedback on their score via text-to-speech.
     */
    protected void onGradingDone() {
        TextToSpeechManager.getInstance().speak(grader.getFeedback());
    }

    /**
     * Show the start button.
     */
    protected void showStartButton() {
        startBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.GONE);
    }
}
