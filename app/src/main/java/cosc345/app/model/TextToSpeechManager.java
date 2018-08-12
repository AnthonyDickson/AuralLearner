package cosc345.app.model;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;

import cosc345.app.lib.Callback;
import cosc345.app.lib.State;

/**
 * Encapsulate and manage text-to-speech.
 */
public class TextToSpeechManager {
    private static final String LOG_TAG = "TextToSpeech";
    private static TextToSpeechManager instance;

    private TextToSpeech tts;
    private final HashMap<String, String> ttsParams = new HashMap<>();
    private WeakReference<Context> parentContext;
    private Callback onStart;
    private Callback onDone;
    private State state = State.NOT_READY;

    private TextToSpeechManager() {
    }

    /**
     * Get the instance of the text-to-speech manager.
     *
     * @return the instance of the text-to-speech manager.
     */
    public static TextToSpeechManager getInstance() {
        if (TextToSpeechManager.instance == null) {
            TextToSpeechManager.instance = new TextToSpeechManager();
        }

        return TextToSpeechManager.instance;
    }

    /**
     * Set up the text-to-speech service.
     *
     * @param parentContext the parent context (Activity).
     * @param onStart       the callback to called once TTS has begun.
     * @param onDone        the callback to called once TTS has finished.
     */
    public void init(Context parentContext, Callback onStart, Callback onDone) {
        this.parentContext = new WeakReference<>(parentContext);
        this.onStart = onStart;
        this.onDone = onDone;

        init();
    }

    private void init() {
        state = State.INITIALISING;
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "id");
        tts = new TextToSpeech(parentContext.get(), status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.UK);
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.i(TextToSpeechManager.LOG_TAG, "Utterance started");
                        state = State.BUSY;

                        if (onStart != null) {
                            onStart.execute();
                        }
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.i(TextToSpeechManager.LOG_TAG, "Utterance finished");
                        state = State.READY;

                        if (onDone != null) {
                            onDone.execute();
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.e(TextToSpeechManager.LOG_TAG, String.format("An error occurred when speaking utterance %s.", utteranceId));
                    }
                });

                state = State.READY;
                Log.i(TextToSpeechManager.LOG_TAG, "Initialisation Complete.");
            }
        });
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
     * Restarts the text-to-speech service.
     */
    public void restart() {
        if (tts == null) {
            return;
        }

        if (state == State.NOT_READY || state == State.SHUTDOWN) {
            Log.i(TextToSpeechManager.LOG_TAG, "Restarted.");
            init();
        }
    }

    /**
     * Completely stop and close the text-to-speech service.
     */
    public void close() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            state = State.SHUTDOWN;

            Log.i(TextToSpeechManager.LOG_TAG, "Shutdown.");
        }
    }

    /**
     * Stop the current utterance (if there is one).
     */
    public void pause() {
        if (tts != null) {
            tts.stop();
            state = State.READY;

            Log.i(TextToSpeechManager.LOG_TAG, "Paused.");
        }
    }
}
