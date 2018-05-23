//https://introcs.cs.princeton.edu/java/21function/Tone.java
public class Tone {

  // create a pure tone of the given frequency for the given duration
    public static double[] tone(double hz, double duration) { 
        int n = (int) (StdAudio.SAMPLE_RATE * duration);
        double[] a = new double[n+1];
        for (int i = 0; i <= n; i++) {
            a[i] = Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
        }
        return a; 
    } 


    public static void main(String[] args) {

        // frequency
        double hz = 125;

        // number of seconds to play the note
        double duration = 1;

        // create the array
        double[] a = tone(hz, duration);

        // play it using standard audio
        StdAudio.play(a);
    }
}