package cosc345.app.model;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;
import cosc345.app.model.State;

/**
 * Listens to microphone input and detects the pitch of the audio.
 */
public class PitchDetector {
    private static final int DEFAULT_SAMPLE_RATE = 22050;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final int DEFAULT_BUFFER_OVERLAP = 0;

    private final AudioDispatcher dispatcher;
    private Thread audioThread;
    private State state;

    /**
     * Convenience constructor.
     * @param handler An object that implements PitchDetectionHandler.
     */
    public PitchDetector(PitchDetectionHandler handler) {
        this(DEFAULT_SAMPLE_RATE, DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_OVERLAP, handler);
    }

    /**
     * See the following page for information on the arguments: https://0110.be/releases/TarsosDSP/TarsosDSP-latest/TarsosDSP-latest-Documentation/be/tarsos/dsp/io/jvm/AudioDispatcherFactory.html#fromDefaultMicrophone-int-int-int-
     */
    public PitchDetector(int sampleRate, int bufferSize, int bufferOverlap, PitchDetectionHandler handler) {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate,bufferSize,bufferOverlap);
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, sampleRate, bufferSize, handler);
        dispatcher.addAudioProcessor(pitchProcessor);
        state = State.READY;
    }

    /**
     * Start the pitch detection thread and start processing audio input.
     */
    public void start() {
        if (state != State.READY) return;

        audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
        state = State.BUSY;
    }

    /**
     * Stop the pitch detection thread and stop processing audio input.
     */
    public void stop() {
        if (state != State.BUSY) return;

        dispatcher.stop();
        audioThread.interrupt();
        audioThread = null;
        state = State.READY;
    }
}