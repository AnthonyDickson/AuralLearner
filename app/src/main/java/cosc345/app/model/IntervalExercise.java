package cosc345.app.model;

import java.util.ArrayList;

import cosc345.app.model.Note;
import cosc345.app.model.Interval;
import cosc345.app.model.Difficulty;
import java.util.Random;

public class IntervalExercise extends Grader{

    private Random random = new Random();
    public Interval interval;

    public IntervalExercise(Difficulty difficulty){
        ArrayList<Note> notes = new ArrayList<>();
        if (difficulty == Difficulty.EASY){
            notes = pickIntervalEasy();
        } else if(difficulty == Difficulty.MEDIUM){
            notes = pickIntervalMedium();
        } else if(difficulty == Difficulty.HARD){
            notes = pickIntervalHard();
        }

        super.notes = notes;
    }
    //Perfect Unison Major 3rd
    public ArrayList<Note> pickIntervalEasy(){
        // so here you would need to decide the bass note
        ArrayList<Note> exercise = new ArrayList<>();
        Intervals[] exerciseConstraints = {Intervals.P1,Intervals.P4, Intervals.P5, Intervals.P8};
        double invertProbability = 0.1;
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
    public ArrayList<Note> pickIntervalMedium(){
        ArrayList<Note> exercise = new ArrayList<>();
        Intervals[] exerciseConstraints = {Intervals.P1,Intervals.P4, Intervals.P5, Intervals.P8,
        Intervals.M2, Intervals.m3, Intervals.m6, Intervals.M6};
        double invertProbability = 0.25;
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
    public ArrayList<Note> pickIntervalHard(){
        ArrayList<Note> exercise = new ArrayList<>();
        Intervals[] exerciseConstraints = {Intervals.P1,Intervals.P4, Intervals.P5, Intervals.P8,
                Intervals.M2, Intervals.m3, Intervals.m6, Intervals.M6, Intervals.m2, Intervals.A4,
        Intervals.m7, Intervals.M7};
        double invertProbability = 0.5;
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
