package cosc345.app.lib;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Encapsulate and manage text-to-speech.
 *
 * @author Anthony Dickson
 */
public class TextToSpeechManager {
    public static final String LOG_TAG = "TextToSpeech";

    Context parentContext;
    TextToSpeech tts;

    public TextToSpeechManager(Context parentContext) {
        this.parentContext = parentContext;

        tts = new TextToSpeech(parentContext, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.UK);
                Log.i(LOG_TAG, "TTS Initialised");
            }
        });
    }

    /**
     * Make the device read out the given text.
     * @param text The text to read out.
     */
    public void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void close(){
        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
    }
}
