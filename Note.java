//will include hertz frequincy to pitch conversion
//will trigger a sound
//array for  pitches and another array which is
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Note{
  
  FFT fft = new FFT();
  
  protected double offsetFromPitch = 0.0;
  protected double pitchMatched = 0.0;
  // array of freq values starting at F2 to F#3(14 items
  public final double[] pitches = {87.31, 92.50, 98.00, 103.83, 110.00, 116.54, 123.47, 130.81,
    138.59, 146.83, 155.56, 164.81, 174.61, 185.00};
  // corrospongs to first array
  public final String[] pitchesSharp = {"F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D",
    "D#", "E", "F", "F#"};
  public final String[] pitchesFlat = {"F", "Gb", "G", "Ab", "A", "Bb", "B", "C", "Db", "D",
    "Eb", "E", "F", "Gb"};
  /* basically what they sing */
  public double hertzAVG = 0;
  /* used to store the difference between the pitch and the one one above it */
  public double difference = 0;
  /* the note for the object */
  public String note;
  /* position of note inside pitches array */
  public int noteIndex;
  
  public Random random = new Random();
  /*
   * Note is represented by a char
   */
  public Note(String note){
    this.note = note;
    for (int i = 0; i < pitches.length-1; i++){
      if (this.note.equals(pitchesSharp[i]) || this.note.equals(pitchesFlat[i])){
        this.noteIndex = i;
      }
      
    }
  };
  /*
   * this one should randomly create a note
   * pick and letter, an accidental weighted slightley less for an accidental
   */
  public Note(){
    this.noteIndex = random.nextInt(13);
    if (random.nextInt(2) ==1){
      this.note = pitchesSharp[this.noteIndex];
    }else
      this.note = pitchesFlat[this.noteIndex];    
  };
  /* 
   * constructor which takes in a note and an interval
   * to make a note which is an interval of another
   */
  public Note(Note noteInput, int interval){
    lowerNote = noteInput.note.charAt(0);
    this.noteIndex = (noteInput.noteIndex +(interval-1))%pitches.length-1);
    //checks for accidental(sharps or flats
    if (noteInput.note.length() > 1){
      if (noteInput.note.charAt(1) == '#'){
        this.note = pitchesSharp[this.noteIndex]; //i.e a 5th is actually +4 not 5
      }else{
        this.note = pitchesFlat[this.noteIndex]; //i.e a 5th is actually +4 not 5
      }
    }else{
      if (random.nextInt(2) >0){
        this.note = pitchesSharp[this.noteIndex];
      }else{
        this.note = pitchesFlat[this.noteIndex];
      }                                 
      
      
    }
    
}
    
    
    /*
     * Later will take a note length as a value
     * for now it just messure for a time of
     * one crotchet at 120bpm or 500 ms
     * https://msu.edu/course/asc/232/song_project/dectalk_pages/note_to_%20ms.html
     */
    
    public void detectNote(){
      
      long noteStart = System.currentTimeMillis(); //fetch starting time
      float[] hertzReadings = new float[50];
      int count = 0;
      
      
      
      // is just for checking
      
      // may have to alter this part in practice if FFT is not fast enough
      while(false||(System.currentTimeMillis()-noteStart)<500){
        Timeunit.MILLISECONDS.sleep(10);
        //this fft call should return a hz reading and do microphone
        hertzReadings[count] = fft.hertz(); //50 readings
        
        count++;
      }
      
      //this calculates average hz but also checks values that are too random
      // maybe discount large outliers by  rolling average if this doesnt work
      
      for(int i = 0; i < hertzReadings.length; i++){
        hertzAVG += hertzReadings[i];
      }
      hertzAVG /= hertzReadings.length; // this pitch will be played back by an oscilator as well as for checking
      
      
      // might make sense to only calculate acouple octaves, average male and female range
      // F2 to F4 See pitch chart image
      // when calculating offset, you will have to keep in mind that octaves are two to one octaves
      // calculate the difference between the two different pitchs check for each octave and see whether 
      // it falls within that range
      
      //it does not check last array index, that value only exists ofr the calc
      for (int i = 0; i <pitchs.length-1; i++){
        //does this part twice to check for the pitch at each octave
        difference = pitch[i+1] -pitch[i];
        if(hertzAVG > pitch[i] and hertzAVG < pitch[i] + (difference)/2){
          offsetFromPitch = hertzAVG -pitch[i];
          difference = (pitch[i+1] -pitch[i]);
          pitchMatched = pitch[i];
          break;
        }else if(hertzAVG > pitch[i]*2 and hertzAVG < pitch[i]*2 + (differce*2)/2){
          offsetFromPitch = hertzAVG -pitch[i]*2;
          difference = difference*2;
          pitchMatched = pitch[i]*2;
          break;
        }
      }
    }
    /* it makes more sense for the interval one to be able to match the pitchs and then
     * use this inorder give feedback after interval or scales exercise
     * allows greater flexibility
     */
    public boolean isMatched(){
      if (offsetFromPitch > difference * 0.2){
        return false;
      } else {
        return true;
      }
    }
    /* Computer acurate pitch */
    public void playPerfect(){
      Tone.play(pitchesSharp[noteIndex]);
    }
    /* User pitch */
    public void playUser(){
      Tone.play(hertzAVG);
    }
}
