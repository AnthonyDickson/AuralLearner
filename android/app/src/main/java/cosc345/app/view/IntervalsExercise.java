package cosc345.app.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cosc345.app.R;
import cosc345.app.lib.Interval;
import cosc345.app.lib.Note;
import cosc345.app.model.FFT;
import cosc345.app.model.PlayableInterval;
import cosc345.app.model.VoiceRecognitionManager;

public class IntervalsExercise extends AppCompatActivity implements FFT.FFTResultListener {
    private static final double VOLUME_THRESHOLD = 8e9;
    private boolean isListening, isPlaying;
    private Thread fftThread;
    private PlayableInterval targetInterval;
    private Interval userInterval;
    private Button startBtn;
    private Button stopBtn;
    private Button playTargetBtn;
    private Button stopTargetBtn;
    private TextView targetIntervalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intervals_exercise);

        isListening = false;
        isPlaying = false;

        startBtn = findViewById(R.id.intervals_startBtn);
        stopBtn = findViewById(R.id.intervals_stopBtn);
        playTargetBtn = findViewById(R.id.intervals_playTargetBtn);
        stopTargetBtn = findViewById(R.id.intervals_stopTargetBtn);
        targetIntervalView = findViewById(R.id.intervals_targetName);

        setTargetIntervalView(new PlayableInterval(new Note("C4"), Interval.Intervals.P5));

        startBtn.setOnClickListener(v -> startListening());
        stopBtn.setOnClickListener(v -> stopListening());
        playTargetBtn.setOnClickListener(v -> startTargetPlayback());
        stopTargetBtn.setOnClickListener(v -> stopTargetPlayback());
    }

    private void startListening() {
        if (!isListening) {
            if (isPlaying) {
                stopTargetPlayback();
            }

            // TODO: remove this line when bug with VoiceRecognitionManager is fixed.
            VoiceRecognitionManager.getInstance().close();

            startBtn.setVisibility(View.GONE);
            stopBtn.setVisibility(View.VISIBLE);
            fftThread = new Thread(new FFT(this));
            fftThread.start();
            isListening = true;
        }
    }

    private void stopListening() {
        if (isListening) {
            stopBtn.setVisibility(View.GONE);
            startBtn.setVisibility(View.VISIBLE);
            fftThread.interrupt();
            fftThread = null;
            isListening = false;
        }
    }

    private void startTargetPlayback() {
        if (isListening) {
            stopListening();
        }
        if (isPlaying) {
            stopTargetPlayback();
        }

        playTargetBtn.setVisibility(View.GONE);
        stopTargetBtn.setVisibility(View.VISIBLE);
        targetInterval.play();
        isPlaying = true;
    }

    private void stopTargetPlayback() {
        if (!isPlaying) {
            return;
        }

        targetInterval.stop();
        onPlaybackDone();
    }

    /**
     * Callback for when the tone is finished playing back.
     */
    private void onPlaybackDone() {
        stopTargetBtn.setVisibility(View.GONE);
        playTargetBtn.setVisibility(View.VISIBLE);
        isPlaying = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopListening();
        stopTargetPlayback();
    }

    @Override
    public void onFFTResult(double frequency, double amplitude, double averageFrequency,
                            double[] recentFrequencies) {
        if (!isListening || amplitude < IntervalsExercise.VOLUME_THRESHOLD) {
            return;
        }
    }

    private void setTargetIntervalView(PlayableInterval interval) {
        interval.setCallback(this::onPlaybackDone);
        targetInterval = interval;
        targetIntervalView.setText(interval.toString());
    }
}
