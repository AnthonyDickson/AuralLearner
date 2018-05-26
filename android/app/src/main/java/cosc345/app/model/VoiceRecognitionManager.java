package cosc345.app.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cosc345.app.lib.MenuAction;
import cosc345.app.lib.State;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * Manages voice recognition and voice control.
 */
public class VoiceRecognitionManager implements RecognitionListener {
    private static final String LOG_TAG = "VoiceRecognition";
    /** This string used to tell the voice recognition to search for the keyphrase. */
    private static final String KWS_SEARCH = "wakeup";
    /** This string used to tell the voice recognition to do the grammar search. */
    private static final String MENU_SEARCH = "menu";
    /** Keyword we are looking for to activate recognition */
    private static final String KEYPHRASE = "menu";
    private static VoiceRecognitionManager instance;
    private State state = State.NOT_READY;
    private SpeechRecognizer recogniser;
    private WeakReference<Context> parentContext;
    private final ArrayList<MenuAction> actions = new ArrayList<>();

    private VoiceRecognitionManager() {}

    /**
     * Get the instance of the voice recognition manager.
     * @return the instance of the voice recognition manager.
     */
    public static VoiceRecognitionManager getInstance() {
        if (VoiceRecognitionManager.instance == null) {
            VoiceRecognitionManager.instance = new VoiceRecognitionManager();
        }

        return VoiceRecognitionManager.instance;
    }

    /**
     * Initialise the voice recogniser.
     * @param parentContext the parent activity.
     */
    public void init(Context parentContext) {
        this.parentContext = new WeakReference<>(parentContext);
        // recogniser initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new VoiceRecognitionInitialiser(this).execute();
    }

    /**
     * Sets up the voice recogniser.
     * @param assetsDir the directory where the voice recognition files are stored.
     * @throws IOException if the voice recognition files are unable to be loaded.
     */
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
        recogniser.addKeyphraseSearch(VoiceRecognitionManager.KWS_SEARCH, VoiceRecognitionManager.KEYPHRASE);
        // Create your custom grammar-based search
        File menuGrammar = new File(assetsDir, "mymenu.gram");
        recogniser.addGrammarSearch(VoiceRecognitionManager.MENU_SEARCH, menuGrammar);
//        File menuGrammar = new File(assetsDir, "menu.gram");
//        recogniser.addKeywordSearch(MENU_SEARCH, menuGrammar);
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }

        if (hypothesis.getHypstr().equals(VoiceRecognitionManager.KEYPHRASE)) {
            switchSearch(VoiceRecognitionManager.KWS_SEARCH);
        }
    }

    @Override
    public void onTimeout() {
        Log.i(VoiceRecognitionManager.LOG_TAG, "Timed out.");
        switchSearch(VoiceRecognitionManager.KWS_SEARCH);
    }

    @Override
    public void onEndOfSpeech() {
        if (!recogniser.getSearchName().equals(VoiceRecognitionManager.KWS_SEARCH)) {
            switchSearch(VoiceRecognitionManager.KWS_SEARCH);
        }

        state = State.READY;
        Log.i(VoiceRecognitionManager.LOG_TAG, "Finished Listening");
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }

        Log.i(VoiceRecognitionManager.LOG_TAG, String.format("Full result: %s", hypothesis.getHypstr()));
        handleHypothesis(hypothesis);
    }

    /**
     * Handles the hypothesis from the voice recogniser.
     * @param hypothesis the prediction of the input speech.
     */
    private void handleHypothesis(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();
        Log.i(VoiceRecognitionManager.LOG_TAG, String.format("Current hypothesis string: %s", text));
        Log.i(VoiceRecognitionManager.LOG_TAG, String.format("Hypothesis probability: %d", hypothesis.getProb()));
        Log.i(VoiceRecognitionManager.LOG_TAG, String.format("Hypothesis best score: %d", hypothesis.getBestScore()));

        if (text.equals(VoiceRecognitionManager.KEYPHRASE)) {
            switchSearch(VoiceRecognitionManager.MENU_SEARCH);
        } else {
            for (MenuAction a : actions) {
                if (text.equals(a.activation)) {
                    a.execute();
                }
            }
        }
    }

    /**
     * Change the voice recogniser's search mode.
     * @param searchName the name of the search mode to change to.
     */
    private void switchSearch(String searchName) {
        recogniser.stop();

        if (searchName.equals(VoiceRecognitionManager.KWS_SEARCH)) {
            recogniser.startListening(VoiceRecognitionManager.KWS_SEARCH);
            state = State.READY;
        } else {
            if (searchName.equals(VoiceRecognitionManager.MENU_SEARCH)) {
                Log.i(VoiceRecognitionManager.LOG_TAG, "Started listening.");
                Toast.makeText(parentContext.get(), "Im listening...", Toast.LENGTH_SHORT).show();
                state = State.BUSY;
            }

            recogniser.startListening(searchName, 10000);
        }
    }

    @Override
    public void onError(Exception error) {
        Log.e(VoiceRecognitionManager.LOG_TAG, error.getMessage());
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
            state = State.PAUSED;

            Log.i(VoiceRecognitionManager.LOG_TAG, "Paused.");
        }
    }

    /**
     * Resume voice recognition after it has been paused.
     */
    public void resume() {
        if (recogniser != null && state == State.PAUSED) {
            recogniser.startListening(VoiceRecognitionManager.KWS_SEARCH);
            state = State.READY;

            Log.i(VoiceRecognitionManager.LOG_TAG, "Resumed.");
        }
    }

    /**
     * Restart the voice recognition service.
     */
    public void restart() {
        if (recogniser == null) {
            return;
        }

        if (state == State.NOT_READY || state == State.SHUTDOWN) {
            Log.i(VoiceRecognitionManager.LOG_TAG, "Restarted.");
            new VoiceRecognitionInitialiser(this).execute();
        }
    }

    /**
     * Close the voice recogniser service.
     */
    public void close() {
        if (recogniser != null && state != State.SHUTDOWN) {
            recogniser.cancel();
            recogniser.shutdown();
            state = State.SHUTDOWN;

            Log.i(VoiceRecognitionManager.LOG_TAG, "Shutdown.");
        }
    }

    /**
     * Initialises the voice recogniser asynchronously.
     */
    private static class VoiceRecognitionInitialiser extends AsyncTask<Void, Void, Exception> {
        private final WeakReference<VoiceRecognitionManager> activityReference;

        // only retain a weak reference to the activity
        VoiceRecognitionInitialiser(VoiceRecognitionManager context) {
            context.state = State.INITIALISING;
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
                context.switchSearch(VoiceRecognitionManager.KWS_SEARCH);
                context.state = State.READY;
                Log.i(VoiceRecognitionManager.LOG_TAG, "Initialisation Complete.");
            }
        }
    }
}
