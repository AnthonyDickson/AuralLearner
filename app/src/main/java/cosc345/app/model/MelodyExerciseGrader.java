package cosc345.app.model;

import java.util.ArrayList;

import java.util.Random;

public class MelodyExerciseGrader extends Grader {
    private Random random = new Random();
    public Melody melody;

    public MelodyExerciseGrader(Difficulty difficulty) {
        super();

        int melodyRange;
        int melodyLength;
        int maxStep;
        double reverseProbability;
        Scale.ScaleType[] scaleTypes;

        if (difficulty == Difficulty.EASY) {
            melodyRange = 3;
            melodyLength = 4;
            reverseProbability = 0;
            maxStep = 1;
            scaleTypes = ScaleExerciseGrader.EASY_SCALES;
        } else if (difficulty == Difficulty.MEDIUM) {
            melodyRange = random.nextInt(3) + 3;
            reverseProbability = 0.2;
            melodyLength = 8;
            maxStep = 2;
            scaleTypes = ScaleExerciseGrader.MEDIUM_SCALES;
        } else {
            melodyRange = random.nextInt(3) + 5;
            melodyLength = 8;
            reverseProbability = 0.3;
            maxStep = 3;
            scaleTypes = ScaleExerciseGrader.HARD_SCALES;
        }

        Note startingNote = Note.getRandom(Note.C4_INDEX - 12, 4.0, Note.NoteLength.CROTCHET);
        Scale.ScaleType scaleType = scaleTypes[Utilities.random.nextInt(scaleTypes.length)];
        Scale scale = new Scale(startingNote, scaleType);
        melody = new Melody(scale, melodyLength, maxStep, melodyRange, reverseProbability);

        super.notes = melody.notes;
    }
}
