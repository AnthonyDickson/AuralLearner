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

        Intervals[] exerciseConstraints;
        double invertProbability;
        int melodyRange;

        if (difficulty == Difficulty.EASY){

            exerciseConstraints = exerciseConstraintsEasy;
            melodyRange = 5;
            invertProbability = 0.1;


        } else if(difficulty == Difficulty.MEDIUM){

            exerciseConstraints = exerciseConstraintsMedium;
            invertProbability = 0.25;
            melodyRange = 7;


        } else{

            exerciseConstraints = exerciseConstraintsHard;
            invertProbability = 0.5;
            melodyRange = 9;

        }
        ArrayList<Note> exercise = new ArrayList<>();
        Boolean invert;
        Intervals intervalPick = exerciseConstraints[random.nextInt(exerciseConstraints.length)];
        if (random.nextDouble() < invertProbability){
            invert = true;
        } else {
            invert = false;
        }
        this.interval = new Interval(Note.getRandom(), intervalPick, invert); //need to add notes
        exercise.add(this.interval.root);
        exercise.add(this.interval.other);
        return exercise;

    }
}
