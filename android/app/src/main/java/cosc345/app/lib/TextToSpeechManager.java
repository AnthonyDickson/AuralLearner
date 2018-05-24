package cosc345.app.lib;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * Encapsulate and manage text-to-speech.
 *
 * @author Anthony Dickson
 */
public class TextToSpeechManager {
    private static final String LOG_TAG = "TextToSpeech";

    private TextToSpeech tts;
    private Context parentContext;
    private HashMap<String, String> ttsParams = new HashMap<>();

    /**
     * Set up the text-to-speech service.
     *
     * @param parentContext
     * @param onStart the callback to called once TTS has begun.
     * @param onDone  the callback to called once TTS has finished.
     */
    public TextToSpeechManager(Context parentContext, Callback onStart, Callback onDone) {
        this.parentContext = parentContext;
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "id");
        tts = new TextToSpeech(parentContext, status -> {
            init(status, onStart, onDone);
        });
    }

    private void init(int status, Callback onStart, Callback onDone) {
        if (status != TextToSpeech.ERROR) {
            tts.setLanguage(Locale.UK);
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.i(LOG_TAG, "Utterance started");

                    if (onStart != null) {
                        onStart.execute();
                    }
                }

                @Override
                public void onDone(String utteranceId) {
                    Log.i(LOG_TAG, "Utterance finished");

                    if (onDone != null) {
                        onDone.execute();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e(LOG_TAG, String.format("An error occurred when speaking utterance %s.", utteranceId));
                }
            });

            Log.i(LOG_TAG, "TTS Initialised");
        }
    }

    /**
     * Make the device read out the given text.
     *
     * @param text The text to read out.
     */
    public void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, ttsParams);
    }

    /**
     * Completely stop and close the text-to-speech service.
     */
    public void close() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    public void pause() {
        if (tts != null) {
            tts.stop();
        }
    }
}
