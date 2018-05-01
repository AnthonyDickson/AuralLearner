package com.dican732.cosc345app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class VoiceRecognition implements RecognitionListener {
    public static final String LOG_TAG = "Voice Recognition";
    /* We only need the keyphrase to start recognition, one menu with list of choices,
       and one word that is required for method switchSearch - it will bring recogniser
       back to listening for the keyphrase*/
    private static final String KWS_SEARCH = "wakeup";
    private static final String MENU_SEARCH = "menu";
    /* Keyword we are looking for to activate recognition */
    private static final String KEYPHRASE = "open menu";
    private SpeechRecognizer recogniser;
    private Context parentContext;

    public VoiceRecognition(Context parentContext) {
        this.parentContext = parentContext;
        // recogniser initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new VoiceRecognitionInitialiser(this).execute();
    }

    /**
     * Close the voice recogniser.
     * This method should be called when the parent context (activity) stops.
     */
    public void close() {
        if (recogniser != null) {
            recogniser.cancel();
            recogniser.shutdown();

            Log.i(LOG_TAG, "Shutdown.");
        }
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recogniser = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                // Disable this line if you don't want recogniser to save raw
                // audio files to app's storage
                //.setRawLogDir(assetsDir)
                .getRecognizer();
        recogniser.addListener(this);
        // Create keyword-activation search.
        recogniser.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        // Create your custom grammar-based search
//        File menuGrammar = new File(assetsDir, "mymenu.gram");
//        recogniser.addGrammarSearch(MENU_SEARCH, menuGrammar);
        File menuGrammar = new File(assetsDir, "menu.gram");
        recogniser.addKeywordSearch(MENU_SEARCH, menuGrammar);
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        handleHypothesis(hypothesis);
    }

    @Override
    public void onTimeout() {
        Log.i(LOG_TAG, "Timed out.");
        switchSearch(KWS_SEARCH);
    }

    @Override
    public void onEndOfSpeech() {
        if (!recogniser.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);

        Log.i(LOG_TAG, "Finished Listening");
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }

        handleHypothesis(hypothesis);
    }

    private void handleHypothesis(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();

        switch (text) {
            case KEYPHRASE:
                switchSearch(MENU_SEARCH);
                break;

            default:
                // Do nothing.
        }

        Log.i(LOG_TAG, "Current hypothesis string - %s".format(text));
    }

    private void switchSearch(String searchName) {
        recogniser.stop();

        if (searchName.equals(KWS_SEARCH)) {
            recogniser.startListening(searchName);
        } else {
            if (searchName.equals(MENU_SEARCH)) {
                Log.i(LOG_TAG, "Started listening.");
            }

            recogniser.startListening(searchName, 10000);
        }
    }

    @Override
    public void onError(Exception error) {
        Log.e(LOG_TAG, error.getMessage());
    }

    private static class VoiceRecognitionInitialiser extends AsyncTask<Void, Void, Exception> {
        private WeakReference<VoiceRecognition> activityReference;

        // only retain a weak reference to the activity
        VoiceRecognitionInitialiser(VoiceRecognition context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                VoiceRecognition context = activityReference.get();

                Assets assets = new Assets(context.parentContext);
                File assetDir = assets.syncAssets();
                context.setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                System.out.println(result.getMessage());
            } else {
                VoiceRecognition context = activityReference.get();
                context.switchSearch(KWS_SEARCH);
                Log.i(LOG_TAG, "Initialisation Complete.");
            }
        }
    }
}
