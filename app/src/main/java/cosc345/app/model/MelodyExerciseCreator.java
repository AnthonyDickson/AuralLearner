package cosc345.app.model;

import java.util.ArrayList;

import cosc345.app.model.Note;
import cosc345.app.model.Interval;
import cosc345.app.model.Difficulty;
import java.util.Random;

public class MelodyExerciseCreator extends Grader{

    private Random random = new Random();
    public ArrayList<Note> melody = new ArrayList<>();

    public MelodyExerciseCreator(Difficulty difficulty){
        super.notes = pickMelody(difficulty);
    }
    /* Similiar to IntervalExerciseCreator for melodies */
    public ArrayList<Note> pickMelody(Difficulty difficulty){

        //will need to build intervals
        //first will be generated like previous
        //next will specify a starting note and an interval
        //
        //choose a random note to start
        //generate a major scale
        //treat array like a scale and manipulate it like scale
        //degrees 1-8, just take of -1 for each array access
        //
        //melody should start and end on root, but hard may start on another scale degree
        //there should be a melody threshold which it cannot go over


        /* the probability that when picking a note, decides whether the next note
         * will be higher or lower
         */
        double invertProbability;
        /* the notes used for the exercise, should be in a key */
        Note[] scale;
        Note startingNote =  Note.getRandom();

        int melodyRange;
        int exerciseLength;
        int[] scaleSteps;
        int[] scaleStepsEasy = {2};
        int[] scaleStepsMedium = {2, 2, 2, 3};
        int[] scaleStepsHard = {2, 2, 2, 2, 2, 3, 3, 4};

        if (difficulty == Difficulty.EASY){

            melodyRange = 3;
            invertProbability = 0;
            scaleSteps = scaleStepsEasy;
            exerciseLength = 4;


        } else if(difficulty == Difficulty.MEDIUM){

            melodyRange = 6;
            invertProbability = 0.2;
            scaleSteps = scaleStepsMedium;
            exerciseLength = 8;


        } else{

            melodyRange = 8;
            invertProbability = 0.3;
            scaleSteps = scaleStepsHard;
            exerciseLength = 8;


        }
        //insures the range
        while (startingNote.getName().charAt(-1) != (3 | 4)){
            startingNote = Note.getRandom();
        }
        ArrayList<Note> exercise = new ArrayList<>();
        boolean pastHalfway = false; //only change if middle note has been reached

        return exercise;

    }
    public Note[] scaleGenerator(Note startingNote){
        //use note from string constructor
        //find that note, then look for the higher one which should be an arrayindex
        //from the first one
        //needs to use note class to generate scale

        int[] majorScaleSemitoneLeaps = { 2, 2, 1, 2, 2, 1};// steps to take when adding next note

        Note[] scale = new Note[7];
        String startNoteName = startingNote.getName();
        int notePosition = startingNote.getNameIndex();

        // 'b' denotes flat
        if (startNoteName.charAt(1) == 'b'){


        } else {

        }

        return scale;
    }
}
