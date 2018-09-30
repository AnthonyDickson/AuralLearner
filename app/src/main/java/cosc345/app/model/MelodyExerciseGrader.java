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

        if (difficulty == Difficulty.EASY) {
            melodyRange = 3;
            melodyLength = 4;
            reverseProbability = 0;
            maxStep = 3;
        } else if (difficulty == Difficulty.MEDIUM) {
            melodyRange = random.nextInt(3) + 3;
            reverseProbability = 0.2;
            melodyLength = 8;
            maxStep = 3;
        } else {
            melodyRange = random.nextInt(3) + 5;
            melodyLength = 8;
            reverseProbability = 0.3;
            maxStep = 3;
        }

        Note startingNote = Note.getRandom(Note.C4_INDEX - 12, 4.0, Note.NoteLength.CROTCHET);
        Scale scale = new Scale(startingNote, Scale.ScaleType.MAJOR, Note.NoteLength.MINIM);
        melody = new Melody(scale, melodyLength, maxStep, melodyRange, reverseProbability);

        super.notes = melody.notes;
    }
}
