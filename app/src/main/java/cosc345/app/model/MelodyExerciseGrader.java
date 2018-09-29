package cosc345.app.model;

import java.util.ArrayList;

import java.util.Random;

public class MelodyExerciseGrader extends Grader {
    private Random random = new Random();
    public Melody melody;

    public MelodyExerciseGrader(Difficulty difficulty){
        super();

        super.notes = pickMelody(difficulty);
    }
    /* Similiar to IntervalExerciseGrader for melodies */
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
        Scale scale;

        int melodyRange;
        int exerciseLength;
        int[] scaleSteps;
        int[] scaleStepsEasy = {1};
        int[] scaleStepsMedium = {1, 1, 1, 2};
        int[] scaleStepsHard = {1, 1, 1, 1, 1, 2, 2, 3};

        if (difficulty == Difficulty.EASY){

            melodyRange = 3;
            invertProbability = 0;
            scaleSteps = scaleStepsEasy;
            exerciseLength = 4;


        } else if(difficulty == Difficulty.MEDIUM){

            melodyRange = random.nextInt(3) +3;;
            invertProbability = 0.2;
            scaleSteps = scaleStepsMedium;
            exerciseLength = 8;


        } else{

            melodyRange = random.nextInt(3) +5;
            invertProbability = 0.3;
            scaleSteps = scaleStepsHard;
            exerciseLength = 8;


        }

        Note startingNote = Note.getRandom(Note.C4_INDEX - 12, 4.0, Note.NoteLength.CROTCHET);

        ArrayList<Note> exercise = new ArrayList<>();
        scale = new Scale(startingNote, Scale.ScaleType.MAJOR);
        boolean pastHalfWay = false; //only change if middle note has been reached
        //use past Halfway to decide the end of the scale
        int scalePointer = 1;
        exercise.add(scale.notes[0]);
        for (int i = 1; i < exerciseLength-1; i++){
            //adds mostly higher notes
            if (!pastHalfWay){
                if (i == exerciseLength-1){//final two scale notes
                    exercise.add(scale.notes[7]);
                    exercise.add(scale.notes[8]);

                } else {
                    int step = scaleSteps[random.nextInt(scaleSteps.length)];
                    if (random.nextDouble() > invertProbability || i == 1){
                        scalePointer += step;
                        exercise.add(scale.notes[scalePointer]);

                        if (scalePointer >melodyRange ){
                            pastHalfWay = true;
                            scalePointer = melodyRange;
                        }
                        exercise.add(scale.notes[scalePointer]);

                    } else {
                        scalePointer -=step;
                        if (scalePointer > 0){
                            scalePointer = 0;
                        }
                        exercise.add(scale.notes[scalePointer]);
                    }
                }


            } else if (pastHalfWay == true){ //adds mostly down
                if (i == exerciseLength-1){//final two scale notes
                    exercise.add(scale.notes[2]);
                    exercise.add(scale.notes[1]);

                } else {
                    int step = scaleSteps[random.nextInt(scaleSteps.length)];
                    if (random.nextDouble() > invertProbability || i == 1){
                        scalePointer -= step;
                        if (scalePointer > 0){
                            scalePointer = 0;
                        }
                        exercise.add(scale.notes[scalePointer]);

                    } else {
                        scalePointer +=step;
                        if (scalePointer >melodyRange ){
                            scalePointer = melodyRange;
                        }
                        exercise.add(scale.notes[scalePointer]);
                    }
                }


            }


        }

        return exercise;

    }
}
