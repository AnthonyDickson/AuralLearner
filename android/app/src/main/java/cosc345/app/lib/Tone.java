package cosc345.app.lib;

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

    public static void play(Note note) {
        play(note.hertzAVG);
    }

    public static void play(double hz){
        // number of seconds to play the note// in this case a crotchet or 1/4 note
        double duration = 0.5;

        // create the array
        double[] a = tone(hz, duration);

        // play it using standard audio TODO: Find something else to play tones.
        StdAudio.play(a);
    }
}
