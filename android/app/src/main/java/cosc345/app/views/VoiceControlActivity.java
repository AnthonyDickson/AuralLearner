package cosc345.app.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cosc345.app.lib.TextToSpeechManager;
import cosc345.app.lib.VoiceRecognitionManager;

/**
 * An activity that uses and manages voice control and text-to-speech.
 */
public class VoiceControlActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


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
