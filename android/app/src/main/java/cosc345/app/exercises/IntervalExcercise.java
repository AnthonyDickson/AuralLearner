//package cosc345.app.exercises;/*
// * This is not necessarily going to be functioning yet, just a mock up
// * Unsure whether to have a dedicated rhythm class
// * This definitley will require a pitch detector class
// * An interval class will be needed
// *@auther Johnny
// */
//import java.util.concurrent.TimeUnit;
//import java.util.Random;
//
//import cosc345.app.lib.Note;
//import cosc345.app.lib.Tone;
//
//public class IntervalExcercise{
//  //TimeUnit.MINUTES.sleep(1);
//  //TimeUnit.SECONDS.sleep(1);
//  //long startTime = System.currentTimeMillis(); //fetch starting time
//  //while(false||(System.currentTimeMillis()-startTime)<10000)
//
//  static Random random = new Random();
//   // maybe make this part of cosc345.app.lib.Note
//
//  /* easy, you can sing 1st 4th or a 5th(no inversions
//   * For now this will play the pitch, then check if you are matching
//   * Needs an oscilator object
//   */
//  public static void runExerciseLevel_1(){
//    int[] intervalSelection = {4,5};
//    Note note1 = new Note(); //random
//    Note note2 = new Note(note1, intervalSelection[random.nextInt(2)]);
//    //play the notes
//    Tone.play(note1);
//    TimeUnit.MILLISECONDS.sleep(500);
//    Tone.play(note2);
//    TimeUnit.MILLISECONDS.sleep(500);
//    //part for user to do
//    //these should happen at same time
//    note1.playPerfect();
//    note1.detectNote();
//    TimeUnit.MILLISECONDS.sleep(500);
//
//    note1.playPerfect();
//    note2.detectNote();
//    TimeUnit.MILLISECONDS.sleep(500);
//    //feedback
//    note1.playPerfect();
//    note1.playUser();
//    if (note1.isMatched){
//      Tone.play(90);
//      Tone.play(180);
//    }else{
//      Tone.play(90);
//      Tone.play(100);
//    }
//
//
//    note2.playPerfect();
//    note2.playUser();
//    if (note2.isMatched){
//      Tone.play(90);
//      Tone.play(180);
//    }else{
//      Tone.play(90);
//      Tone.play(100);
//    }
//
//
//
//    //wait 500ms
//
//    //play tone but also sing against it, might need a thread
//
//  }
//
//
//}
//
//
//
//
//
