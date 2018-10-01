package cosc345.AuralLearner.model;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;

/**
 * Listens to microphone input and detects the pitch of the audio.
 */
public class PitchDetector {
    public static final int DEFAULT_SAMPLE_RATE = 22050;
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final int DEFAULT_BUFFER_OVERLAP = 0;

    private AudioDispatcher dispatcher;
    private final int sampleRate;
    private final int bufferSize;
    private final int bufferOverlap;
    private final PitchDetectionHandler handler;
    private Thread audioThread;

    /**
     * Convenience constructor.
     *
     * @param handler An object that implements PitchDetectionHandler.
     */
    public PitchDetector(PitchDetectionHandler handler) {
        this(DEFAULT_SAMPLE_RATE, DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_OVERLAP, handler);
    }

    /**
     * @param sampleRate    The requested sample rate must be supported by the capture device. Nonstandard sample rates can be problematic!
     * @param bufferSize    The size of the buffer defines how much samples are processed in one step. Common values are 1024,2048.
     * @param bufferOverlap How much consecutive buffers overlap (in samples). Half of the AudioBufferSize is common.
     * @param handler       The object that will h
     * Information on the parameters retrieved from: https://0110.be/releases/TarsosDSP/TarsosDSP-latest/TarsosDSP-latest-Documentation/be/tarsos/dsp/io/jvm/AudioDispatcherFactory.html#fromDefaultMicrophone-int-int-int-
     */
    public PitchDetector(int sampleRate, int bufferSize, int bufferOverlap, PitchDetectionHandler handler) {
        this.sampleRate = sampleRate;
        this.bufferSize = bufferSize;
        this.bufferOverlap = bufferOverlap;
        this.handler = handler;
        this.dispatcher = getAudioDispatcher();
    }

    private AudioDispatcher getAudioDispatcher() {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, bufferOverlap);
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, sampleRate, bufferSize, handler);
        dispatcher.addAudioProcessor(pitchProcessor);

        return dispatcher;
    }

    /**
     * Start the pitch detection thread and start processing audio input.
     */
    public void start() {
        if (dispatcher.isStopped()) {
            dispatcher = getAudioDispatcher();
        }

        audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }

    /**
     * Stop the pitch detection thread and stop processing audio input.
     */
    public void stop() {
        dispatcher.stop();

        if (audioThread != null) {
            audioThread.interrupt();
            audioThread = null;
        }
    }
}
