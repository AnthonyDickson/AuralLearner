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

        Intervals[] exerciseConstraints;
        double invertProbability;
        int melodyRange;

        Intervals[] exerciseConstraintsEasy = {Intervals.P1,Intervals.P4, Intervals.P5, Intervals.P8};

        Intervals[] exerciseConstraintsMedium = {Intervals.P1,Intervals.P4, Intervals.P5, Intervals.P8,
                Intervals.M2, Intervals.m3, Intervals.m6, Intervals.M6};
        Intervals[] exerciseConstraintsHard = {Intervals.P1,Intervals.P4, Intervals.P5, Intervals.P8,
                Intervals.M2, Intervals.m3, Intervals.m6, Intervals.M6, Intervals.m2, Intervals.A4,
                Intervals.m7, Intervals.M7};

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
            melodyRange =

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
