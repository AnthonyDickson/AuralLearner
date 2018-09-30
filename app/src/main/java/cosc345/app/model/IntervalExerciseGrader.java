package cosc345.app.model;

import java.util.ArrayList;

import java.util.Random;

public class IntervalExerciseGrader extends Grader {
    static private final int C3_INDEX = Note.C4_INDEX - 12;

    static private Intervals[] exerciseConstraintsEasy = {Intervals.P1,Intervals.P4, Intervals.P5, Intervals.P8};

    static private Intervals[] exerciseConstraintsMedium = {Intervals.P1,Intervals.P4, Intervals.P5, Intervals.P8,
            Intervals.M2, Intervals.m3, Intervals.m6, Intervals.M6};
    static private Intervals[] exerciseConstraintsHard = {Intervals.P1,Intervals.P4, Intervals.P5, Intervals.P8,
            Intervals.M2, Intervals.m3, Intervals.m6, Intervals.M6, Intervals.m2, Intervals.A4,
            Intervals.m7, Intervals.M7};

    private Random random = new Random();

    public IntervalExerciseGrader(Difficulty difficulty){
        super();

        super.notes = pickInterval(difficulty);
    }

    public ArrayList<Note> pickInterval(Difficulty difficulty){

        Intervals[] exerciseConstraints;
        double invertProbability;

        Note startingNote = Note.getRandom(C3_INDEX, 4.0, Note.NoteLength.MINIM);

        if (difficulty == Difficulty.EASY){

            exerciseConstraints = exerciseConstraintsEasy;
            invertProbability = 0.1;


        } else if(difficulty == Difficulty.MEDIUM){

            exerciseConstraints = exerciseConstraintsMedium;
            invertProbability = 0.25;


        } else{

            exerciseConstraints = exerciseConstraintsHard;
            invertProbability = 0.5;

        }

        ArrayList<Note> exercise = new ArrayList<>();
        Boolean invert;
        Intervals intervalPick = exerciseConstraints[random.nextInt(exerciseConstraints.length)];

        invert = random.nextDouble() < invertProbability;

        Interval interval = new Interval(startingNote, intervalPick, invert); //need to add notes
        exercise.add(interval.root);
        exercise.add(interval.other);

        this.playable = interval;

        return exercise;
    }
}
