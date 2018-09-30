package cosc345.app.controller;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cosc345.app.model.Grader;
import cosc345.app.model.Note;
import cosc345.app.model.Playable;
import cosc345.app.model.TextToSpeechManager;

/**
 * A generalised form of the exercise activities. <br >
 * <br >
 * Any implementing class should override
 * the onCreate() method and make sure to: <br >
 * - set the activity layout<br >
 * - assign the startBtn and stopBtn buttons<br >
 * - set the startBtn onClickListener to startExercise(), and set the onClickListener of the stopBtn
 *   to be stopExercise()<br >
 * - override the startExercise() method and add the grader and target playable (where a target
 * would be an instance of Interval, Scale, or Melody) initialisation.
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

    protected void startExercise() {
        timesPlayed = 0;

        startBtn.setVisibility(View.GONE);
        stopBtn.setVisibility(View.VISIBLE);
    }


    protected void stopExercise() {
        if (grader != null) {
            grader.playable.stop();
            grader.stop();
        }

        showStartButton();
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

    protected void onGradingDone() {
        TextToSpeechManager.getInstance().speak(grader.getFeedback());
    }

    protected void showStartButton() {
        startBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.GONE);
    }
}
