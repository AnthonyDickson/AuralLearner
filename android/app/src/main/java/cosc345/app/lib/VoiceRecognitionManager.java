package cosc345.app.lib;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * Manage voice recognition and voice control.
 *
 * @author Anthony Dickson
 */
public class VoiceRecognitionManager implements RecognitionListener {
    private static final String LOG_TAG = "VoiceRecognition";
    /* We only need the keyphrase to start recognition, one menu with list of choices,
       and one word that is required for method switchSearch - it will bring recogniser
       back to listening for the keyphrase*/
    private static final String KWS_SEARCH = "wakeup";
    private static final String MENU_SEARCH = "menu";
    /* Keyword we are looking for to activate recognition */
    private static final String KEYPHRASE = "menu";
    private static volatile VoiceRecognitionManager instance;
    private STATE state = STATE.NOT_READY;
    private SpeechRecognizer recogniser;
    private WeakReference<Context> parentContext;
    private ArrayList<MenuAction> actions = new ArrayList<>();

    private VoiceRecognitionManager(Context parentContext) {
        this.parentContext = new WeakReference<>(parentContext);
        // recogniser initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new VoiceRecognitionInitialiser(this).execute();
    }

    public static VoiceRecognitionManager getInstance() {
        return instance;
    }

    public static synchronized VoiceRecognitionManager getInstance(Context parentContext) {
        if (instance == null) {
            instance = new VoiceRecognitionManager(parentContext);
        }

        return instance;
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
        File menuGrammar = new File(assetsDir, "mymenu.gram");
        recogniser.addGrammarSearch(MENU_SEARCH, menuGrammar);
//        File menuGrammar = new File(assetsDir, "menu.gram");
//        recogniser.addKeywordSearch(MENU_SEARCH, menuGrammar);
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        if (hypothesis.getHypstr().equals(KEYPHRASE)) {
            switchSearch(KWS_SEARCH);
        }
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

        state = STATE.READY;
        Log.i(LOG_TAG, "Finished Listening");
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }

        Log.i(LOG_TAG, String.format("Full result: %s", hypothesis.getHypstr()));
        handleHypothesis(hypothesis);
    }

    private void handleHypothesis(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();
        Log.i(LOG_TAG, String.format("Current hypothesis string: %s", text));
        Log.i(LOG_TAG, String.format("Hypothesis probability: %d", hypothesis.getProb()));
        Log.i(LOG_TAG, String.format("Hypothesis best score: %d", hypothesis.getBestScore()));

        if (text.equals(KEYPHRASE)) {
            switchSearch(MENU_SEARCH);
        } else {
            for (MenuAction a : actions) {
                if (text.equals(a.activation)) {
                    a.execute();
                }
            }
        }
    }

    private void switchSearch(String searchName) {
        recogniser.stop();

        if (searchName.equals(KWS_SEARCH)) {
            recogniser.startListening(KWS_SEARCH);
            state = STATE.READY;
        } else {
            if (searchName.equals(MENU_SEARCH)) {
                Log.i(LOG_TAG, "Started listening.");
                Toast.makeText(parentContext.get(), "Im listening...", Toast.LENGTH_SHORT).show();
                state = STATE.LISTENING;
            }

            recogniser.startListening(searchName, 10000);
        }
    }

    @Override
    public void onError(Exception error) {
        Log.e(LOG_TAG, error.getMessage());
    }

    /**
     * Register an action to be made available in the voice recognition menu.
     * Note: Make sure the string used for the action's <code>activation</code>
     * is in the mymenu.gram file inside the assets folder, otherwise it will
     * not work.
     *
     * @param action The menu action to register.
     */
    public void registerAction(MenuAction action) {
        actions.add(action);
    }

    /**
     * Cancel any voice recognition activity and pause the voice recognition service.
     */
    public void pause() {
        if (recogniser != null) {
            recogniser.cancel();
            state = STATE.PAUSED;

            Log.i(LOG_TAG, "Paused.");
        }
    }

    /**
     * Restart the voice recognition service.
     */
    public void resume() {
        if (recogniser == null) {
            return;
        }

        if (state == STATE.PAUSED) {
            recogniser.startListening(KWS_SEARCH);
            state = STATE.READY;
            Log.i(LOG_TAG, "Resumed.");
        } else if (state == STATE.NOT_READY || state == STATE.SHUTDOWN) {
            new VoiceRecognitionInitialiser(this).execute();
        }
    }

    /**
     * Close the voice recogniser service.
     */
    public void close() {
        if (recogniser != null) {
            recogniser.cancel();
            recogniser.shutdown();
            state = STATE.SHUTDOWN;

            Log.i(LOG_TAG, "Shutdown.");
        }
    }


    public enum STATE {NOT_READY, INITIALISING, READY, LISTENING, PAUSED, SHUTDOWN}

    private static class VoiceRecognitionInitialiser extends AsyncTask<Void, Void, Exception> {
        private WeakReference<VoiceRecognitionManager> activityReference;

        // only retain a weak reference to the activity
        VoiceRecognitionInitialiser(VoiceRecognitionManager context) {
            context.state = STATE.INITIALISING;
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            try {
                VoiceRecognitionManager context = activityReference.get();

                Assets assets = new Assets(context.parentContext.get());
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
                VoiceRecognitionManager context = activityReference.get();
                context.switchSearch(KWS_SEARCH);
                context.state = STATE.READY;
                Log.i(LOG_TAG, "Initialisation Complete.");
            }
        }
    }
}
