package cosc345.AuralLearner.controller;

import android.support.v7.app.AppCompatActivity;

import cosc345.AuralLearner.model.TextToSpeechManager;
import cosc345.AuralLearner.model.VoiceRecognitionManager;

/**
 * An activity that uses and manages voice control and text-to-speech.
 */
public class VoiceControlActivity extends AppCompatActivity {


    @Override
    protected void onResume() {
        super.onResume();

        VoiceRecognitionManager.getInstance().restart();
        TextToSpeechManager.getInstance().restart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        VoiceRecognitionManager.getInstance().close();
        TextToSpeechManager.getInstance().close();
    }
}
